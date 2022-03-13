import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_hms_scan/flutter_hms_scan.dart';
import 'package:flutter_hms_scan/model/scan_bean.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  ScanBean? _bean;
  bool _scanSuc = false;
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion =
          await FlutterHmsScan.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter hms scan'),
        ),
        body: Container(
          width: double.infinity,
          height: double.infinity,
          child: Column(
            children: [
              const SizedBox(height: 80),
              ElevatedButton(
                onPressed: () {
                  FlutterHmsScan.loadScanKit().then((value) {
                    setState(() {
                      _bean = value;
                      _scanSuc = _bean?.scanStatus ?? false;
                    });
                  });
                },
                child: const Text('LoadScanKit', style: TextStyle(fontSize: 20),),
              ),
              const SizedBox(height: 50),
              Text('Running on: $_platformVersion\n'),
              const SizedBox(height: 50),
              if (_scanSuc) Text("${_bean?.codeResult}"),
              const SizedBox(height: 10),
              if (_scanSuc) Text("ScanStatus:  ${_bean?.scanStatus}"),
              const SizedBox(height: 10),
              if (_scanSuc) Text("CodeFormat:  ${_bean?.codeFormat}"),
              const SizedBox(height: 10),
              if (_scanSuc) Text("ResultType:  ${_bean?.resultType}"),
            ],
          ),
        ),
      ),
    );
  }
}
