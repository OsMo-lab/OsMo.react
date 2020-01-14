import * as React from 'react';
import { Text, View, FlatList,Platform, StatusBar} from 'react-native';
import {SafeAreaView} from 'react-navigation';
import Ionicons from 'react-native-vector-icons/Ionicons';
import FlatListItemSeparator from './FlatListSeparator';

export default class AccountScreen extends React.Component {
  render() {
    return (
      <SafeAreaView style={{ backgroundColor: 'black', flex: 1 }}>
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight, flex: 1, backgroundColor: 'black' }}>
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
          <Text  style={{color:'#FB671E',fontSize:20}}>Account</Text> 
          <Text style={{color:'gray'}}>{this.props.screenProps.appState.userNick}</Text>
        </View>
        <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
          <Ionicons name='ios-contact' size={80} 
          color={this.props.screenProps.appState.userNick=='unknown'?'gray':'#FB671E'} 
          />
          
          {this.props.screenProps.appState.userNick=='unknown' ? 
            <Ionicons style={{alignSelf: "center"}} name="ios-log-in" size={40} color='#FB671E' onPress={() => this.props.navigation.push('SignIn')}/>
          : null }
          <Ionicons style={{alignSelf: "center"}} name='ios-close-circle' size={40} color='red' onPress={() => this.ResetClick()}/>
          
         </View>
         {this.props.screenProps.appState.userNick!='unknown' ? 
         <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
          <Ionicons name='ios-people' size={40} color='#FB671E' />
          <Text style={{color:'#FB671E'}}>Groups</Text>
          <Text style={{color:'#FB671E'}}>Create</Text>
          <Text style={{color:'#FB671E'}}>Enter</Text>
        </View>
        : null }
        <FlatList 
        style={{backgroundColor: 'black'}}
        ItemSeparatorComponent={FlatListItemSeparator}
        data={this.props.screenProps.appState.groups}
        renderItem={({item}) => <Text style={{height:100,color:'white'}}>{item.name}</Text>}
        keyExtractor={(item, index) => index.toString()}
      />
      </View>
      </SafeAreaView>
    );
  }


  ResetClick() {
    return this.props.screenProps.onResetAuthorization();
  }
}