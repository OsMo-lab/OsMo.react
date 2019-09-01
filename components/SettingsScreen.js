import * as React from 'react';
import { Text, View, Button, Platform, StatusBar } from 'react-native';

export default class SettingsScreen extends React.Component {
  render() {
    return (
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight, flex: 1,backgroundColor: 'black',paddingLeft:5 }}>
        <Text  style={{color:'#FB671E',fontSize:20}}>Settings</Text> 

          <Button
    onPress={() => this.ResetClick()}
    title="Reset authorization"
    color="#FF0000"
  />
      </View>
    );
  }

  ResetClick() {
    console.log('ResetClick');
    return this.props.screenProps.onResetAuthorization();
  }
}