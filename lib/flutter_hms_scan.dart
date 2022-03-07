import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:flutter_hms_scan/model/scan_bean.dart';

class FlutterHmsScan {
  static const MethodChannel _channel = MethodChannel('hms_scan');

  static Future<ScanBean> loadScanKit() async {
    return await _channel
        .invokeMethod("loadScanKit")
        .then((value) => scanBeanFromJson(json.encode(value)));
  }
}
