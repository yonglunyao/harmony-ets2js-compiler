import { Permissions, abilityAccessCtrl } from '@kit.AbilityKit';
import { PermissionUtil } from '@pura/harmony-utils';

export class PermissionUtils {
  static async checkPermission(permission /* Permissions */) {
    return await PermissionUtil.checkPermissions(permission);
  }

  static async checkPermissions(permissions /* Permissions[] */) {
    const granted = [];
    for (const permission of permissions) {
  const hasPermission = await PermissionUtil.checkPermissions(permission);
  if (hasPermission) {
  granted.push(permission);
}
}
    return granted;
  }

  static async checkRequestPermission(permission /* Permissions */) {
    return await PermissionUtil.checkRequestPermissions(permission);
  }

  static async checkRequestPermissions(permissions /* Permissions[] */) {
    for (const permission of permissions) {
  const granted = await PermissionUtil.checkRequestPermissions(permission);
  if (!granted) {
  return false;
}
}
    return true;
  }

  static async requestPermissions(permissions /* Permissions | Permissions[] */) {
    return await PermissionUtil.requestPermissions(permissions);
  }

  static async requestPermissionsEasy(permissions /* Permissions | Permissions[] */) {
    return await PermissionUtil.requestPermissionsEasy(permissions);
  }

  static async requestPermissionOnSetting(permissions /* Permissions | Permissions[] */) {
    return await PermissionUtil.requestPermissionOnSetting(permissions);
  }

  static async requestPermissionOnSettingEasy(permissions /* Permissions[] */) {
    return await PermissionUtil.requestPermissionOnSettingEasy(permissions);
  }

  static async requestGlobalSwitch(type /* SwitchType */) {
    let switchType;
    if (type === "microphone") {
      switchType = 1;
    }
    else {
      if (type === "camera") {
        switchType = 2;
      }
      else {
        switchType = 3;
      }
    }
    return await PermissionUtil.requestGlobalSwitch(switchType);
  }

  static allGranted(results /* PermissionResult[] */) {
    return results.every((result) => result.granted);
  }

  static getDeniedPermissions(results /* PermissionResult[] */) {
    return results.filter((result) => !result.granted).map((result) => result.permission);
  }

  static getGrantedPermissions(results /* PermissionResult[] */) {
    return results.filter((result) => result.granted).map((result) => result.permission);
  }

  CommonPermissions = {INTERNET: "ohos.permission.INTERNET", GET_NETWORK_INFO: "ohos.permission.GET_NETWORK_INFO", LOCATION: "ohos.permission.LOCATION", APPROXIMATELY_LOCATION: "ohos.permission.APPROXIMATELY_LOCATION", READ_WRITE_DOWNLOAD_DIRECTORY: "ohos.permission.READ_WRITE_DOWNLOAD_DIRECTORY", CAMERA: "ohos.permission.CAMERA", MICROPHONE: "ohos.permission.MICROPHONE", READ_CONTACTS: "ohos.permission.READ_CONTACTS", WRITE_CONTACTS: "ohos.permission.WRITE_CONTACTS", READ_PASTEBOARD: "ohos.permission.READ_PASTEBOARD", WRITE_PASTEBOARD: "ohos.permission.WRITE_PASTEBOARD", NOTIFICATION_CONTROLLER: "ohos.permission.NOTIFICATION_CONTROLLER", SYSTEM_FLOAT_WINDOW: "ohos.permission.SYSTEM_FLOAT_WINDOW", KEEP_BACKGROUND_RUNNING: "ohos.permission.KEEP_BACKGROUND_RUNNING", SET_ALARM: "ohos.permission.SET_ALARM"};

}


//# sourceMappingURL=PermissionUtils.js.map