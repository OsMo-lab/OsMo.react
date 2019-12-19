import * as React from 'react';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Autolink from 'react-native-autolink';
import {Text, View, Image, Platform, StatusBar, NativeModules, Alert, Clipboard, Linking } from 'react-native';
import {SafeAreaView} from 'react-navigation';
const {OsMoEventEmitter} = NativeModules;
  
export default class MonitorScreen extends React.Component {
  TrackerClick() {
    if (this.props.screenProps.appState.tracker.state == 'stop'){
      OsMoEventEmitter.startSendingCoordinates(false);
    } else {
      OsMoEventEmitter.stopSendingCoordinates();
    }
  }

  TrackerUrlClick() {
    if (this.props.screenProps.appState.tracker.id!='') {
      let url = 'https://osmo.mobi/s/' + this.props.screenProps.appState.tracker.id;
      Alert.alert(
        'Alert Title',
        'Track URL',
        [
          {text: 'Copy to clipboard', onPress: () => Clipboard.setString(url)},
          
          {text: 'Open URL', onPress: () => {
            Linking.canOpenURL(url)
                .then((supported) => {
                  if (!supported) {
                    console.log("Can't handle url: " + url);
                  } else {
                    return Linking.openURL(url);
                  }
                })
                .catch((err) => console.error('An error occurred', err))
            }
          },
          {
            text: 'Cancel',
            onPress: () => console.log('Cancel Pressed'),
            style: 'cancel',
          },
        ],
        {cancelable: false},
      );

    }
    
  }

  TrackerPauseClick() {
    if (this.props.screenProps.appState.tracker.state == 'run'){
      OsMoEventEmitter.pauseSendingCoordinates();
    } else {
      OsMoEventEmitter.stopSendingCoordinates();
    }
  }
  
  getStateIconName (){
    if (this.props.screenProps.appState.tracker.state == 'stop') {
      return 'ios-play';
    } else {
      return 'ios-square';
    }
  }

  getPauseIconName (){
    if (this.props.screenProps.appState.tracker.state == 'run') {
      return 'ios-pause';
    } else {
      return 'ios-play';
    }
  }
  render() {
    return (
      <SafeAreaView style={{ backgroundColor: 'black', flex: 1 }}>
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight,flex: 1, backgroundColor: 'black',paddingLeft:5}}>
        <View style={{flexDirection: 'row',justifyContent: 'space-between',backgroundColor:this.props.screenProps.appState.trackerId!='' ? 'black' : (this.props.screenProps.appState.address!='' ? 'yellow' : 'red') }}>
        <Image
          style={{width: 120, height: 43}}
          source={{uri: 'https://osmo.mobi/des/120.png'}}
         />
         <Text style={{textAlign: 'right',color:'gray', fontSize:16}}>{this.props.screenProps.appState.userNick}</Text>
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
          <Text style={{justifyContent: 'center',color:'white', fontSize:18}}>Time</Text>
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
          <Text style={{justifyContent: 'center',color:'white',fontSize:32}}>{this.props.screenProps.appState.tracker.time}</Text>
        </View>
           
        <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
          <Text style={{color: '#FB671E',fontSize:18}}>Distance</Text>
          <Text style={{color: '#FB671E',fontSize:18}}>Speed</Text>
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
          <Text style={{color:'#FB671E',fontSize:32}}>{this.props.screenProps.appState.tracker.distance}</Text>
          <Text style={{color:'#FB671E',fontSize:32}}>{this.props.screenProps.appState.tracker.speed}</Text>
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
          <Text style={{justifyContent: 'center',color:'dodgerblue', fontSize:18}} onPress={() => this.TrackerUrlClick()}>{this.props.screenProps.appState.tracker.id!=''?this.props.screenProps.appState.tracker.id:'Press Start Button'}</Text>
        </View>
        <View style={{flexDirection: 'row'}}>
          <Text style={{color:'lightgray',fontSize:20}}>TrackerID:</Text>
          <Text style={{color:'lightgray',fontSize:20}}>{this.props.screenProps.appState.trackerId}</Text>
        </View>
        <View style={{flexDirection: 'row', margin: 5 }}>
          <Ionicons name={this.getStateIconName()} size={140} color='#FB671E' backgroundColor="green" onPress={() => this.TrackerClick()}/>
          {this.props.screenProps.appState.tracker.state == 'run' ? <Ionicons backgroundColor="white" style={{paddingLeft:20,paddingBottom:20,alignSelf: "flex-end"}} name={this.getPauseIconName()} size={80} color='#FB671E' onPress={() => this.TrackerPauseClick()}/> : null}
          
        </View>
        <View style={{flexDirection: 'row'}}>
          <Autolink style={{color:'white'}} text={this.props.screenProps.appState.motd}></Autolink>
         
        </View>
      </View>
      </SafeAreaView>
    );
  }
}