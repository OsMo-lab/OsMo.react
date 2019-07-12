import * as React from 'react';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Text, View, StyleSheet, FlatList, Platform, StatusBar } from 'react-native';
import MapView from 'react-native-maps';
import { UrlTile } from 'react-native-maps';
var net  = require('react-native-tcp');

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
        }
    }

    connectToServer(address) {
        let srv = address.split(':');
        let config = {
            address: srv[0], //ip address of server
            port: srv[1], //port of socket server
            reconnect: true, //OPTIONAL (default false): auto-reconnect on lost server
            reconnectDelay: 500, //OPTIONAL (default 500ms): how often to try to auto-reconnect
            maxReconnectAttempts: 10, //OPTIONAL (default infinity): how many time to attemp to auto-reconnect

        }
        var client = new net.Socket();
        
        let options = {
            host: srv[0],
            port: srv[1]
            };
        client.connect(options, () => {
            //client.write('get_data');
            
            this.state.log.push({message:'connecting!'});
            
          });
          
          client.on('ready', (data) => { 
            this.state.log.push('client ready');
          }
          );
          client.on('data', (data) => { 
            this.state.log.push('onData');
            this.state.log.push({message:data});
          }
          );
          client.on('connect', (data) => { 
            this.state.log.push('onConnect');
            client.write('AUTH|' + this.state.device);
          }
          );
          client.on('error', function(error) {
            this.state.log.push('onError');
            this.state.log.push({message:error});
          });
        /*
        this.state.log.push({message:'Opening WebSocket ' + address});
        var ws = new WebSocket('wss://' + address,['osmo']);

        ws.onopen = () => {
          ws.send('AUTH|' + this.state.device);
          this.state.log.push({message:'WS onopen!'});
        };

        ws.onmessage = (e) => {
          this.state.log.push({message:'WS onMessage!'});
          console.log(e.data);
        };

        ws.onerror = (e) => {
          this.state.log.push({message:'WS onError!' + e.message});
          console.log(e.message);
        };

        ws.onclose = (e) => {
          this.state.log.push({message:'WS onClose!' + e.reason});
          console.log(e.code, e.reason);
        };
        */
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

    componentDidMount() {
        let vendorKey = DeviceInfo.getDeviceId();
        let model = DeviceInfo.getDeviceName();
        let platform = model + ' ' + Platform.OS + ' ' + Platform.Version;

        let requestString = 'app=' + OsmoAppKey + '&id=' + vendorKey + '&imei=0&platform=' + platform;

        /*
        body = 
        JSON.stringify({
            app: OsmoAppKey,
            id: vendorKey,
            imei:0,
            platform:platform,
            })
        */
        this.state.groups.push({ name: 'Group 1', uid: 1 });
        this.state.groups.push({ name: 'Group 2', uid: 2 });
        this.state.log.push({ message: 'POST:' + requestString });
        return fetch(authUrl, {
                method: 'POST',
                headers: {
                    Accept: 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                },
                body: requestString
            })
            .then((response) => response.json())
            .then((responseJson) => {
                this.state.log.push({ message: JSON.stringify(responseJson) });

                this.setState({
                    isLoading: false,
                    motd: requestString,
                    device: responseJson.device,
                }, function() {
                    this.state.log.push({ message: 'Received device key rom server' });
                    this.getServerInfo(responseJson.device);
                });
            })
            .catch((error) => {
                console.error(error);
            });
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