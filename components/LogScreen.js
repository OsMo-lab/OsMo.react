import * as React from 'react';
import { Text, View, FlatList, Platform, StatusBar } from 'react-native';
import FlatListItemSeparator from './FlatListSeparator';

export default class LogScreen extends React.Component {
  render() {
    return (
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight, flex: 1,backgroundColor: 'black',paddingLeft:5 }}>
        <Text  style={{color:'#FB671E',fontSize:20}}>Log</Text> 
        <FlatList 
          ItemSeparatorComponent={FlatListItemSeparator}
          data={this.props.screenProps.appState.log}
          extraData={this.props.screenProps.appState.log.length}
          renderItem={({item}) => <Text style={{height:50,color:'white'}}>{item.message}</Text>}
        />
       
      </View>
       
    );
  }
}
