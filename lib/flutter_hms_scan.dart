
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterHmsScan {
  static const MethodChannel _channel = MethodChannel('hms_scan');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
  
  static Future<String> loadScanKit() async {
    return await _channel.invokeMethod("loadScanKit");
  }
}
