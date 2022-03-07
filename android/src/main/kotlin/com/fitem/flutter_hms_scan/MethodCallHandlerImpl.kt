package com.fitem.flutter_hms_scan

import android.app.Activity
import android.content.Intent
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry

/**
 * 插件方法监听
 * Created by LeiGuangwu on 2022/3/2.
 */
class MethodCallHandlerImpl(var scanLauncher: ScanLauncher) : MethodChannel.MethodCallHandler,
    MethodCallHandlerListener, PluginRegistry.ActivityResultListener,
    PluginRegistry.RequestPermissionsResultListener {

    private lateinit var channel: MethodChannel

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "loadScanKit" -> {
                scanLauncher.loadScanKit(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun startService(binaryMessenger: BinaryMessenger) {
        channel = MethodChannel(binaryMessenger, "hms_scan")
        channel.setMethodCallHandler(this)
    }


    override fun stopService() {
        channel.setMethodCallHandler(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return false
        }
        return scanLauncher.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>?,
        grantResults: IntArray?
    ): Boolean {
        if (permissions == null || grantResults == null) {
            return false
        }
        return  scanLauncher.onRequestPermissionResult(requestCode, permissions, grantResults)
    }
}