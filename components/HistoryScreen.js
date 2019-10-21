import * as React from 'react';
import { Text, View, FlatList, Platform, StatusBar } from 'react-native';
import FlatListItemSeparator from './FlatListSeparator';
import {SafeAreaView} from 'react-navigation';

export default class HistoryScreen extends React.Component {
  render() {
    return (
      <SafeAreaView style={{ backgroundColor: 'black', flex: 1 }}>
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight, flex: 1,backgroundColor: 'black',paddingLeft:5 }}>
        <Text  style={{color:'#FB671E',fontSize:20}}>History</Text> 
        
        <FlatList 
        ItemSeparatorComponent={FlatListItemSeparator}
        data={[{key: 'Trip 1'}, {key: 'Trip 2'}, {key: 'Trip 3'}, {key: 'Trip 4'}, {key: 'Trip 5'}, {key: 'Trip 6'}]}
        renderItem={({item}) => <Text style={{height:150,color:'white'}}>{item.key}</Text>}
        />
      </View> 
      </SafeAreaView>
    );
  }
}
