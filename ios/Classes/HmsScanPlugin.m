#import "HmsScanPlugin.h"
#if __has_include(<flutter_hms_scan/flutter_hms_scan-Swift.h>)
#import <flutter_hms_scan/flutter_hms_scan-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_hms_scan-Swift.h"
#endif

@implementation HmsScanPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftHmsScanPlugin registerWithRegistrar:registrar];
}
@end
