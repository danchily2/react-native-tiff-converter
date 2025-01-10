
// #ifdef RCT_NEW_ARCH_ENABLED
// #import "RNTiffConverterSpec.h"

// @interface TiffConverter : NSObject <NativeTiffConverterSpec>
// #else
#import <React/RCTBridgeModule.h>

@interface TiffConverter : NSObject <RCTBridgeModule>
// #endif

@end
