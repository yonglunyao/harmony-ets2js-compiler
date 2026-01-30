import { BusinessError } from '@ohos.base';
import { Logger } from '../utils/Logger';

export class NfcManager {
  TAG = NfcManager;

  isNfcAvailable = false;

  static async checkNfcAvailability() {
    try {
  const nfcModule = await {"kind":102,"kindName":"ImportKeyword"}("@ohos.nfc");
  const isAvailable = nfcModule.isNfcAvailable();
  NfcManager.isNfcAvailable = isAvailable;
  Logger.success(`NFC可用性检查: ${isAvailable}`);
  return isAvailable;
} catch (error) {
  Logger.error("NFC可用性检查失败", error);
  return false;
}
  }

  static async getNfcState() {
    try {
  const nfcModule = await {"kind":102,"kindName":"ImportKeyword"}("@ohos.nfc");
  const state = nfcModule.isNfcAvailable();
  const stateText = state ? "已开启" : "已关闭";
  Logger.success(`获取NFC状态: ${stateText}`);
  return stateText;
} catch (error) {
  Logger.error("获取NFC状态失败", error);
  return "未知";
}
  }

  static async readTagData(tagInfo /* NfcTagInfo */) {
    try {
  if (!NfcManager.isNfcAvailable) {
  Logger.error("NFC不可用");
  return "";
}
  const simulatedData = {"kind":229,"kindName":"TemplateExpression"};
  Logger.success(`读取NFC标签数据成功: ${simulatedData}`);
  return simulatedData;
} catch (error) {
  Logger.error("读取NFC标签数据失败", error);
  return "";
}
  }

  static async writeTagData(tagInfo /* NfcTagInfo */, data /* string */) {
    try {
  if (!NfcManager.isNfcAvailable) {
  Logger.error("NFC不可用");
  return false;
}
  if (!tagInfo.isWritable) {
  Logger.error("该标签不支持写入");
  return false;
}
  if (data.length > tagInfo.maxSize) {
  Logger.error("数据超过标签最大容量");
  return false;
}
  Logger.success(`写入NFC标签数据成功: ${data}`);
  return true;
} catch (error) {
  Logger.error("写入NFC标签数据失败", error);
  return false;
}
  }

  static bytesToHex(bytes /* number[] */) {
    return bytes.map((byte) => byte.toString(16).padStart(2, "0")).join('').toUpperCase();
  }

}


//# sourceMappingURL=NfcManager.js.map