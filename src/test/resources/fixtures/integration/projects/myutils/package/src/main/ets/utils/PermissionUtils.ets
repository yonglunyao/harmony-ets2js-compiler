/**
 * 权限工具类
 * 基于 @pura/harmony-utils 的 PermissionUtil 实现
 */
import { Permissions, abilityAccessCtrl } from '@kit.AbilityKit';
import { PermissionUtil } from '@pura/harmony-utils';

/**
 * 权限申请结果
 */
export interface PermissionResult {
  /**
   * 权限名称
   */
  permission: Permissions;
  /**
   * 是否已授权
   */
  granted: boolean;
}

/**
 * 权限申请回调
 */
export type PermissionCallback = (results: PermissionResult[]) => void;

/**
 * 全局开关类型
 */
export type SwitchType = 'microphone' | 'camera' | 'location';

/**
 * 常用权限集合接口
 */
interface CommonPermissionsType {
  INTERNET: Permissions;
  GET_NETWORK_INFO: Permissions;
  LOCATION: Permissions;
  APPROXIMATELY_LOCATION: Permissions;
  READ_WRITE_DOWNLOAD_DIRECTORY: Permissions;
  CAMERA: Permissions;
  MICROPHONE: Permissions;
  READ_CONTACTS: Permissions;
  WRITE_CONTACTS: Permissions;
  READ_PASTEBOARD: Permissions;
  WRITE_PASTEBOARD: Permissions;
  NOTIFICATION_CONTROLLER: Permissions;
  SYSTEM_FLOAT_WINDOW: Permissions;
  KEEP_BACKGROUND_RUNNING: Permissions;
  SET_ALARM: Permissions;
}

/**
 * 权限工具类
 */
export class PermissionUtils {

  /**
   * 校验单个权限是否已授权
   * @param permission 待检查的权限
   * @returns true表示已授权，false表示未授权
   */
  static async checkPermission(permission: Permissions): Promise<boolean> {
    return await PermissionUtil.checkPermissions(permission);
  }

  /**
   * 校验多个权限是否已授权
   * @param permissions 权限数组
   * @returns 已授权的权限数组
   */
  static async checkPermissions(permissions: Permissions[]): Promise<Permissions[]> {
    const granted: Permissions[] = [];
    for (const permission of permissions) {
      const hasPermission = await PermissionUtil.checkPermissions(permission);
      if (hasPermission) {
        granted.push(permission);
      }
    }
    return granted;
  }

  /**
   * 校验单个权限并申请授权（如果未授权）
   * @param permission 需要授权的权限
   * @returns true表示授权成功，false表示用户拒绝授权
   */
  static async checkRequestPermission(permission: Permissions): Promise<boolean> {
    return await PermissionUtil.checkRequestPermissions(permission);
  }

  /**
   * 校验多个权限并申请授权
   * @param permissions 需要授权的权限数组
   * @returns true表示全部授权成功，false表示有权限被拒绝
   */
  static async checkRequestPermissions(permissions: Permissions[]): Promise<boolean> {
    for (const permission of permissions) {
      const granted = await PermissionUtil.checkRequestPermissions(permission);
      if (!granted) {
        return false;
      }
    }
    return true;
  }

  /**
   * 申请单个或多个权限
   * @param permissions 需要授权的权限（单个或数组）
   * @returns true表示授权成功，false表示用户拒绝授权
   */
  static async requestPermissions(permissions: Permissions | Permissions[]): Promise<boolean> {
    return await PermissionUtil.requestPermissions(permissions);
  }

  /**
   * 申请权限，拒绝后二次向用户申请（推荐使用）
   * @param permissions 需要授权的权限（单个或数组）
   * @returns true表示授权成功，false表示用户拒绝授权
   */
  static async requestPermissionsEasy(permissions: Permissions | Permissions[]): Promise<boolean> {
    return await PermissionUtil.requestPermissionsEasy(permissions);
  }

  /**
   * 二次向用户申请授权（单个权限或读写权限组）
   * @param permissions 需要授权的权限集合
   * @returns true表示授权成功，false表示用户拒绝授权
   */
  static async requestPermissionOnSetting(permissions: Permissions | Permissions[]): Promise<boolean> {
    return await PermissionUtil.requestPermissionOnSetting(permissions);
  }

  /**
   * 二次向用户申请授权（多个权限）
   * @param permissions 需要授权的权限集合
   * @returns true表示授权成功，false表示用户拒绝授权
   */
  static async requestPermissionOnSettingEasy(permissions: Permissions[]): Promise<boolean> {
    return await PermissionUtil.requestPermissionOnSettingEasy(permissions);
  }

  /**
   * 拉起全局开关设置弹框（如录音、拍照等功能的开关）
   * @param type 全局开关类型
   * @returns true表示操作成功
   */
  static async requestGlobalSwitch(type: SwitchType): Promise<boolean> {
    let switchType: abilityAccessCtrl.SwitchType;
    if (type === 'microphone') {
      switchType = 1 as abilityAccessCtrl.SwitchType; // SWITCH_TYPE_MICROPHONE
    } else if (type === 'camera') {
      switchType = 2 as abilityAccessCtrl.SwitchType; // SWITCH_TYPE_CAMERA
    } else {
      switchType = 3 as abilityAccessCtrl.SwitchType; // SWITCH_TYPE_LOCATION
    }
    return await PermissionUtil.requestGlobalSwitch(switchType);
  }

  /**
   * 判断权限申请结果是否全部授权
   * @param results 权限申请结果数组
   * @returns true表示全部授权
   */
  static allGranted(results: PermissionResult[]): boolean {
    return results.every(result => result.granted);
  }

  /**
   * 获取未授权的权限列表
   * @param results 权限申请结果数组
   * @returns 未授权的权限数组
   */
  static getDeniedPermissions(results: PermissionResult[]): Permissions[] {
    return results
      .filter(result => !result.granted)
      .map(result => result.permission);
  }

  /**
   * 获取已授权的权限列表
   * @param results 权限申请结果数组
   * @returns 已授权的权限数组
   */
  static getGrantedPermissions(results: PermissionResult[]): Permissions[] {
    return results
      .filter(result => result.granted)
      .map(result => result.permission);
  }

  /**
   * 常用权限集合
   */
  static readonly CommonPermissions: CommonPermissionsType = {
    /**
     * 网络相关
     */
    INTERNET: 'ohos.permission.INTERNET' as Permissions,
    GET_NETWORK_INFO: 'ohos.permission.GET_NETWORK_INFO' as Permissions,

    /**
     * 位置相关
     */
    LOCATION: 'ohos.permission.LOCATION' as Permissions,
    APPROXIMATELY_LOCATION: 'ohos.permission.APPROXIMATELY_LOCATION' as Permissions,

    /**
     * 存储相关
     */
    READ_WRITE_DOWNLOAD_DIRECTORY: 'ohos.permission.READ_WRITE_DOWNLOAD_DIRECTORY' as Permissions,

    /**
     * 相机、麦克风
     */
    CAMERA: 'ohos.permission.CAMERA' as Permissions,
    MICROPHONE: 'ohos.permission.MICROPHONE' as Permissions,

    /**
     * 联系人
     */
    READ_CONTACTS: 'ohos.permission.READ_CONTACTS' as Permissions,
    WRITE_CONTACTS: 'ohos.permission.WRITE_CONTACTS' as Permissions,

    /**
     * 剪贴板
     */
    READ_PASTEBOARD: 'ohos.permission.READ_PASTEBOARD' as Permissions,
    WRITE_PASTEBOARD: 'ohos.permission.WRITE_PASTEBOARD' as Permissions,

    /**
     * 通知
     */
    NOTIFICATION_CONTROLLER: 'ohos.permission.NOTIFICATION_CONTROLLER' as Permissions,

    /**
     * 悬浮窗
     */
    SYSTEM_FLOAT_WINDOW: 'ohos.permission.SYSTEM_FLOAT_WINDOW' as Permissions,

    /**
     * 后台任务
     */
    KEEP_BACKGROUND_RUNNING: 'ohos.permission.KEEP_BACKGROUND_RUNNING' as Permissions,

    /**
     * 闹钟
     */
    SET_ALARM: 'ohos.permission.SET_ALARM' as Permissions
  };
}
