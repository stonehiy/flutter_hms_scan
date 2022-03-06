package com.fitem.flutter_hms_scan

import android.app.Activity
import android.content.Context

/**
 * 打印机管理
 * Created by LeiGuangwu on 2022/3/2.
 */
class ScanLauncher(var applicationContext: Context, var activity : Activity?) {

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
    }
}