/**
 * 验证工具类
 */
export class ValidationUtils {
  /**
   * 验证是否为有效的手机号（中国大陆）
   * @param phone 手机号
   * @returns true表示有效
   */
  static isPhone(phone: string): boolean {
    const reg = /^1[3-9]\d{9}$/;
    return reg.test(phone);
  }

  /**
   * 验证是否为有效的邮箱地址
   * @param email 邮箱地址
   * @returns true表示有效
   */
  static isEmail(email: string): boolean {
    const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return reg.test(email);
  }

  /**
   * 验证是否为有效的身份证号（中国大陆）
   * @param idCard 身份证号
   * @returns true表示有效
   */
  static isIdCard(idCard: string): boolean {
    // 18位身份证号正则
    const reg18 = /^[1-9]\d{5}(19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/;
    // 15位身份证号正则
    const reg15 = /^[1-9]\d{5}\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}$/;

    if (!reg18.test(idCard) && !reg15.test(idCard)) {
      return false;
    }

    // 18位身份证号校验位验证
    if (idCard.length === 18) {
      const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
      const checkCodes = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];
      let sum = 0;
      for (let i = 0; i < 17; i++) {
        sum = sum + parseInt(idCard.charAt(i)) * weights[i];
      }
      const checkCode = checkCodes[sum % 11];
      return checkCode === idCard.charAt(17).toUpperCase();
    }

    return true;
  }

  /**
   * 验证是否为有效的URL
   * @param url URL地址
   * @returns true表示有效
   */
  static isUrl(url: string): boolean {
    try {
      // 简单的URL验证
      const reg = /^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$/i;
      return reg.test(url);
    } catch {
      return false;
    }
  }

  /**
   * 验证是否为有效的IP地址（IPv4）
   * @param ip IP地址
   * @returns true表示有效
   */
  static isIPv4(ip: string): boolean {
    const reg = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;
    return reg.test(ip);
  }

  /**
   * 验证是否为有效的IP地址（IPv6）
   * @param ip IP地址
   * @returns true表示有效
   */
  static isIPv6(ip: string): boolean {
    // 简化版IPv6验证
    const reg = /^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$/;
    return reg.test(ip);
  }

  /**
   * 验证是否为有效的邮政编码（中国大陆）
   * @param postalCode 邮政编码
   * @returns true表示有效
   */
  static isPostalCode(postalCode: string): boolean {
    const reg = /^[1-9]\d{5}$/;
    return reg.test(postalCode);
  }

  /**
   * 验证是否为数字（整数或小数）
   * @param num 数字字符串
   * @returns true表示是数字
   */
  static isNumber(num: string): boolean {
    const reg = /^-?\d+(\.\d+)?$/;
    return reg.test(num);
  }

  /**
   * 验证是否为整数
   * @param num 数字字符串
   * @returns true表示是整数
   */
  static isInteger(num: string): boolean {
    const reg = /^-?\d+$/;
    return reg.test(num);
  }

  /**
   * 验证是否为正整数
   * @param num 数字字符串
   * @returns true表示是正整数
   */
  static isPositiveInteger(num: string): boolean {
    const reg = /^[1-9]\d*$/;
    return reg.test(num);
  }

  /**
   * 验证是否为有效的银行卡号
   * @param cardNo 银行卡号
   * @returns true表示有效
   */
  static isBankCard(cardNo: string): boolean {
    const reg = /^[1-9]\d{12,18}$/;
    return reg.test(cardNo);
  }

  /**
   * 验证是否为有效的车牌号（中国大陆）
   * @param plateNo 车牌号
   * @returns true表示有效
   */
  static isPlateNo(plateNo: string): boolean {
    // 普通车牌号
    const regNormal = /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-Z0-9]{5,6}$/;
    // 新能源车牌号
    const regNew = /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-Z0-9]{5}[DF][A-Z0-9]$/;
    return regNormal.test(plateNo) || regNew.test(plateNo);
  }

  /**
   * 验证是否为中文字符
   * @param str 字符串
   * @returns true表示全是中文字符
   */
  static isChinese(str: string): boolean {
    const reg = /^[\u4e00-\u9fa5]+$/;
    return reg.test(str);
  }

  /**
   * 验证是否为英文字母
   * @param str 字符串
   * @returns true表示全是英文字母
   */
  static isLetter(str: string): boolean {
    const reg = /^[a-zA-Z]+$/;
    return reg.test(str);
  }

  /**
   * 验证是否为小写字母
   * @param str 字符串
   * @returns true表示全是小写字母
   */
  static isLowerCase(str: string): boolean {
    const reg = /^[a-z]+$/;
    return reg.test(str);
  }

  /**
   * 验证是否为大写字母
   * @param str 字符串
   * @returns true表示全是大写字母
   */
  static isUpperCase(str: string): boolean {
    const reg = /^[A-Z]+$/;
    return reg.test(str);
  }

  /**
   * 验证密码强度
   * @param password 密码
   * @returns 密码强度等级（0-4，0表示弱，4表示强）
   */
  static getPasswordStrength(password: string): number {
    let strength = 0;
    if (password.length >= 8) {
      strength++;
    }
    if (/[a-z]/.test(password)) {
      strength++;
    }
    if (/[A-Z]/.test(password)) {
      strength++;
    }
    if (/[0-9]/.test(password)) {
      strength++;
    }
    if (/[^a-zA-Z0-9]/.test(password)) {
      strength++;
    }
    return strength > 4 ? 4 : strength;
  }

  /**
   * 验证密码是否包含大小写字母、数字和特殊字符
   * @param password 密码
   * @returns true表示符合要求
   */
  static isStrongPassword(password: string): boolean {
    return password.length >= 8 &&
      /[a-z]/.test(password) &&
      /[A-Z]/.test(password) &&
      /[0-9]/.test(password) &&
      /[^a-zA-Z0-9]/.test(password);
  }

  /**
   * 验证字符串长度是否在指定范围内
   * @param str 字符串
   * @param min 最小长度
   * @param max 最大长度
   * @returns true表示在范围内
   */
  static isLengthInRange(str: string, min: number, max: number): boolean {
    return str.length >= min && str.length <= max;
  }

  /**
   * 验证是否为有效的十六进制颜色值
   * @param color 颜色值
   * @returns true表示有效
   */
  static isHexColor(color: string): boolean {
    const reg = /^#?([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$/;
    return reg.test(color);
  }

  /**
   * 验证是否为有效的RGB颜色值
   * @param rgb RGB颜色值
   * @returns true表示有效
   */
  static isRGB(rgb: string): boolean {
    const reg = /^rgb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)$/;
    const match = rgb.match(reg);
    if (match === null || match.length < 4) {
      return false;
    }
    const r = parseInt(match[1]);
    const g = parseInt(match[2]);
    const b = parseInt(match[3]);
    return r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255;
  }

  /**
   * 验证是否为有效的日期格式
   * @param date 日期字符串
   * @param format 格式，默认为'yyyy-MM-dd'
   * @returns true表示有效
   */
  static isDate(date: string, format: string = 'yyyy-MM-dd'): boolean {
    if (format === 'yyyy-MM-dd') {
      const reg = /^\d{4}-\d{2}-\d{2}$/;
      if (!reg.test(date)) {
        return false;
      }
      const d = new Date(date);
      return !isNaN(d.getTime());
    }
    return false;
  }

  /**
   * 验证是否为有效的JSON字符串
   * @param jsonStr JSON字符串
   * @returns true表示有效
   */
  static isJSON(jsonStr: string): boolean {
    try {
      JSON.parse(jsonStr);
      return true;
    } catch {
      return false;
    }
  }

  /**
   * 验证是否为有效的QQ号
   * @param qq QQ号
   * @returns true表示有效
   */
  static isQQ(qq: string): boolean {
    const reg = /^[1-9]\d{4,10}$/;
    return reg.test(qq);
  }

  /**
   * 验证是否为有效的微信号
   * @param wechat 微信号
   * @returns true表示有效
   */
  static isWechat(wechat: string): boolean {
    const reg = /^[a-zA-Z][-_a-zA-Z0-9]{5,19}$/;
    return reg.test(wechat);
  }

  /**
   * 验证是否为有效的MAC地址
   * @param mac MAC地址
   * @returns true表示有效
   */
  static isMAC(mac: string): boolean {
    const reg = /^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$/;
    return reg.test(mac);
  }

  /**
   * 验证是否为有效的UUID
   * @param uuid UUID
   * @returns true表示有效
   */
  static isUUID(uuid: string): boolean {
    const reg = /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/;
    return reg.test(uuid);
  }

  /**
   * 验证是否为有效的用户名（字母开头，允许字母数字下划线，4-16位）
   * @param username 用户名
   * @returns true表示有效
   */
  static isUsername(username: string): boolean {
    const reg = /^[a-zA-Z][a-zA-Z0-9_]{3,15}$/;
    return reg.test(username);
  }
}
