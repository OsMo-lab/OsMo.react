//
//  History.swift
//  iOsMo
//
//  Created by Alexey Sirotkin on 22.05.2019.
//  Copyright © 2019 Alexey Sirotkin. All rights reserved.
//

import Foundation

//
//  History.swift
//  iOsMo
//
//  Created by Alexey Sirotkin on 16.04.17.
//  Copyright © 2017 Alexey Sirotkin. All rights reserved.
//

import Foundation
import MapKit

open class History: Equatable {
    var u: Int
    var uid: Int = 0
    var distantion: Double = 0

    var name: String = ""
    var url: String = ""
    var image: String = ""
    var gpx_optimal: String = ""
    var gpx_full: String = ""
 
    var start: Date?
    var end: Date?
    
    init (json: Dictionary<String, AnyObject>) {
        //print(json)
        self.u = json["u"] as? Int ?? 0
        if (self.u == 0) {
            self.u = Int(json["u"] as? String ?? "0")!
        }
        self.uid = Int(json["uid"] as? String ?? "0")!
        self.name = json["name"] as? String ?? ""
        //self.distantion = json["distantion"] as? Double ?? 0.0
        if let d = json["distantion"] as? String {
            let nf = NumberFormatter()
            nf.decimalSeparator="."
            self.distantion = (nf.number(from: d)?.doubleValue ?? 0.0)
 
        }
        self.url = json["url"] as? String ?? ""
        self.image = json["image"] as? String ?? ""
        self.gpx_optimal = json["gpx_optimal"] as? String ?? ""
        self.gpx_full = json["gpx_full"] as? String ?? ""
   
        self.start = Date(timeIntervalSince1970: (json["start"] as? Double) ?? 0)
        self.end = Date(timeIntervalSince1970: (json["end"] as? Double) ?? 0)
        
    }
    
    open func getGPXOptimalData() -> XML? {
        var paths = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true);
        let filename = "\(u).gpx"
        let path =  "\(paths[0])/history/gpx_optimal/"
        let file: FileHandle? = FileHandle(forReadingAtPath: "\(path)\(filename)")
        if file != nil {
            // Read all the data
            let data = file?.readDataToEndOfFile()
            
            // Close the file
            file?.closeFile()
            
            let xml = XML(data: data!)
            return xml;
        }
        else {
            return nil
        }
    }
    
    open func getGPXFullData() -> XML? {
        var paths = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true);
        let filename = "\(u).gpx"
        let path =  "\(paths[0])/history/gpx_full/"
        let file: FileHandle? = FileHandle(forReadingAtPath: "\(path)\(filename)")
        if file != nil {
            // Read all the data
            let data = file?.readDataToEndOfFile()
            
            // Close the file
            file?.closeFile()
            
            let xml = XML(data: data!)
            return xml;
        }
        else {
            return nil
        }
    }
    
    public static func == (left: History, right: History) -> Bool {
        return left.u == right.u
    }
}
