import 'dart:convert';

import 'dart:ffi';

ScanBean scanBeanFromJson(String str) => ScanBean.fromJson(json.decode(str));

String scanBeanToJson(ScanBean data) => json.encode(data.toJson());

class ScanBean {
  ScanBean({
    this.scanStatus = false,
    this.codeFormat,
    this.resultType,
    this.codeResult,
  });

  bool scanStatus;
  String? codeFormat;
  String? resultType;
  String? codeResult;

  factory ScanBean.fromJson(Map<String, dynamic> json) => ScanBean(
        scanStatus: json["scanStatus"] is String
            ? json["scanStatus"] == "true"
            : json["scanStatus"] ?? false,
        codeFormat: json["codeFormat"],
        resultType: json["resultType"],
        codeResult: json["codeResult"],
      );

  Map<String, dynamic> toJson() => {
        "scanStatus": scanStatus,
        "codeFormat": codeFormat,
        "resultType": resultType,
        "codeResult": codeResult,
      };
}
