import * as React from 'react';
import { Text, View, Button,Platform, StatusBar, TextInput, Alert} from 'react-native';
import {SafeAreaView} from 'react-navigation';

export default class SignInScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            newUser: false,
            pass:"",
            pass2:"",
            email:"",
            nick:"",
        }
    }
  render() {
    
    return (
        <SafeAreaView style={{ backgroundColor: 'black', flex: 1 }}>
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
            <TextInput placeholder="user@site.com" style={{borderRadius: 10 ,color:'black',backgroundColor:'#ffd479',fontSize:18}} onChangeText={(text) => this.setState({email:text})}></TextInput> 
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            {this.state.newUser ? (
            <Text style={{color:'#FB671E',fontSize:20}}>Nick</Text> 
            ) : null }
        </View>
        <View >
            {this.state.newUser ? (
            <TextInput placeholder="Your nice nickname" onChangeText={(text) => this.setState({nick:text})} style={{borderRadius: 10 ,color:'black',backgroundColor:'#ffd479',fontSize:18}}></TextInput> 
            ) : null }
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            <Text style={{color:'#FB671E',fontSize:20}}>Password</Text> 
            {!this.state.newUser ? (
            <Text  style={{color:'dodgerblue',fontSize:20}}>Forgot?</Text> 
            ) : null }
        </View>
        <View >
            <TextInput autoCorrect={false} secureTextEntry={true} textContentType="password"  onChangeText={(text) => this.setState({pass:text})} style={{borderRadius: 10 ,color:'black',backgroundColor:'#ffd479',fontSize:18}}></TextInput> 
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            {this.state.newUser ? (
            <Text style={{color:'#FB671E',fontSize:20}}>Confirm password</Text> 
            ) : null }
        </View>
        <View >
            {this.state.newUser ? (
            <TextInput autoCorrect={false} secureTextEntry={true} textContentType="password"  onChangeText={(text) => this.setState({pass2:text})} style={{borderRadius: 10 ,color:'black',backgroundColor:'#ffd479',fontSize:18}}></TextInput> 
            ) : null }
        </View>
        
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            <Button
                onPress={() => this.ActionClick()}
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
      </SafeAreaView>
    );
  }

  /* Регистрация нового пользователя и авторизация существующего*/
  getMoviesFromApiAsync() {
    
  }

  ActionClick() {
    let url = this.state.newUser ? "https://api2.osmo.mobi/signup?" : "https://api2.osmo.mobi/signin?";

    if (this.state.newUser) {
        if (this.state.nick == "") {
            Alert.alert(
                'Register new user',
                'Enter Nick!',
                [
                  {text: 'Ok',},
                ]
            );
            return;
        }
        if (this.state.pass != this.state.pass2) {
            Alert.alert(
                'Register new user',
                'Passwords did not matches!',
                [
                  {text: 'Ok',},
                ]
            );
            return;
        }
    }
    
    var data = "key=" + encodeURIComponent(global.config.device) + "&email=" + encodeURIComponent(this.state.email) + "&password=" + encodeURIComponent(this.state.pass);
    if (this.state.newUser) {
        data = data  + "&nick=" + encodeURIComponent(this.state.nick);
    } 
    
    let options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
        },
        body:data
    };
    fetch(url,options)
      .then((response) => response.json())
      .then((responseJson) => {
        if (responseJson.auth) {
            this.props.screenProps.onUserAuthorize(responseJson.nick);
            this.props.navigation.pop();
        } else if (responseJson.error_description){
            Alert.alert(
                this.state.newUser ? 'Sign In Error' : 'Sign Up Error',
                responseJson.error_description,
                [
                  {text: 'Ok',},
                ]
            );
            return;
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }

  /* Переключение режима вход|регистрация */ 
  RegisterClick() {
    this.setState({newUser: !this.state.newUser});
  }
  
  CancelClick() {
    this.props.navigation.pop();
  }
}