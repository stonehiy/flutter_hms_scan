package com.fitem.flutter_hms_scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.fitem.flutter_hms_scan.ScanLauncher.Companion.CAMERA_REQ_CODE
import com.fitem.flutter_hms_scan.ScanLauncher.Companion.DECODE
import com.fitem.flutter_hms_scan.ScanLauncher.Companion.GENERATE
import com.fitem.flutter_hms_scan.ScanLauncher.Companion.REQUEST_CODE_SCAN_ONE
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
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
    private var result : MethodChannel.Result? = null

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        this.result = result
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "loadScanKit" -> {
                requestPermission(CAMERA_REQ_CODE, DECODE)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    /**
     * Apply for permissions.
     */
    private fun requestPermission(requestCode: Int, mode: Int) {
        if(scanLauncher.activity == null) return
        if (mode == DECODE) {
            decodePermission(requestCode)
        } else if (mode == GENERATE) {
            generatePermission(requestCode)
        }
    }

    /**
     * Apply for permissions.
     */
    private fun decodePermission(requestCode: Int) {
        ActivityCompat.requestPermissions(
            scanLauncher.activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
            requestCode)
    }

    /**
     * Apply for permissions.
     */
    private fun generatePermission(requestCode: Int) {
        ActivityCompat.requestPermissions(
            scanLauncher.activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            requestCode)
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
        //Default View
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            val obj: HmsScan? = data.getParcelableExtra(ScanUtil.RESULT)
            if (obj != null) {
                result?.success(obj.getOriginalValue())
            }
            //MultiProcessor & Bitmap
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>?,
        grantResults: IntArray?
    ): Boolean {
        if (permissions == null || grantResults == null) {
            return false
        }
        if (grantResults.size < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        //Default View Mode
        if (requestCode == CAMERA_REQ_CODE) {
            ScanUtil.startScan(scanLauncher.activity, REQUEST_CODE_SCAN_ONE, HmsScanAnalyzerOptions.Creator().create())
        }
        return false
    }
}