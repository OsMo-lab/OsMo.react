//
//  EventEmitter.swift
//  OsMo
//
//  Created by Alexey Sirotkin on 13.08.2019.
//  Copyright © 2019 OsMo. All rights reserved.
//

import Foundation

var hasListeners : Bool = false;

@objc (OsMoEventEmitter)
class OsMoEventEmitter: RCTEventEmitter{
  let connectionManager =  ConnectionManager.sharedConnectionManager;
  let sendingManger = SendingManager.sharedSendingManager;
  
  // we need to override this method and
  // return an array of event names that we can listen to
  @objc class var sharedOsMoEventEmitter : OsMoEventEmitter{
    struct Static {
      static let instance: OsMoEventEmitter = OsMoEventEmitter()
    }
    return Static.instance
  }
  
  override init() {
    super.init()
    _ = connectionManager.onMessageReceived.add{
      if (hasListeners) {
        self.sendEvent(withName: "onMessageReceived", body: ["message": $0])
      }
    }
    _ = connectionManager.onAuthReceived.add{
      if (hasListeners) {
        self.sendEvent(withName: "onMessageReceived", body: ["newkey": $0])
      }
    }
    _ = connectionManager.onServerInfoReceived.add{
      if (hasListeners) {
        self.sendEvent(withName: "onMessageReceived", body: ["server": $0])
      }
    }
    
     _ = sendingManger.sentObservers.add{
      if (hasListeners) {
        let data : NSDictionary =  ["speed": $0.0 * 3.6, "distance": $0.1];
        let location : NSDictionary =  ["command": "", "data": data];
        
        do{
            let json = try JSONSerialization.data(withJSONObject: location, options: JSONSerialization.WritingOptions(rawValue: 0))
            
            if let jsonString = NSString(data: json, encoding: String.Encoding.utf8.rawValue) {
              self.sendEvent(withName: "onMessageReceived", body: ["location": jsonString])
            }
        }catch {
            print("error generating location message")
        }
        
        //
      }
    }
  }
  
  
  
  override func supportedEvents() -> [String]! {
    return ["onMessageReceived"]
  }
  
  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  override func startObserving() {
    hasListeners = true;
    
  }
  override func stopObserving() {
    hasListeners = false
  }
  
  @objc func configure(_ config: String){
    if let data: Data = config.data(using: .utf8) {
      do {
        let json = try JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.mutableContainers);
        connectionManager.settings = (json as? NSDictionary)!
      } catch {
      }
    }
  }
  
  @objc func sendMessage(_ message: String){
     connectionManager.send(request: message)
  }
  
  
  @objc func connect(){
    connectionManager.connect()
  }
  
  @objc open func startSendingCoordinates(_ once: Bool) {
    sendingManger.startSendingCoordinates(once);
  }
  
  @objc open func stopSendingCoordinates () {
    sendingManger.stopSendingCoordinates()
  }

  @objc open func pauseSendingCoordinates () {
    sendingManger.pauseSendingCoordinates()
  }
  
  @objc func processLocation(_ location: String){
  }
}
