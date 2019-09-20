import * as React from 'react';
import { Text, View, FlatList,Platform, StatusBar} from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';

export default class SignInScreen extends React.Component {
  render() {
    return (
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight, flex: 1, backgroundColor: 'black' }}>
        
        
        <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
            <Text  style={{color:'#FB671E',fontSize:20}}>Sign-In</Text> 
            <Text  style={{color:'#FB671E',fontSize:20}}>Register</Text> 
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
            <Text  style={{color:'#FB671E',fontSize:20}}>E-mail</Text> 
        </View>
        
        <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
            <Text  style={{color:'#FB671E',fontSize:20}}>Password</Text> 
        </View>


        <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
            <Button
                onPress={() => this.CancelClick()}
                title="Sign-In"
                color="#FF0000"
            />
            <Button
                onPress={() => this.CancelClick()}
                title="Cancel"
                color="#FF0000"
            />
        </View>
      </View>
    );
  }


  CancelClick() {
    console.log('CancelClick');
  }
}