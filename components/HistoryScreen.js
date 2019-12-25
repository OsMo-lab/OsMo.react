import * as React from 'react';
import { Text, View, FlatList, Platform, StatusBar, Image } from 'react-native';
import FlatListItemSeparator from './FlatListSeparator';
import {SafeAreaView} from 'react-navigation';

export default class HistoryScreen extends React.Component {
  constructor(props) {
    super(props);  
  }

  render() {
    return (
      <SafeAreaView style={{ backgroundColor: 'black', flex: 1 }}>
      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight, flex: 1,backgroundColor: 'black',paddingLeft:5 }}>
        <Text  style={{color:'#FB671E',fontSize:20}}>History</Text> 
        
        <FlatList 
        ItemSeparatorComponent={FlatListItemSeparator}
        data={this.props.screenProps.appState.history}
        renderItem={({item}) => 
          <View>
            <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            <Text style={{height:20,color:'white'}}>{item.name}</Text>
            <Text style={{height:20,color:'white'}}>{item.end}</Text>
            </View>
            
            
            <Image
            style={{width: 200, height: 200}}
            source={{uri: item.image}}></Image>
          </View>}
        keyExtractor={(item, index) => index.toString()}
        />
      </View> 
      </SafeAreaView>
    );
  }
  componentDidMount(){
    this.props.screenProps.onRequestHistory();
  }
}
