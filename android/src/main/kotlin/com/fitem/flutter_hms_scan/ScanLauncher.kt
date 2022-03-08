package com.fitem.flutter_hms_scan

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/**
 * hms扫码启动器
 * Created by Fitem on 2022/3/2.
 */
class ScanLauncher(var applicationContext: Context, var activity: Activity?) {

    companion object {
        const val CAMERA_REQ_CODE = 111
        const val DEFINED_CODE = 222
        const val BITMAP_CODE = 333
        const val MULTIPROCESSOR_SYN_CODE = 444
        const val MULTIPROCESSOR_ASYN_CODE = 555
        const val GENERATE_CODE = 666
        const val DECODE = 1
        const val GENERATE = 2
        const val REQUEST_CODE_SCAN_ONE = 0X01
        const val REQUEST_CODE_DEFINE = 0X0111
        const val REQUEST_CODE_SCAN_MULTI = 0X011
        const val DECODE_MODE = "decode_mode"
        const val RESULT = "SCAN_RESULT"
        const val SCAN_STATUS = "scanStatus"
        const val CODE_FORMAT = "codeFormat"
        const val RESULT_TYPE = "resultType"
        const val CODE_RESULT = "codeResult"
    }

    private var result: MethodChannel.Result? = null

    /**
     * 扫码
     */
    fun loadScanKit(call: MethodCall, result: MethodChannel.Result) {
        this.result = result
        requestPermission(CAMERA_REQ_CODE, DECODE)
    }

    /**
     * Apply for permissions.
     */
    private fun requestPermission(requestCode: Int, mode: Int) {
        if (activity == null) {
            result?.success(mapOf(SCAN_STATUS to false))
            return
        }
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
            activity!!,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
            requestCode
        )
    }

    /**
     * Apply for permissions.
     */
    private fun generatePermission(requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            requestCode
        )
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean {
        //Default View
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            val obj: HmsScan? = data.getParcelableExtra(ScanUtil.RESULT)
            if (obj != null) {
                result?.success(
                    mapOf(
                        SCAN_STATUS to true,
                        CODE_FORMAT to getCodeFormat(obj.scanType),
                        RESULT_TYPE to getResultType(obj),
                        CODE_RESULT to obj.originalValue
                    )
                )
                return true
            }
            //MultiProcessor & Bitmap
        }
        return false
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {

        if (grantResults.size < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        //Default View Mode
        if (requestCode == CAMERA_REQ_CODE) {
            ScanUtil.startScan(
                activity,
                REQUEST_CODE_SCAN_ONE,
                HmsScanAnalyzerOptions.Creator().create()
            )
            return true
        }
        return false
    }

    /**
     * 获取CodeFormat
     */
    private fun getCodeFormat(codeFormat: Int): String {
        return when (codeFormat) {
            HmsScan.QRCODE_SCAN_TYPE -> "QR code"
            HmsScan.AZTEC_SCAN_TYPE -> "AZTEC code"
            HmsScan.DATAMATRIX_SCAN_TYPE -> "DATAMATRIX code"
            HmsScan.PDF417_SCAN_TYPE -> "PDF417 code"
            HmsScan.CODE93_SCAN_TYPE -> "CODE93"
            HmsScan.CODE39_SCAN_TYPE -> "CODE39"
            HmsScan.CODE128_SCAN_TYPE -> "CODE128"
            HmsScan.EAN13_SCAN_TYPE -> "EAN13 code"
            HmsScan.EAN8_SCAN_TYPE -> "EAN8 code"
            HmsScan.ITF14_SCAN_TYPE -> "ITF14 code"
            HmsScan.UPCCODE_A_SCAN_TYPE -> "UPCCODE_A"
            HmsScan.UPCCODE_E_SCAN_TYPE -> "UPCCODE_E"
            HmsScan.CODABAR_SCAN_TYPE -> "CODABAR"
            else -> "OTHER"
        }
    }

    /**
     * 获取ResultType
     */
    private fun getResultType(hmsScan: HmsScan): String {
        return when (hmsScan.scanType) {
            HmsScan.QRCODE_SCAN_TYPE -> when (hmsScan.scanTypeForm) {
                HmsScan.QRCODE_SCAN_TYPE -> "Text"
                HmsScan.EVENT_INFO_FORM -> "Event"
                HmsScan.CONTACT_DETAIL_FORM -> "Contact"
                HmsScan.DRIVER_INFO_FORM -> "License"
                HmsScan.EMAIL_CONTENT_FORM -> "Email"
                HmsScan.LOCATION_COORDINATE_FORM -> "Location"
                HmsScan.TEL_PHONE_NUMBER_FORM -> "Tel"
                HmsScan.SMS_FORM -> "SMS"
                HmsScan.WIFI_CONNECT_INFO_FORM -> "Wi-Fi"
                HmsScan.URL_FORM -> "WebSite"
                HmsScan.URL_FORM -> "WebSite"
                else -> "Text"
            }
            HmsScan.EAN13_SCAN_TYPE -> when (hmsScan.scanTypeForm) {
                HmsScan.ISBN_NUMBER_FORM -> "ISBN"
                HmsScan.ARTICLE_NUMBER_FORM -> "Product"
                else -> "Text"
            }
            HmsScan.EAN8_SCAN_TYPE,
            HmsScan.UPCCODE_A_SCAN_TYPE,
            HmsScan.UPCCODE_E_SCAN_TYPE -> when (hmsScan.scanTypeForm) {
                HmsScan.ARTICLE_NUMBER_FORM -> "Product"
                else -> "Text"

            }
            else -> "Text"
        }
    }
}