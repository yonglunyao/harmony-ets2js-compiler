import cryptoFramework from '@ohos.security.cryptoFramework';
import util from '@ohos.util';
import { Logger } from '../utils/Logger';

const base64Helper = new util.Base64Helper();
export class CryptographyUtil {
  TAG = "CryptographyUtil";

  static async encryptAES(plainText /* string */, keyStr /* string */) {
    try {
  const symKeyGenerator = cryptoFramework.createSymKeyGenerator("AES256");
  const keyData = base64Helper.decodeSync(keyStr);
  const symKey = await symKeyGenerator.convertKey({data: keyData});
  const cipher = cryptoFramework.createCipher("AES256|CBC|PKCS7");
  const iv = new Uint8Array(16);
  const ivParamsSpec = {algName: "IvParamsSpec", iv: {data: iv}};
  await cipher.init(cryptoFramework.CryptoMode.ENCRYPT_MODE, symKey, ivParamsSpec);
  const encoder = new util.TextEncoder();
  const plainData = encoder.encodeInto(plainText);
  const cipherData = await cipher.doFinal({data: plainData});
  const result = base64Helper.encodeToStringSync(cipherData.data);
  Logger.success(`AES加密成功，长度: ${result.length}`);
  return result;
} catch (error) {
  Logger.error("AES加密失败", error);
  return "";
}
  }

  static async decryptAES(cipherText /* string */, keyStr /* string */) {
    try {
  const symKeyGenerator = cryptoFramework.createSymKeyGenerator("AES256");
  const keyData = base64Helper.decodeSync(keyStr);
  const symKey = await symKeyGenerator.convertKey({data: keyData});
  const cipher = cryptoFramework.createCipher("AES256|CBC|PKCS7");
  const iv = new Uint8Array(16);
  const ivParamsSpec = {algName: "IvParamsSpec", iv: {data: iv}};
  await cipher.init(cryptoFramework.CryptoMode.DECRYPT_MODE, symKey, ivParamsSpec);
  const cipherDataUint8 = base64Helper.decodeSync(cipherText);
  const plainData = await cipher.doFinal({data: cipherDataUint8});
  const decoder = util.TextDecoder.create("utf-8");
  const result = decoder.decodeToString(plainData.data);
  Logger.success(`AES解密成功，长度: ${result.length}`);
  return result;
} catch (error) {
  Logger.error("AES解密失败", error);
  return "";
}
  }

  static async sha256(input /* string */) {
    try {
  const md = cryptoFramework.createMd("SHA256");
  const encoder = new util.TextEncoder();
  const data = encoder.encodeInto(input);
  await md.update({data: data});
  const hashData = await md.digest();
  const result = base64Helper.encodeToStringSync(hashData.data);
  Logger.success("SHA256哈希计算成功");
  return result;
} catch (error) {
  Logger.error("SHA256哈希计算失败", error);
  return "";
}
  }

  static async md5(input /* string */) {
    try {
  const md = cryptoFramework.createMd("MD5");
  const encoder = new util.TextEncoder();
  const data = encoder.encodeInto(input);
  await md.update({data: data});
  const hashData = await md.digest();
  const result = base64Helper.encodeToStringSync(hashData.data);
  Logger.success("MD5哈希计算成功");
  return result;
} catch (error) {
  Logger.error("MD5哈希计算失败", error);
  return "";
}
  }

  static encodeBase64(input /* string */) {
    try {
  const encoder = new util.TextEncoder();
  const data = encoder.encodeInto(input);
  const result = base64Helper.encodeToStringSync(data);
  Logger.success("Base64编码成功");
  return result;
} catch (error) {
  Logger.error("Base64编码失败", error);
  return "";
}
  }

  static decodeBase64(input /* string */) {
    try {
  const data = base64Helper.decodeSync(input);
  const decoder = util.TextDecoder.create("utf-8");
  const result = decoder.decodeToString(data);
  Logger.success("Base64解码成功");
  return result;
} catch (error) {
  Logger.error("Base64解码失败", error);
  return "";
}
  }

  static async generateAESKey() {
    try {
  const symKeyGenerator = cryptoFramework.createSymKeyGenerator("AES256");
  const symKey = await symKeyGenerator.generateSymKey();
  const keyData = await symKey.getEncoded();
  const result = base64Helper.encodeToStringSync(keyData.data);
  Logger.success("AES密钥生成成功");
  return result;
} catch (error) {
  Logger.error("AES密钥生成失败", error);
  return "";
}
  }

}


//# sourceMappingURL=CryptographyUtil.js.map