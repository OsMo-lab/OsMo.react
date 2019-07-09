import * as React from 'react';
import { Text, View, FlatList, Platform, StatusBar } from 'react-native';

export default class SettingsScreen extends React.Component {
  render() {
    return (
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight, flex: 1,backgroundColor: 'black',paddingLeft:5 }}>
        <Text  style={{color:'#FB671E',fontSize:20}}>Settings</Text> 
      </View>
    );
  }
}