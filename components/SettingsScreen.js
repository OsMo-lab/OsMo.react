import * as React from 'react';
import { Text, View, Button, Platform, StatusBar } from 'react-native';
import {SafeAreaView} from 'react-navigation';

export default class SettingsScreen extends React.Component {
  render() {
    return (
      <SafeAreaView style={{ backgroundColor: 'black', flex: 1 }}>
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight, flex: 1,backgroundColor: 'black',paddingLeft:5 }}>
        <Text  style={{color:'#FB671E',fontSize:20}}>Settings</Text> 

          <Button
    onPress={() => this.ResetClick()}
    title="Reset authorization"
    color="#FF0000"
  />
      </View>
      </SafeAreaView>
    );
  }

  ResetClick() {
    return this.props.screenProps.onResetAuthorization();
  }
}