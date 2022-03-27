//
//  BarcodeScannerViewControllerDelegate.swift
//  flutter_hms_scan
//
//  Created by Fitem on 2022/3/27.
//

import Foundation

protocol BarcodeScannerViewControllerDelegate {
    func didScanBarcodeWithResult(_ controller: BarcodeScannerViewController?,
                                  scanResult: ScanResult
    )
    
    func didFailWithErrorCode(_ controller: BarcodeScannerViewController?,
                              errorCode: String
    )
}
