package com.fitem.flutter_hms_scan

import io.flutter.plugin.common.BinaryMessenger

/**
 * Created by Fitem on 2022/3/2.
 */
interface MethodCallHandlerListener {

    fun startService(binaryMessenger: BinaryMessenger)

    fun stopService()
}