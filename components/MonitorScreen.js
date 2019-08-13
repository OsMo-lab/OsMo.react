import * as React from 'react';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Text, View, Image, Platform, StatusBar, NativeModules } from 'react-native';




export default class MonitorScreen extends React.Component {
  TrackerClick() {
    const {OsMoEventEmitter} = NativeModules;
    if (this.props.screenProps.appState.tracker.state == 'stop'){
      OsMoEventEmitter.startSendingCoordinates(false);
    } else {
      OsMoEventEmitter.stopSendingCoordinates();
    }
    console.log('tracker clicked');

  }
  getStateIconName (){
    if (this.props.screenProps.appState.tracker.state == 'stop') {
      return 'ios-play';
    } else {
      return 'ios-square';
    }
  }
  render() {
    return (
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight,flex: 1, backgroundColor: 'black',paddingLeft:5}}>
        <View style={{flexDirection: 'row',justifyContent: 'space-between'}}>
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
          <Text style={{justifyContent: 'center',color:'dodgerblue', fontSize:18}}>{this.props.screenProps.appState.tracker.id!=''?this.props.screenProps.appState.tracker.id:'Press Start Button'}</Text>
        </View>
        <View style={{flexDirection: 'row'}}>
          <Text style={{color:'lightgray',fontSize:20}}>TrackerID:</Text>
          <Text style={{color:'lightgray',fontSize:20}}>{this.props.screenProps.appState.device}</Text>
        </View>
        <View style={{flexDirection: 'row', margin: 5 }}>
          <Ionicons name={this.getStateIconName()} size={140} color='#FB671E' onPress={() => this.TrackerClick()}/>
          <Ionicons style={{paddingLeft:20,alignSelf: "flex-end"}} name='ios-pause' size={80} color='#FB671E' />
        </View>
        <View style={{flexDirection: 'row'}}>
          <Text style={{color:'white'}}>{this.props.screenProps.appState.motd}</Text>
        </View>
      </View>
    );
  }
}