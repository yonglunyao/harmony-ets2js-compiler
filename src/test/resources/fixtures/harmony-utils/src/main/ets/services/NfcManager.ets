import { BusinessError } from '@ohos.base';
import { Logger } from '../utils/Logger';

export interface NfcTagInfo {
  id: string;
  technology: string;
  maxSize: number;
  isWritable: boolean;
  rawData?: string;
}

export class NfcManager {
  private static readonly TAG = 'NfcManager';
  private static isNfcAvailable: boolean = false;

  // 检查NFC是否可用
  static async checkNfcAvailability(): Promise<boolean> {
    try {
      const nfcModule = await import('@ohos.nfc');
      const isAvailable = nfcModule.isNfcAvailable();
      NfcManager.isNfcAvailable = isAvailable;
      Logger.success(`NFC可用性检查: ${isAvailable}`);
      return isAvailable;
    } catch (error) {
      Logger.error('NFC可用性检查失败', error as BusinessError);
      return false;
    }
  }

  // 获取NFC状态
  static async getNfcState(): Promise<string> {
    try {
      const nfcModule = await import('@ohos.nfc');
      const state = nfcModule.isNfcAvailable();
      const stateText = state ? '已开启' : '已关闭';
      Logger.success(`获取NFC状态: ${stateText}`);
      return stateText;
    } catch (error) {
      Logger.error('获取NFC状态失败', error as BusinessError);
      return '未知';
    }
  }

  // 读取NFC标签数据
  static async readTagData(tagInfo: NfcTagInfo): Promise<string> {
    try {
      if (!NfcManager.isNfcAvailable) {
        Logger.error('NFC不可用');
        return '';
      }

      // 模拟读取数据
      const simulatedData = `TAG_${tagInfo.id}_${Date.now()}`;
      Logger.success(`读取NFC标签数据成功: ${simulatedData}`);
      return simulatedData;
    } catch (error) {
      Logger.error('读取NFC标签数据失败', error as BusinessError);
      return '';
    }
  }

  // 写入NFC标签数据
  static async writeTagData(tagInfo: NfcTagInfo, data: string): Promise<boolean> {
    try {
      if (!NfcManager.isNfcAvailable) {
        Logger.error('NFC不可用');
        return false;
      }

      if (!tagInfo.isWritable) {
        Logger.error('该标签不支持写入');
        return false;
      }

      if (data.length > tagInfo.maxSize) {
        Logger.error('数据超过标签最大容量');
        return false;
      }

      // 模拟写入数据
      Logger.success(`写入NFC标签数据成功: ${data}`);
      return true;
    } catch (error) {
      Logger.error('写入NFC标签数据失败', error as BusinessError);
      return false;
    }
  }

  // 字节数组转十六进制字符串
  static bytesToHex(bytes: number[]): string {
    return bytes.map(byte => byte.toString(16).padStart(2, '0')).join('').toUpperCase();
  }
}
