package com.fitem.flutter_hms_scan

import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.PluginRegistry.Registrar


/**
 * HmsScanPlugin
 */
class HmsScanPlugin : FlutterPlugin, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var mScanLauncher: ScanLauncher
    private lateinit var mHandler: MethodCallHandlerImpl

    fun registerWith(registrar: Registrar) {
        mScanLauncher = ScanLauncher(registrar.context(), registrar.activity())
        mHandler = MethodCallHandlerImpl(mScanLauncher)
        mHandler.startService(registrar.messenger())
        registrar.addActivityResultListener(mHandler)
        registrar.addRequestPermissionsResultListener(mHandler)
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mScanLauncher = ScanLauncher(flutterPluginBinding.applicationContext, null)
        mHandler = MethodCallHandlerImpl(mScanLauncher)
        mHandler.startService(flutterPluginBinding.binaryMessenger)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        mHandler.stopService()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        mScanLauncher.activity = binding.activity
        binding.addActivityResultListener(mHandler)
        binding.addRequestPermissionsResultListener(mHandler)
    }

    override fun onDetachedFromActivity() {
        mScanLauncher.activity = null
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }
}
