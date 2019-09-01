import * as React from 'react';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Text, View, NativeModules,NativeEventEmitter } from 'react-native';
import { createBottomTabNavigator, createAppContainer } from 'react-navigation';
import AsyncStorage from '@react-native-community/async-storage';
import AccountScreen from './components/AccountScreen';
import HistoryScreen from './components/HistoryScreen';
import MonitorScreen from './components/MonitorScreen';
import SettingsScreen from './components/SettingsScreen';
import MapScreen from './components/MapScreen';
import LogScreen from './components/LogScreen';

const authUrl = "https://api.osmo.mobi/new?"
const servUrl = "https://api.osmo.mobi/serv?" // to get server info
const apiUrl = "https://api.osmo.mobi/iProx?"
const OsmoAppKey = "Jdf43G_fVl3Opa42"


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

export default class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isLoading: true,
            tracker: {
                distance: 0,
                speed: 0,
                time: 0,
                state: 'stop',
                id: '',
            },
            log: [],
            groups: [],
            device: '',
            userNick: 'unknown',
            motd: 'Welcome to OsMo',
            motdtime:0,
            permanent: 0,
        }
        this.eventEmitter;
        this.onAUTH;
        this.onMOTDUpdate;
    }

    componentDidMount() {

        this.state.groups.push({ name: 'Group 1', uid: 1 });
        this.state.groups.push({ name: 'Group 2', uid: 2 });

        const {OsMoEventEmitter} = NativeModules;
        
        this.eventEmitter = new NativeEventEmitter(OsMoEventEmitter);
       
        onMessageReceived = this.eventEmitter.addListener("onMessageReceived",
        res => {
            this.state.log.push({message:JSON.stringify(res)});
            let output = res.message;
            let command = output.split('|');
            if (command.length == 2) {
                if (command[0] == 'AUTH' ) {
                    let resp = JSON.parse(command[1]);
                    storeData = async () => {
                        try {
                          await AsyncStorage.setItem('device', resp.id)
                        } catch (e) {
                          // saving error
                        }
                    }
                    this.setState({device: resp.id});
                    if (resp.uid > 0) {
                        this.setState({userNick : resp.name});
                    }
                    this.setState({permanent : resp.permanent});
                    if (this.state.motdtime < resp.motd) {
                        storeData = async () => {
                            try {
                              await AsyncStorage.setItem('motdtime', resp.motd)
                            } catch (e) {
                              // saving error
                            }
                        }
                        this.setState({motdtime : resp.motd});
                        OsMoEventEmitter.getMessageOfTheDay();
                    }    
                    this.state.log.push({message:JSON.stringify(res)});
                    return;
                }
                if (command[0] == 'MD') {
                    let st = Object.assign(this.state,{motd : command[1]});
                    this.setState(st);
                    return;
                }
                if (command[0] == 'TO') {
                    let resp = JSON.parse(command[1]);
                    this.setState({tracker:{state:'run',id:resp.url, distance:0,time:0,speed:0}});
                    return;
                }
                if (command[0] == 'TC') {
                    let resp = JSON.parse(command[1]);
                    let tracker = Object.assign(this.state.tracker,{state:'stop',id:''} );
                    this.setState({tracker:tracker});
                    return;
                }

            }
        }
        );  
        
        getData = async () => {
            try {
              const value = await AsyncStorage.getItem('device')
              if(value !== null) {
                this.setState({device: value});
              }
            } catch(e) {
              // error reading value
            }
        }
        getData = async () => {
            try {
              const value = await AsyncStorage.getItem('motdtime')
              if(value !== null) {
                this.setState({motdtime: value});
              }
            } catch(e) {
              // error reading value
            }
        }
        OsMoEventEmitter.connect(this.state.device);
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

    
    clearKeys() {
        console.log('clearKeys');    
        
        
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
        this.setState({device:'',motd:'', motdtime:0,userNick:'unknown',tracker:tracker});
        
    }

    
    onResetAuthorization() {
        console.log(this.state);
        if (this.state.tracker.state == 'stop'){
            this.clearKeys();
        }
    }
    
}

const AppNavigator = createBottomTabNavigator({
    Monitor: { screen: MonitorScreen },
    Account: { screen: AccountScreen },
    Map: { screen: MapScreen },
    History: { screen: HistoryScreen },
    Settings: { screen: SettingsScreen },
    Log: { screen: LogScreen },
}, {
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