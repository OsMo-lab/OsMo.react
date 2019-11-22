import * as React from 'react';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Text, View, NativeModules,NativeEventEmitter, PermissionsAndroid, Alert } from 'react-native';
import { createBottomTabNavigator, createAppContainer, createStackNavigator } from 'react-navigation';
import AsyncStorage from '@react-native-community/async-storage';
import AccountScreen from './components/AccountScreen';
import HistoryScreen from './components/HistoryScreen';
import MonitorScreen from './components/MonitorScreen';
import SettingsScreen from './components/SettingsScreen';
import MapScreen from './components/MapScreen';
import LogScreen from './components/LogScreen';
import SignInScreen from './components/SignIn';

class IconWithBadge extends React.Component {
    render() {
        const { name, badgeCount, color, size } = this.props;
        return ( <View style = {
                    { width: 24, height: 24, margin: 5 }
                } >
                <Ionicons name = { name }
                size = { size }
                color = { color }
                /> {
                badgeCount > 0 && ( <View style = {
                        {
                            position: 'absolute',
                            right: -6,
                            top: -3,
                            backgroundColor: 'red',
                            borderRadius: 6,
                            width: 12,
                            height: 12,
                            justifyContent: 'center',
                            alignItems: 'center'
                        }
                    } >
                    <Text style = {
                        { color: 'white', fontSize: 10, fontWeight: 'bold' }
                    } > { badgeCount } </Text>
                     </View >
                )
            } </View>
    );
}
}

export async function request_location_runtime_permission() {

  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      {
        'title': 'OsMo.mobi Location Permission',
        'message': 'OsMo.Mobi App needs access to your location '
      }
    )
    /*
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      Alert.alert("Location Permission Granted.");
    }
    else {
      Alert.alert("Location Permission Not Granted");
    }*/
  } catch (err) {
    console.warn(err)
  }
}

export default class App extends React.Component {
    constructor(props) {
        global.config = {
            authUrl: "https://api2.osmo.mobi/new?", // register new device
            servUrl:"https://api2.osmo.mobi/serv?", // to get server info
            apiUrl:"https://api2.osmo.mobi/iProx?", // send requests in background mode for iOS
            OsmoAppKey:Platform.OS === 'ios' ? "j4C32_f2bvK" : "dSA3dS-cF2Cj45",
            device:"",
            motd:"Welcome to OsMo",
            authed:false,
            sessionStarted:false,
        }
        super(props);
        this.state = {
            isLoading: true,
            address:'',
            tracker: {
                distance: 0,
                speed: 0,
                time: 0,
                state: 'stop',
                id: '',
            },
            log: [],
            groups: [],
            trackerId: '',
            userNick: 'unknown',
            motd: '',
            motdtime:0,
            permanent: 0,
        }
        this.eventEmitter;
        this.onAUTH;
        this.onMOTDUpdate;
    }

    async componentDidMount() {
        await request_location_runtime_permission();
        this.state.groups.push({ name: 'Group 1', uid: 1 });
        this.state.groups.push({ name: 'Group 2', uid: 2 });

        const {OsMoEventEmitter} = NativeModules;
        
        this.eventEmitter = new NativeEventEmitter(OsMoEventEmitter);
       
        onMessageReceived = this.eventEmitter.addListener("onMessageReceived",
        res => {
            console.log(res);
            this.state.log.push({message:JSON.stringify(res)});
            //Получены TCP координаты сервера 
            if (res.server) {
                let resp = JSON.parse(res.server);
                console.log(resp.address);
                this.setState({address:resp.address});
                return;
            }
            //Сообщение от сервера
            if (res.message) {
                let command = res.message.split('|');
                if (command.length == 2) {
                    if (command[0] == 'AUTH' ) {
                        let resp = JSON.parse(command[1]);
                        this.storeData('trackerId', resp.id);
                        this.setState({trackerId: resp.id});
                        
                        global.config.authed = true;
                        OsMoEventEmitter.configure(JSON.stringify(global.config));
                        if (resp.uid > 0) {
                            this.setState({userNick : resp.name});
                        }
                        this.setState({permanent : resp.permanent});
                        if (this.state.motdtime < resp.motd) {
                            this.storeData('motdtime', String(resp.motd));
                            this.setState({motdtime : resp.motd});
                            OsMoEventEmitter.getMessageOfTheDay();
                        }    
                        return;
                    }
                    if (command[0] == 'MD') {
                        global.config.motd = command[1];
                        let st = Object.assign(this.state,{motd : global.config.motd});
                        this.storeData('motd', global.config.motd);
                            
                        this.setState(st);
                        return;
                    }

                    if (command[0] == 'TO') {
                        global.config.sessionStarted = true;
                        OsMoEventEmitter.configure(JSON.stringify(global.config));
                        let resp = JSON.parse(command[1]);
                        if (resp.error) {
                            OsMoEventEmitter.sendMessage('PUSH|bla-bla-bla');

                        } else {
                            this.setState({tracker:{state:'run',id:resp.url, distance:0,time:0,speed:0}});
                        }
                        return;
                    }
                    if (command[0] == 'TC') {
                        global.config.sessionStarted = false;
                        OsMoEventEmitter.configure(JSON.stringify(global.config));
                        
                        let resp = JSON.parse(command[1]);
                        let tracker = Object.assign(this.state.tracker,{state:'stop',id:''} );
                        this.setState({tracker:tracker});
                        return;
                    }
                    
                } else {
                    if (command[0] == 'PP') {
                        OsMoEventEmitter.sendMessage('P');
                        return;
                    }
                } 
            }
            

            //Сообщение о полученном новом device
            if (res.newkey) {
                this.storeData('device', res.newkey);
                global.config.device = res.newkey;
                OsMoEventEmitter.configure(JSON.stringify(global.config));
                return;
            }
            //Ошибка при соединенеии
            if (res.error) {
                let resp = JSON.parse(res.error);
                if (resp.error == 10 || resp.error == 100) {
                    this.clearKeys();
                }
            }
        });  
        

        /*
        AsyncStorage.getItem('trackerId', (err, result) => {
            this.setState({trackerId: result});
            console.log('trackerId from config:' + result);
        });
        */
        AsyncStorage.getItem('motdtime', (err, result) => {
            this.setState({motdtime: result});
            console.log('motdtime from config:' + result);
        });
        AsyncStorage.getItem('motd', (err, result) => {
            global.config.motd = result;
            this.setState({motd: result});
        });
        AsyncStorage.getItem('device', (err, result) => {
            global.config.device = result;
            OsMoEventEmitter.configure(JSON.stringify(global.config));
            OsMoEventEmitter.connect()
        });
        
    }

    componentWillUnmount() {
        this.eventEmitter.remove();
    }

    render() {
        let props = {
            appState: this.state,
            onResetAuthorization: () => this.onResetAuthorization() 
        }; 
        return <AppContainer screenProps = {props}/>;
    }

    
    async storeData (name,value){
        try {
            await AsyncStorage.setItem(name, value)
        } catch (e) {
        // saving error
        }
    }

    clearKeys() {
        const {OsMoEventEmitter} = NativeModules;
        console.log('clearKeys');    
        global.device = "";
        
        this.state.log.push({message:'clearKeys'});
        removeValue = async () => {
            try {
              await AsyncStorage.removeItem('device')
            } catch(e) {
              // remove error
            }
        }
        
        removeValue = async () => {
            try {
              await AsyncStorage.removeItem('trackerid')
            } catch(e) {
              // remove error
            }
        }
        removeValue = async () => {
            try {
              await AsyncStorage.removeItem('user')
            } catch(e) {
              // remove error
            }
        }
        let tracker = Object.assign(this.state.tracker,{state:'stop',id:''} );
        this.setState({trackerId:'',motd:'', motdtime:0,userNick:'unknown',tracker:tracker});
        OsMoEventEmitter.configure(JSON.stringify(global.config));
        OsMoEventEmitter.connect();
    }
    
    onResetAuthorization() {
        if (this.state.tracker.state == 'stop'){
            this.clearKeys();
        }
    }
}
/*
const SinInStack = createStackNavigator(
    {
        Account: {
            screen: AccountScreen,
            navigationOptions: () => ({
                title: 'A',
                headerBackTitle: null,
                }),
        },
        SignIn: {
            screen: SignInScreen,
            navigationOptions: () => ({
                title: 'B',
                headerBackTitle: null,
                }),
        }
    },{
        headerMode: 'none',
        mode: 'modal',
        defaultNavigationOptions: {
          gesturesEnabled: false,
        },
    }
    
);
*/
const AppNavigator = createBottomTabNavigator({
    Monitor: { screen: MonitorScreen },
    Account: { screen: AccountScreen },
    //Account: { screen: SignInScreen },
    Map: { screen: MapScreen },
    History: { screen: HistoryScreen },
    Settings: { screen: SettingsScreen },
    Log: { screen: LogScreen },
}, {
    navigationOptions:{
          headerStyle:{
                backgroundColor: '#212121', // this will handle the cutOff at the top the screen
          },
          headerTitleStyle:{
                fontSize: 14,
                fontWeight: '800',
                textAlign: 'center',
                flex: 1, // to make a header centered to the screen
          } ,
        },
    defaultNavigationOptions: ({ navigation }) => ({
        tabBarIcon: ({ focused, horizontal, tintColor }) => {
            const { routeName } = navigation.state;
            let IconComponent = Ionicons;
            let iconName;
            if (routeName === 'Monitor') {
                iconName = `ios-navigate`;
            } else if (routeName === 'Settings') {
                iconName = `ios-settings`;
            } else if (routeName === 'Map') {
                iconName = `ios-map`;
            } else if (routeName === 'Account') {
                iconName = `ios-person`;
            } else if (routeName === 'History') {
                iconName = `ios-recording`;
            } else if (routeName === 'Log') {
                iconName = `ios-journal`;
            }

            return <IconComponent name = { iconName }
            size = { 32 }
            color = { tintColor }
            />;
        },
    }),
    tabBarOptions: {
        activeTintColor: '#FB671E',
        inactiveTintColor: 'gray',
        showLabel: false,
        style: {
            backgroundColor: 'black',
        },
    },
    
});

const AppContainer = createAppContainer(AppNavigator);