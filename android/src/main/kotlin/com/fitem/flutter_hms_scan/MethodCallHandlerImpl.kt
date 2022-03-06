package com.fitem.flutter_hms_scan

import android.os.Bundle
import android.os.RemoteException
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/**
 * 插件方法监听
 * Created by LeiGuangwu on 2022/3/2.
 */
class MethodCallHandlerImpl(var scanLauncher: ScanLauncher) : MethodChannel.MethodCallHandler,
    MethodCallHandlerListener {

    private lateinit var channel: MethodChannel

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    override fun startService(binaryMessenger: BinaryMessenger) {
        channel = MethodChannel(binaryMessenger, "print_plugin")
        channel.setMethodCallHandler(this)
    }


    override fun stopService() {
        channel.setMethodCallHandler(null)
    }
}