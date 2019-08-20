//
//  OsMoEventEmitter.m
//  OsMo
//
//  Created by Alexey Sirotkin on 04.08.2019.
//  Copyright © 2019 OsMo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"
#import "React/RCTEventEmitter.h"


@interface RCT_EXTERN_MODULE(OsMoEventEmitter, RCTEventEmitter)
RCT_EXTERN_METHOD(connect)
RCT_EXTERN_METHOD(getMessageOfTheDay)
RCT_EXTERN_METHOD(startSendingCoordinates:(BOOL)once)
RCT_EXTERN_METHOD(stopSendingCoordinates)
RCT_EXTERN_METHOD(pauseSendingCoordinates)
@end
