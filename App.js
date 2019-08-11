import * as React from 'react';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Text, View, NativeModules,NativeEventEmitter } from 'react-native';

// instantiate the event emitter

const process = require('process');
import { createBottomTabNavigator, createAppContainer } from 'react-navigation';


// You can import from local files
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

import DeviceInfo from 'react-native-device-info';

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
        this.connectionManager;
        this.onAUTH;
        this.onMOTDUpdate;
    }

  /*  
    connectToServer(address) {
        //process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = '0';
  
        let srv = address.split(':');
        let config = {
            address: srv[0], //ip address of server
            port: srv[1], //port of socket server
            reconnect: true, //OPTIONAL (default false): auto-reconnect on lost server
            reconnectDelay: 500, //OPTIONAL (default 500ms): how often to try to auto-reconnect
            maxReconnectAttempts: 10, //OPTIONAL (default infinity): how many time to attemp to auto-reconnect

        }
    }

    getServerInfo(key) {
        return fetch(servUrl, {
                method: 'POST',
                headers: {
                    Accept: 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                },
                body: 'app=' + OsmoAppKey,
            })
            .then((response) => response.json())
            .then((responseJson) => {
                this.state.log.push({ message: JSON.stringify(responseJson) });

                this.setState({
                    isLoading: false,
                }, function() {
                    this.state.log.push({ message: 'Received server info' });
                    this.connectToServer(responseJson.address);
                });
            })
            .catch((error) => {
                console.error(error);
            });
    }
*/
    componentDidMount() {

        this.state.groups.push({ name: 'Group 1', uid: 1 });
        this.state.groups.push({ name: 'Group 2', uid: 2 });

        const {ConnectionManager} = NativeModules;
        //const {SendingManager} = NativeModules;
        
        this.connectionManager = new NativeEventEmitter(ConnectionManager);
        //this.sendingManager = new NativeEventEmitter(SendingManager);


        onMessageReceived = this.connectionManager.addListener("onMessageReceived",
        res => {
            this.state.log.push({message:JSON.stringify(res)});
            let output = res.message;
            let command = output.split('|');
            if (command.length == 2) {
                if (command[0] == 'AUTH' ) {
                    let auth = JSON.parse(command[1]);
                    this.setState({device: auth.id});
                    if (auth.uid > 0) {
                        this.setState({userNick : auth.name});
                    }
                    this.setState({permanent : auth.permanent});
                    if (this.state.motdtime < auth.motd) {
                        this.setState({motdtime : auth.motd});
                        ConnectionManager.getMessageOfTheDay();
                    }    
                    this.state.log.push({message:JSON.stringify(res)});
                    return;
                }
                if (command[0] == 'MD') {
                    this.setState({motd : command[1]});
                    return;
                }
            }
        }
        );  
         
        //ConnectionManager.connect();
    }

    componentWillUnmount() {
        this.connectionManager.remove();
        //onMOTDUpdate.remove();
        //onAUTH.remove();
    }

    render() {
        return <AppContainer screenProps = {
            { appState: this.state }
        }
        />;
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