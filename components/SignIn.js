import * as React from 'react';
import { Text, View, Button,Platform, StatusBar, TextInput} from 'react-native';

export default class SignInScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            newUser: false
        }
    }
  render() {
    
    return (
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight, flex: 1, backgroundColor: 'black' }}>
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            <Text  style={{color:'#FB671E',fontSize:20}}>Sign-In</Text> 
            <Button style={{color:'dodgerblue',fontSize:20}}
                onPress={() => this.RegisterClick()}
                title={this.state.newUser ? 'Sign-In' : 'Register'}
            ></Button> 
        </View>
        <View>
            <Text  style={{color:'#FB671E',fontSize:20}}>E-mail</Text> 
        </View>
        <View >
            <TextInput placeholder="user@site.com" style={{borderRadius: 10 ,color:'black',backgroundColor:'#ffd479',fontSize:18}}></TextInput> 
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            <Text  style={{color:'#FB671E',fontSize:20}}>Password</Text> 
            {!this.state.newUser ? (
            <Text  style={{color:'dodgerblue',fontSize:20}}>Forgot?</Text> 
            ) : null }
        </View>
        <View >
            <TextInput secureTextEntry={true} textContentType="password" style={{borderRadius: 10 ,color:'black',backgroundColor:'#ffd479',fontSize:18}}></TextInput> 
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            {this.state.newUser ? (
            <Text  style={{color:'#FB671E',fontSize:20}}>Confirm password</Text> 
            ) : null }
        </View>
        <View >
            {this.state.newUser ? (
            <TextInput secureTextEntry={true} textContentType="password" style={{borderRadius: 10 ,color:'black',backgroundColor:'#ffd479',fontSize:18}}></TextInput> 
            ) : null }
        </View>
        
        
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            <Button
                onPress={() => this.CancelClick()}
                title="Sign-In"
                color="#FB671E"
                style={{paddingLeft:20}}
            />
            <Button
                onPress={() => this.CancelClick()}
                title="Cancel"
                color="#FB671E"
                style={{paddingRight:20}}
            />
        </View>
      </View>
    );
  }

  RegisterClick() {
    this.setState({newUser: !this.state.newUser});
  }

  CancelClick() {
    console.log('CancelClick');
  }
}