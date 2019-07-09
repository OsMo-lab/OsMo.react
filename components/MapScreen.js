import * as React from 'react';
import MapView from 'react-native-maps';
import {UrlTile, OverlayComponent} from 'react-native-maps'
import { Text, View, Platform, StatusBar} from 'react-native';


export default class MapScreen extends React.Component {
  render() {    
    return (      

      <MapView style={{paddingTop: Platform.OS === 'ios' ? 0 : StatusBar.currentHeight,flex: 1}}   
        region={{latitude: 59,longitude: 53,latitudeDelta: 0.0922,longitudeDelta: 0.0421}}
        provider={null}
        mapType="none"
        showsUserLocation={true}     
        showsCompass={true} 
      >    
      <UrlTile
    /**
     * The url template of the tile server. The patterns {x} {y} {z} will be replaced at runtime
     * For example, http://c.tile.openstreetmap.org/{z}/{x}/{y}.png
     */
    urlTemplate='https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png'
    /**
     * The maximum zoom level for this tile overlay. Corresponds to the maximumZ setting in
     * MKTileOverlay. iOS only.
     */
    maximumZ={19}
    
    /**
     * flipY allows tiles with inverted y coordinates (origin at bottom left of map)
     * to be used. Its default value is false.
     */
    flipY={false}
  />
    </MapView>

    );  
  }
}
