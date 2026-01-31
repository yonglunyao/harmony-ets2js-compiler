/**
 * MyUtils - 基础工具库
 * 提供常用的工具函数类，包括字符串、日期、数组、验证、对象、数学工具、权限管理和加解密
 */

// 导出所有工具类
export { StringUtils } from './src/main/ets/utils/StringUtils';
export { DateUtils } from './src/main/ets/utils/DateUtils';
export { ArrayUtils } from './src/main/ets/utils/ArrayUtils';
export { ValidationUtils } from './src/main/ets/utils/ValidationUtils';
export { ObjectUtils } from './src/main/ets/utils/ObjectUtils';
export { MathUtils } from './src/main/ets/utils/MathUtils';
export { PermissionUtils } from './src/main/ets/utils/PermissionUtils';
export { CryptoUtils } from './src/main/ets/utils/CryptoUtils';

// 导出工具类类型
export type { StringUtils as StringUtilsType } from './src/main/ets/utils/StringUtils';
export type { DateUtils as DateUtilsType } from './src/main/ets/utils/DateUtils';
export type { ArrayUtils as ArrayUtilsType } from './src/main/ets/utils/ArrayUtils';
export type { ValidationUtils as ValidationUtilsType } from './src/main/ets/utils/ValidationUtils';
export type { ObjectUtils as ObjectUtilsType } from './src/main/ets/utils/ObjectUtils';
export type { MathUtils as MathUtilsType } from './src/main/ets/utils/MathUtils';
export type { PermissionUtils as PermissionUtilsType } from './src/main/ets/utils/PermissionUtils';
export type { CryptoUtils as CryptoUtilsType } from './src/main/ets/utils/CryptoUtils';

// 导出权限相关类型
export type { PermissionResult, PermissionCallback } from './src/main/ets/utils/PermissionUtils';

// 导出加解密相关类型
export type { AESConfig, AESMode, AESPadding } from './src/main/ets/utils/CryptoUtils';
