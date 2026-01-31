/**
 * 加解密工具类
 * 基于 @ohos/crypto-js 实现 AES 加解密和哈希
 */
import { CryptoJS } from '@ohos/crypto-js';

/**
 * AES 加密模式
 */
export type AESMode = 'CBC' | 'CFB' | 'CTR' | 'OFB' | 'ECB';

/**
 * AES 填充方式
 */
export type AESPadding = 'Pkcs7' | 'ZeroPadding' | 'NoPadding';

/**
 * AES 加密配置
 */
export interface AESConfig {
  /**
   * 加密密钥
   */
  key: string;
  /**
   * 初始化向量（IV），CBC/CFB/OFB 模式需要
   */
  iv?: string;
  /**
   * 加密模式，默认 CBC
   */
  mode?: AESMode;
  /**
   * 填充方式，默认 Pkcs7
   */
  padding?: AESPadding;
  /**
   * 输出格式，默认 Base64
   */
  outputFormat?: 'Base64' | 'Hex';
}

/**
 * Cipher 配置接口
 */
interface CipherOption {
  iv?: object;
  mode?: object;
  padding?: object;
}

/**
 * 加解密工具类
 */
export class CryptoUtils {

  // ==================== AES 加解密 ====================

  /**
   * AES 加密
   * @param plaintext 明文
   * @param config 加密配置
   * @returns 密文
   */
  static aesEncrypt(plaintext: string, config: AESConfig): string {
    try {
      // 准备密钥
      const key = CryptoJS.enc.Utf8.parse(config.key);

      // 准备配置
      const cfg: Record<string, object> = {};

      // 设置模式
      const mode = config.mode || 'CBC';
      if (mode === 'CBC') {
        cfg.mode = CryptoJS.mode.CBC;
      } else if (mode === 'CFB') {
        cfg.mode = CryptoJS.mode.CFB;
      } else if (mode === 'CTR') {
        cfg.mode = CryptoJS.mode.CTR;
      } else if (mode === 'OFB') {
        cfg.mode = CryptoJS.mode.OFB;
      } else if (mode === 'ECB') {
        cfg.mode = CryptoJS.mode.ECB;
      }

      // 设置填充
      const padding = config.padding || 'Pkcs7';
      if (padding === 'Pkcs7') {
        cfg.padding = CryptoJS.pad.Pkcs7;
      } else if (padding === 'ZeroPadding') {
        cfg.padding = CryptoJS.pad.ZeroPadding;
      } else if (padding === 'NoPadding') {
        cfg.padding = CryptoJS.pad.NoPadding;
      }

      // 设置 IV（非 ECB 模式）
      if (mode !== 'ECB' && config.iv) {
        cfg.iv = CryptoJS.enc.Utf8.parse(config.iv);
      }

      // 加密
      const encrypted = CryptoJS.AES.encrypt(plaintext, key, cfg);

      // 输出
      const outputFormat = config.outputFormat || 'Base64';
      if (outputFormat === 'Base64') {
        return encrypted.toString();
      } else {
        return encrypted.ciphertext.toString(CryptoJS.enc.Hex);
      }
    } catch (err) {
      const error = err as Error;
      throw new Error(`AES encrypt failed: ${error.message}`);
    }
  }

  /**
   * AES 解密
   * @param ciphertext 密文
   * @param config 解密配置
   * @returns 明文
   */
  static aesDecrypt(ciphertext: string, config: AESConfig): string {
    try {
      // 准备密钥
      const key = CryptoJS.enc.Utf8.parse(config.key);

      // 准备配置
      const cfg: Record<string, object> = {};

      // 设置模式
      const mode = config.mode || 'CBC';
      if (mode === 'CBC') {
        cfg.mode = CryptoJS.mode.CBC;
      } else if (mode === 'CFB') {
        cfg.mode = CryptoJS.mode.CFB;
      } else if (mode === 'CTR') {
        cfg.mode = CryptoJS.mode.CTR;
      } else if (mode === 'OFB') {
        cfg.mode = CryptoJS.mode.OFB;
      } else if (mode === 'ECB') {
        cfg.mode = CryptoJS.mode.ECB;
      }

      // 设置填充
      const padding = config.padding || 'Pkcs7';
      if (padding === 'Pkcs7') {
        cfg.padding = CryptoJS.pad.Pkcs7;
      } else if (padding === 'ZeroPadding') {
        cfg.padding = CryptoJS.pad.ZeroPadding;
      } else if (padding === 'NoPadding') {
        cfg.padding = CryptoJS.pad.NoPadding;
      }

      // 设置 IV（非 ECB 模式）
      if (mode !== 'ECB' && config.iv) {
        cfg.iv = CryptoJS.enc.Utf8.parse(config.iv);
      }

      // 解密
      const decrypted = CryptoJS.AES.decrypt(ciphertext, key, cfg);

      // 输出
      return decrypted.toString(CryptoJS.enc.Utf8);
    } catch (err) {
      const error = err as Error;
      throw new Error(`AES decrypt failed: ${error.message}`);
    }
  }

  // ==================== MD5 哈希 ====================

  /**
   * MD5 哈希
   * @param data 输入数据
   * @returns MD5 哈希值（Hex 格式）
   */
  static md5(data: string): string {
    return CryptoJS.MD5(data).toString(CryptoJS.enc.Hex);
  }

  /**
   * MD5 哈希（Base64 格式）
   * @param data 输入数据
   * @returns MD5 哈希值（Base64 格式）
   */
  static md5Base64(data: string): string {
    return CryptoJS.MD5(data).toString(CryptoJS.enc.Base64);
  }

  // ==================== SHA 哈希 ====================

  /**
   * SHA1 哈希
   * @param data 输入数据
   * @returns SHA1 哈希值（Hex 格式）
   */
  static sha1(data: string): string {
    return CryptoJS.SHA1(data).toString(CryptoJS.enc.Hex);
  }

  /**
   * SHA256 哈希
   * @param data 输入数据
   * @returns SHA256 哈希值（Hex 格式）
   */
  static sha256(data: string): string {
    return CryptoJS.SHA256(data).toString(CryptoJS.enc.Hex);
  }

  /**
   * SHA512 哈希
   * @param data 输入数据
   * @returns SHA512 哈希值（Hex 格式）
   */
  static sha512(data: string): string {
    return CryptoJS.SHA512(data).toString(CryptoJS.enc.Hex);
  }

  // ==================== HMAC ====================

  /**
   * HMAC-MD5
   * @param data 输入数据
   * @param key 密钥
   * @returns HMAC 值（Hex 格式）
   */
  static hmacMd5(data: string, key: string): string {
    return CryptoJS.HmacMD5(data, key).toString(CryptoJS.enc.Hex);
  }

  /**
   * HMAC-SHA1
   * @param data 输入数据
   * @param key 密钥
   * @returns HMAC 值（Hex 格式）
   */
  static hmacSha1(data: string, key: string): string {
    return CryptoJS.HmacSHA1(data, key).toString(CryptoJS.enc.Hex);
  }

  /**
   * HMAC-SHA256
   * @param data 输入数据
   * @param key 密钥
   * @returns HMAC 值（Hex 格式）
   */
  static hmacSha256(data: string, key: string): string {
    return CryptoJS.HmacSHA256(data, key).toString(CryptoJS.enc.Hex);
  }

  /**
   * HMAC-SHA512
   * @param data 输入数据
   * @param key 密钥
   * @returns HMAC 值（Hex 格式）
   */
  static hmacSha512(data: string, key: string): string {
    return CryptoJS.HmacSHA512(data, key).toString(CryptoJS.enc.Hex);
  }

  // ==================== Base64 编解码 ====================

  /**
   * Base64 编码
   * @param data 输入字符串
   * @returns Base64 编码后的字符串
   */
  static base64Encode(data: string): string {
    return CryptoJS.enc.Utf8.parse(data).toString(CryptoJS.enc.Base64);
  }

  /**
   * Base64 解码
   * @param base64 Base64 编码的字符串
   * @returns 解码后的字符串
   */
  static base64Decode(base64: string): string {
    return CryptoJS.enc.Base64.parse(base64).toString(CryptoJS.enc.Utf8);
  }

  /**
   * Hex 编码
   * @param data 输入字符串
   * @returns Hex 编码后的字符串
   */
  static hexEncode(data: string): string {
    return CryptoJS.enc.Utf8.parse(data).toString(CryptoJS.enc.Hex);
  }

  /**
   * Hex 解码
   * @param hex Hex 编码的字符串
   * @returns 解码后的字符串
   */
  static hexDecode(hex: string): string {
    return CryptoJS.enc.Hex.parse(hex).toString(CryptoJS.enc.Utf8);
  }

  // ==================== 字符串编码 ====================

  /**
   * UTF-8 编码
   * @param data 输入字符串
   * @returns WordArray 对象
   */
  static utf8Encode(data: string): object {
    return CryptoJS.enc.Utf8.parse(data);
  }

  /**
   * UTF-8 解码
   * @param wordArray WordArray 对象
   * @returns 解码后的字符串
   */
  static utf8Decode(wordArray: object): string {
    return CryptoJS.enc.Utf8.stringify(wordArray);
  }

  /**
   * Latin1 编码
   * @param data 输入字符串
   * @returns WordArray 对象
   */
  static latin1Encode(data: string): object {
    return CryptoJS.enc.Latin1.parse(data);
  }

  /**
   * Latin1 解码
   * @param wordArray WordArray 对象
   * @returns 解码后的字符串
   */
  static latin1Decode(wordArray: object): string {
    return CryptoJS.enc.Latin1.stringify(wordArray);
  }

  // ==================== 工具方法 ====================

  /**
   * 生成随机字符串
   * @param length 字符串长度
   * @returns 随机字符串（Hex 格式）
   */
  static randomHex(length: number): string {
    const randomBytes = CryptoJS.lib.WordArray.random(length);
    return randomBytes.toString(CryptoJS.enc.Hex);
  }

  /**
   * 生成随机密钥（用于 AES）
   * @param bytes 字节数，默认 16 字节（128 位）
   * @returns 随机密钥（Base64 格式）
   */
  static generateAESKey(bytes: number = 16): string {
    const randomBytes = CryptoJS.lib.WordArray.random(bytes);
    return randomBytes.toString(CryptoJS.enc.Base64);
  }

  /**
   * 生成随机 IV（用于 AES CBC/CFB/OFB 模式）
   * @returns 随机 IV（Base64 格式）
   */
  static generateAESIV(): string {
    const randomBytes = CryptoJS.lib.WordArray.random(16);
    return randomBytes.toString(CryptoJS.enc.Base64);
  }
}
