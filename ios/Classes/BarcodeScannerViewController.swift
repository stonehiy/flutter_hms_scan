//
//  BarcodeScannerViewController.swift
//  flutter_hms_scan
//
//  Created by Fitem on 2022/3/27.
//

import Foundation

import MTBBarcodeScanner

class BarcodeScannerViewController: UIViewController {
  private var previewView: UIView?
  private var scanRect: ScannerOverlay?
  private var scanner: MTBBarcodeScanner?
  
  private let formatMap = [
    BarcodeFormat.aztec : AVMetadataObject.ObjectType.aztec,
    BarcodeFormat.code39 : AVMetadataObject.ObjectType.code39,
    BarcodeFormat.code93 : AVMetadataObject.ObjectType.code93,
    BarcodeFormat.code128 : AVMetadataObject.ObjectType.code128,
    BarcodeFormat.dataMatrix : AVMetadataObject.ObjectType.dataMatrix,
    BarcodeFormat.ean8 : AVMetadataObject.ObjectType.ean8,
    BarcodeFormat.ean13 : AVMetadataObject.ObjectType.ean13,
    BarcodeFormat.interleaved2Of5 : AVMetadataObject.ObjectType.interleaved2of5,
    BarcodeFormat.pdf417 : AVMetadataObject.ObjectType.pdf417,
    BarcodeFormat.qr : AVMetadataObject.ObjectType.qr,
    BarcodeFormat.upce : AVMetadataObject.ObjectType.upce,
  ]
  
  var delegate: BarcodeScannerViewControllerDelegate?
  
  private var device: AVCaptureDevice? {
    return AVCaptureDevice.default(for: .video)
  }
  
  private var isFlashOn: Bool {
    return device != nil && (device?.flashMode == AVCaptureDevice.FlashMode.on || device?.torchMode == .on)
  }
  
  private var hasTorch: Bool {
    return device?.hasTorch ?? false
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()

    UIDevice.current.endGeneratingDeviceOrientationNotifications()
    
    #if targetEnvironment(simulator)
    view.backgroundColor = .lightGray
    #endif
    
    previewView = UIView(frame: view.bounds)
    if let previewView = previewView {
      previewView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
      view.addSubview(previewView)
    }
    setupScanRect(view.bounds)
    
    scanner = MTBBarcodeScanner(previewView: previewView)
                                  
    navigationItem.leftBarButtonItem = UIBarButtonItem(title: "cancel",
                                                        style: .plain,
                                                        target: self,
                                                        action: #selector(cancel)
    )
    updateToggleFlashButton()
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    
    if scanner!.isScanning() {
      scanner!.stopScanning()
    }
    
    UIDevice.current.endGeneratingDeviceOrientationNotifications()
    
    scanRect?.startAnimating()
    MTBBarcodeScanner.requestCameraPermission(success: { success in
      if success {
        self.startScan()
      } else {
        #if !targetEnvironment(simulator)
        self.errorResult(errorCode: "PERMISSION_NOT_GRANTED")
        #endif
      }
    })
  }
  
  override func viewWillDisappear(_ animated: Bool) {
    scanner?.stopScanning()
    scanRect?.stopAnimating()
    
    UIDevice.current.beginGeneratingDeviceOrientationNotifications()
    
    if isFlashOn {
      setFlashState(false)
    }
    
    super.viewWillDisappear(animated)
  }
  
  override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
    super.viewWillTransition(to: size, with: coordinator)
    setupScanRect(CGRect(origin: CGPoint(x: 0, y:0),
                         size: size
    ))
  }
  
  private func setupScanRect(_ bounds: CGRect) {
    if scanRect != nil {
      scanRect?.stopAnimating()
      scanRect?.removeFromSuperview()
    }
    scanRect = ScannerOverlay(frame: bounds)
    if let scanRect = scanRect {
      scanRect.translatesAutoresizingMaskIntoConstraints = false
      scanRect.backgroundColor = UIColor.clear
      view.addSubview(scanRect)
      scanRect.startAnimating()
    }
  }
  
  private func startScan() {
    do {
      try scanner!.startScanning(with: cameraFromConfig, resultBlock: { codes in
        if let code = codes?.first {
          let codeType = self.formatMap.first(where: { $0.value == code.type });
          let scanResult = ScanResult.with {
            $0.type = .barcode
            $0.rawContent = code.stringValue ?? ""
            $0.format = codeType?.key ?? .unknown
            $0.formatNote = codeType == nil ? code.type.rawValue : ""
          }
          self.scanner!.stopScanning()
          self.scanResult(scanResult)
        }
      })
    } catch {
      self.scanResult(ScanResult.with {
        $0.type = .error
        $0.rawContent = "\(error)"
        $0.format = .unknown
      })
    }
  }
  
  @objc private func cancel() {
    scanResult( ScanResult.with {
      $0.type = .cancelled
      $0.format = .unknown
    });
  }
  
  @objc private func onToggleFlash() {
    setFlashState(!isFlashOn)
  }
  
  private func updateToggleFlashButton() {
    if !hasTorch {
      return
    }
    
    let buttonText = isFlashOn ? "flash_off" : "flash_on"
    navigationItem.rightBarButtonItem = UIBarButtonItem(title: buttonText,
                                                        style: .plain,
                                                        target: self,
                                                        action: #selector(onToggleFlash)
    )
  }
  
  private func setFlashState(_ on: Bool) {
    if let device = device {
      guard device.hasFlash && device.hasTorch else {
        return
      }
      
      do {
        try device.lockForConfiguration()
      } catch {
        return
      }
      
      device.flashMode = on ? .on : .off
      device.torchMode = on ? .on : .off
      
      device.unlockForConfiguration()
      updateToggleFlashButton()
    }
  }
  
  private func errorResult(errorCode: String){
    delegate?.didFailWithErrorCode(self, errorCode: errorCode)
    dismiss(animated: false)
  }
  
  private func scanResult(_ scanResult: ScanResult){
    self.delegate?.didScanBarcodeWithResult(self, scanResult: scanResult)
    dismiss(animated: false)
  }
  
  private var cameraFromConfig: MTBCamera {
    return .back
  }
}
