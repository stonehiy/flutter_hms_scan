import Flutter
import UIKit
import ScanKitFrameWork

public class SwiftHmsScanPlugin: NSObject, FlutterPlugin, DefaultScanDelegate {
    
    private var result: FlutterResult?
    private var hostViewController: UIViewController?
    
    public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "hms_scan", binaryMessenger: registrar.messenger())
    let instance = SwiftHmsScanPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      self.result = result
      if ("loadScanKit" == call.method) {
          loadScanKit()
      } else {
          result("iOS " + UIDevice.current.systemVersion)
      }
  }
    
    public func loadScanKit() {
        
        if let rootVC = UIApplication.shared.keyWindow?.rootViewController {
                    hostViewController = topViewController(base:rootVC)
                } else if let window = UIApplication.shared.delegate?.window,let rootVC = window?.rootViewController {
                    hostViewController = topViewController(base:rootVC)
                }
        
        let hmsDefaultScanViewController = HmsDefaultScanViewController()

        let navigationController = UINavigationController(rootViewController: hmsDefaultScanViewController)
        
        if #available(iOS 13.0, *) {
              navigationController.modalPresentationStyle = .fullScreen
          }
          
        hmsDefaultScanViewController.defaultScanDelegate = self
        hostViewController?.present(navigationController, animated: false)
    }
    
    private func topViewController(base: UIViewController?) -> UIViewController? {
        if let nav = base as? UINavigationController {
            return topViewController(base: nav.visibleViewController)

        } else if let tab = base as? UITabBarController, let selected = tab.selectedViewController {
            return topViewController(base: selected)

        } else if let presented = base?.presentedViewController {
            return topViewController(base: presented)
        }
        return base
    }
    
    private func defaultScanDelegateForDicResult(resultDic: NSDictionary){
        let string = resultDic["text"]
        result?(string)
    }
    
    private func defaultScanImagePickerDelegateForImage(image : UIImage){
        let resultDic = HmsBitMap.bitMap(for: image, with: HmsScanOptions())
        let string = resultDic?["text"]
        result?(string)
    }
}
