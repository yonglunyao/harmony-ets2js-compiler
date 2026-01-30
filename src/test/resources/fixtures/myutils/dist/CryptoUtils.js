import { CryptoJS } from '@ohos/crypto-js';

export class CryptoUtils {
  static aesEncrypt(plaintext /* string */, config /* AESConfig */) {
    try {
  const key = CryptoJS.enc.Utf8.parse(config.key);
  const cfg = {};
  const mode = config.mode || "CBC";
  if (mode === "CBC") {
  cfg.mode = CryptoJS.mode.CBC;
} else {
if (mode === "CFB") {
  cfg.mode = CryptoJS.mode.CFB;
} else {
if (mode === "CTR") {
  cfg.mode = CryptoJS.mode.CTR;
} else {
if (mode === "OFB") {
  cfg.mode = CryptoJS.mode.OFB;
} else {
if (mode === "ECB") {
  cfg.mode = CryptoJS.mode.ECB;
}
}
}
}
}
  const padding = config.padding || "Pkcs7";
  if (padding === "Pkcs7") {
  cfg.padding = CryptoJS.pad.Pkcs7;
} else {
if (padding === "ZeroPadding") {
  cfg.padding = CryptoJS.pad.ZeroPadding;
} else {
if (padding === "NoPadding") {
  cfg.padding = CryptoJS.pad.NoPadding;
}
}
}
  if (mode !== "ECB" && config.iv) {
  cfg.iv = CryptoJS.enc.Utf8.parse(config.iv);
}
  const encrypted = CryptoJS.AES.encrypt(plaintext, key, cfg);
  const outputFormat = config.outputFormat || "Base64";
  if (outputFormat === "Base64") {
  return encrypted.toString();
} else {
  return encrypted.ciphertext.toString(CryptoJS.enc.Hex);
}
} catch (err) {
  const error = err;
  throw {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Error","text":"Error"},"arguments":[{"kind":229,"kindName":"TemplateExpression"}]}
}
  }

  static aesDecrypt(ciphertext /* string */, config /* AESConfig */) {
    try {
  const key = CryptoJS.enc.Utf8.parse(config.key);
  const cfg = {};
  const mode = config.mode || "CBC";
  if (mode === "CBC") {
  cfg.mode = CryptoJS.mode.CBC;
} else {
if (mode === "CFB") {
  cfg.mode = CryptoJS.mode.CFB;
} else {
if (mode === "CTR") {
  cfg.mode = CryptoJS.mode.CTR;
} else {
if (mode === "OFB") {
  cfg.mode = CryptoJS.mode.OFB;
} else {
if (mode === "ECB") {
  cfg.mode = CryptoJS.mode.ECB;
}
}
}
}
}
  const padding = config.padding || "Pkcs7";
  if (padding === "Pkcs7") {
  cfg.padding = CryptoJS.pad.Pkcs7;
} else {
if (padding === "ZeroPadding") {
  cfg.padding = CryptoJS.pad.ZeroPadding;
} else {
if (padding === "NoPadding") {
  cfg.padding = CryptoJS.pad.NoPadding;
}
}
}
  if (mode !== "ECB" && config.iv) {
  cfg.iv = CryptoJS.enc.Utf8.parse(config.iv);
}
  const decrypted = CryptoJS.AES.decrypt(ciphertext, key, cfg);
  return decrypted.toString(CryptoJS.enc.Utf8);
} catch (err) {
  const error = err;
  throw {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Error","text":"Error"},"arguments":[{"kind":229,"kindName":"TemplateExpression"}]}
}
  }

  static md5(data /* string */) {
    return CryptoJS.MD5(data).toString(CryptoJS.enc.Hex);
  }

  static md5Base64(data /* string */) {
    return CryptoJS.MD5(data).toString(CryptoJS.enc.Base64);
  }

  static sha1(data /* string */) {
    return CryptoJS.SHA1(data).toString(CryptoJS.enc.Hex);
  }

  static sha256(data /* string */) {
    return CryptoJS.SHA256(data).toString(CryptoJS.enc.Hex);
  }

  static sha512(data /* string */) {
    return CryptoJS.SHA512(data).toString(CryptoJS.enc.Hex);
  }

  static hmacMd5(data /* string */, key /* string */) {
    return CryptoJS.HmacMD5(data, key).toString(CryptoJS.enc.Hex);
  }

  static hmacSha1(data /* string */, key /* string */) {
    return CryptoJS.HmacSHA1(data, key).toString(CryptoJS.enc.Hex);
  }

  static hmacSha256(data /* string */, key /* string */) {
    return CryptoJS.HmacSHA256(data, key).toString(CryptoJS.enc.Hex);
  }

  static hmacSha512(data /* string */, key /* string */) {
    return CryptoJS.HmacSHA512(data, key).toString(CryptoJS.enc.Hex);
  }

  static base64Encode(data /* string */) {
    return CryptoJS.enc.Utf8.parse(data).toString(CryptoJS.enc.Base64);
  }

  static base64Decode(base64 /* string */) {
    return CryptoJS.enc.Base64.parse(base64).toString(CryptoJS.enc.Utf8);
  }

  static hexEncode(data /* string */) {
    return CryptoJS.enc.Utf8.parse(data).toString(CryptoJS.enc.Hex);
  }

  static hexDecode(hex /* string */) {
    return CryptoJS.enc.Hex.parse(hex).toString(CryptoJS.enc.Utf8);
  }

  static utf8Encode(data /* string */) {
    return CryptoJS.enc.Utf8.parse(data);
  }

  static utf8Decode(wordArray /* object */) {
    return CryptoJS.enc.Utf8.stringify(wordArray);
  }

  static latin1Encode(data /* string */) {
    return CryptoJS.enc.Latin1.parse(data);
  }

  static latin1Decode(wordArray /* object */) {
    return CryptoJS.enc.Latin1.stringify(wordArray);
  }

  static randomHex(length /* number */) {
    const randomBytes = CryptoJS.lib.WordArray.random(length);
    return randomBytes.toString(CryptoJS.enc.Hex);
  }

  static generateAESKey(bytes /* number */) {
    const randomBytes = CryptoJS.lib.WordArray.random(bytes);
    return randomBytes.toString(CryptoJS.enc.Base64);
  }

  static generateAESIV() {
    const randomBytes = CryptoJS.lib.WordArray.random(16);
    return randomBytes.toString(CryptoJS.enc.Base64);
  }

}


//# sourceMappingURL=CryptoUtils.js.map