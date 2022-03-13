import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:flutter_hms_scan/model/scan_bean.dart';

class FlutterHmsScan {
  // 创建插件
  static const MethodChannel _channel = MethodChannel('hms_scan');
  // 定义调用方法
  static Future<ScanBean> loadScanKit() async {
    return await _channel
        .invokeMethod("loadScanKit")
        .then((value) => scanBeanFromJson(json.encode(value)));
  }

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
