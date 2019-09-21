import * as React from 'react';
import { Text, View, Platform, StatusBar} from 'react-native';


export default class MapScreen extends React.Component {
  render() {    
    return (      

      <View style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight,flex: 1}}   
        region={{latitude: 59,longitude: 53,latitudeDelta: 0.0922,longitudeDelta: 0.0421}}
        provider={null}
        mapType="none"
        showsUserLocation={true}     
        showsCompass={true} 
      >    
      
    </View>

    );  
  }
}
