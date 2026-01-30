export class ValidationUtils {
  static isPhone(phone /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(phone);
  }

  static isEmail(email /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(email);
  }

  static isIdCard(idCard /* string */) {
    const reg18 = {"kind":14,"kindName":"RegularExpressionLiteral"}
    const reg15 = {"kind":14,"kindName":"RegularExpressionLiteral"}
    if (!reg18.test(idCard) && !reg15.test(idCard)) {
      return false;
    }
    if (idCard.length === 18) {
      const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
      const checkCodes = ["1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"];
      let sum = 0;
      for (let i = 0; i < 17; i++) {
  sum = sum + parseInt(idCard.charAt(i)) * weights[i];
}
      const checkCode = checkCodes[sum % 11];
      return checkCode === idCard.charAt(17).toUpperCase();
    }
    return true;
  }

  static isUrl(url /* string */) {
    try {
  const reg = {"kind":14,"kindName":"RegularExpressionLiteral"};
  return reg.test(url);
} catch {
  return false;
}
  }

  static isIPv4(ip /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(ip);
  }

  static isIPv6(ip /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(ip);
  }

  static isPostalCode(postalCode /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(postalCode);
  }

  static isNumber(num /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(num);
  }

  static isInteger(num /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(num);
  }

  static isPositiveInteger(num /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(num);
  }

  static isBankCard(cardNo /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(cardNo);
  }

  static isPlateNo(plateNo /* string */) {
    const regNormal = {"kind":14,"kindName":"RegularExpressionLiteral"}
    const regNew = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return regNormal.test(plateNo) || regNew.test(plateNo);
  }

  static isChinese(str /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(str);
  }

  static isLetter(str /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(str);
  }

  static isLowerCase(str /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(str);
  }

  static isUpperCase(str /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(str);
  }

  static getPasswordStrength(password /* string */) {
    let strength = 0;
    if (password.length >= 8) {
      strength++;
    }
    if ({"kind":14,"kindName":"RegularExpressionLiteral"}.test(password)) {
      strength++;
    }
    if ({"kind":14,"kindName":"RegularExpressionLiteral"}.test(password)) {
      strength++;
    }
    if ({"kind":14,"kindName":"RegularExpressionLiteral"}.test(password)) {
      strength++;
    }
    if ({"kind":14,"kindName":"RegularExpressionLiteral"}.test(password)) {
      strength++;
    }
    return strength > 4 ? 4 : strength;
  }

  static isStrongPassword(password /* string */) {
    return password.length >= 8 && {"kind":14,"kindName":"RegularExpressionLiteral"}.test(password) && {"kind":14,"kindName":"RegularExpressionLiteral"}.test(password) && {"kind":14,"kindName":"RegularExpressionLiteral"}.test(password) && {"kind":14,"kindName":"RegularExpressionLiteral"}.test(password);
  }

  static isLengthInRange(str /* string */, min /* number */, max /* number */) {
    return str.length >= min && str.length <= max;
  }

  static isHexColor(color /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(color);
  }

  static isRGB(rgb /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    const match = rgb.match(reg);
    if (match === null || match.length < 4) {
      return false;
    }
    const r = parseInt(match[1]);
    const g = parseInt(match[2]);
    const b = parseInt(match[3]);
    return r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255;
  }

  static isDate(date /* string */, format /* string */) {
    if (format === "yyyy-MM-dd") {
      const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
      if (!reg.test(date)) {
        return false;
      }
      const d = new Date(date);
      return !isNaN(d.getTime());
    }
    return false;
  }

  static isJSON(jsonStr /* string */) {
    try {
  JSON.parse(jsonStr);
  return true;
} catch {
  return false;
}
  }

  static isQQ(qq /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(qq);
  }

  static isWechat(wechat /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(wechat);
  }

  static isMAC(mac /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(mac);
  }

  static isUUID(uuid /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(uuid);
  }

  static isUsername(username /* string */) {
    const reg = {"kind":14,"kindName":"RegularExpressionLiteral"}
    return reg.test(username);
  }

}


//# sourceMappingURL=ValidationUtils.js.map