import * as React from 'react';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Text, View, NativeModules,NativeEventEmitter, PermissionsAndroid, Alert } from 'react-native';
import { createBottomTabNavigator, createAppContainer, createStackNavigator } from 'react-navigation';
import AsyncStorage from '@react-native-community/async-storage';
import AccountScreen from './components/AccountScreen';
import HistoryScreen from './components/HistoryScreen';
import MonitorScreen from './components/MonitorScreen';
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
    
    if (granted === PermissionsAndroid.RESULTS.GRANTED && PermissionsAndroid.PERMISSIONS.ACCESS_BACKGROUND_LOCATION)  {
        PermissionsAndroid.request(
            PermissionsAndroid.PERMISSIONS.ACCESS_BACKGROUND_LOCATION,
            {
              'title': 'OsMo.mobi Background Location Permission',
              'message': 'OsMo.Mobi App needs access to track your location while in background'
            }
        )
      
    }
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
            timer:null,
            tracker: {
                distance: 0,
                speed: 0,
                time: 0,
                state: 'stop',
                id: '',
                start: 0,
            },
            log: [],
            groups: [],
            history:[],
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

        const {OsMoEventEmitter} = NativeModules;
        
        this.eventEmitter = new NativeEventEmitter(OsMoEventEmitter);
        
       
        onMessageReceived = this.eventEmitter.addListener("onMessageReceived",
        res => {
            if (res.location) {
                let resp = JSON.parse(res.location);
                if (resp.command) {
                    OsMoEventEmitter.processLocation(resp.command);
                }
                if (resp.data) {
                    console.log(resp.data);
                    let tracker = this.state.tracker;
                    if (resp.data.distance) {
                        tracker.distance = resp.data.distance.toFixed(2);
                    }
                    if (resp.data.speed) {
                        tracker.speed = resp.data.speed.toFixed(2);
                    }
                    this.setState({tracker:tracker});
                }
                return;
            }
            this.state.log.push({message:JSON.stringify(res)});
            //Получены TCP координаты сервера 
            if (res.server) {
                let resp = JSON.parse(res.server);
                console.log(resp.address);
                this.setState({address:resp.address});
                return;
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
            
            //Сообщение от сервера
            if (res.message) {
                let command = res.message.split('|');
                if (command.length == 2) {
                    if (command[0] == 'AUTH' ) {
                        let resp = JSON.parse(command[1]);
                        //Ошибка при соединенеии
                        if (resp.error == 10 || resp.error == 100) {
                            this.clearKeys();
                        }
                    
                        this.storeData('trackerId', resp.id);
                        this.setState({trackerId: resp.id});
                        
                        global.config.authed = true;
                        OsMoEventEmitter.configure(JSON.stringify(global.config));
                        if (resp.uid > 0) {
                            this.setState({userNick : resp.name});
                            this.getGroups();
                        }
                        this.setState({permanent : resp.permanent});
                        if (this.state.motdtime < resp.motd) {
                            this.storeData('motdtime', String(resp.motd));
                            this.setState({motdtime : resp.motd});
                            OsMoEventEmitter.sendMessage("MD");
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
                        let resp = JSON.parse(command[1]);
                        if (resp.error) {
                            OsMoEventEmitter.sendMessage('PUSH|bla-bla-bla');
                        } else {
                            global.config.sessionStarted = true;
                            OsMoEventEmitter.configure(JSON.stringify(global.config));
                            this.setState({tracker:{state:'run',id:resp.url, distance:0,time:0,speed:0, start:Date.now()}});
                            //OsMoEventEmitter.startService();
                        }
                        return;
                    }
                    if (command[0] == 'TC') {
                        global.config.sessionStarted = false;
                        OsMoEventEmitter.configure(JSON.stringify(global.config));
                        
                        let resp = JSON.parse(command[1]);
                        let tracker = this.state.tracker;
                        tracker.state = 'stop';
                        tracker.id='';
                        this.setState({tracker:tracker});
                        return;
                    }
                    if (command[0] == 'GROUP') {
                        let resp = JSON.parse(command[1]);
                        this.setState({groups:resp});
                        return;
                    }
                    if (command[0] == 'HISTORY') {
                        let resp = JSON.parse(command[1]);
                        this.setState({history:resp});
                        return;
                    }
                } else {
                    if (command[0] == 'PP') {
                        OsMoEventEmitter.sendMessage('P');
                        return;
                    }
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
        if (this.state.timer) {
            clearInterval(this.state.timer);
        }
        this.eventEmitter.remove();
    }

    render() {
        let props = {
            appState: this.state,
            onResetAuthorization: () => this.onResetAuthorization(), 
            onRequestHistory: () => this.onRequestHistory(), 
        }; 
        props.onUserAuthorize = this.onUserAuthorize.bind(this); 
        props.onStateChanged = this.onStateChanged.bind(this); 
        return <AppContainer screenProps = {props}/>;
    }

    tick =() => {
        let tracker = this.state.tracker;
        tracker.time = (Date.now() - tracker.start) / 1000;
        this.setState({tracker:tracker});
    }

    async storeData (name,value){
        try {
            await AsyncStorage.setItem(name, value)
        } catch (e) {
        // saving error
        }
    }

    clearKeys() {
        console.log('clearKeys');    
        global.config.device = "";
        
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
        let tracker = this.state.tracker;
        tracker.state = 'stop';
        tracker.id='';
        tracker.speed = 0;
        tracker.time = 0;
        tracker.distance = 0;
        this.setState({trackerId:'',motd:'', motdtime:0,userNick:'unknown',tracker:tracker,groups:[], history:[]});
        
        const {OsMoEventEmitter} = NativeModules;
        OsMoEventEmitter.configure(JSON.stringify(global.config));
        OsMoEventEmitter.connect();
    }

    getGroups() {
        const {OsMoEventEmitter} = NativeModules;
        OsMoEventEmitter.sendMessage('GROUP');
    }


    onRequestHistory() {
        const {OsMoEventEmitter} = NativeModules;
        OsMoEventEmitter.sendMessage('HISTORY');
    }

    onUserAuthorize(nick) {
        console.log(nick);
        this.setState({userNick:nick});
        if(nick) {
            this.getGroups();
        }
    }
    
    onResetAuthorization() {
        if (this.state.tracker.state == 'stop'){
            this.clearKeys();
        }
    }

    onStateChanged(new_state) {
        let tracker = this.state.tracker;
        tracker.state = new_state;
        var timer = null;
        if (new_state == 'run') {
            timer = setInterval(this.tick, 1000);
            this.setState({timer});
        } else {
            clearInterval(this.state.timer);
        } 

        this.setState({tracker:tracker, timer:timer});
    }
}

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

const AppNavigator = createBottomTabNavigator({
    Monitor: { screen: MonitorScreen },
    Account: { screen: SinInStack },
    Map: { screen: MapScreen },
    History: { screen: HistoryScreen },
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