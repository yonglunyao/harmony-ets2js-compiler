/**
 * 字符串工具类
 */
export class StringUtils {
  /**
   * 判断字符串是否为空或null/undefined
   * @param str 待检查的字符串
   * @returns true表示为空，false表示不为空
   */
  static isEmpty(str: string | null | undefined): boolean {
    return str === null || str === undefined || str.length === 0;
  }

  /**
   * 判断字符串是否不为空
   * @param str 待检查的字符串
   * @returns true表示不为空，false表示为空
   */
  static isNotEmpty(str: string | null | undefined): boolean {
    return !StringUtils.isEmpty(str);
  }

  /**
   * 判断字符串是否为空白（仅包含空格、制表符、换行符等）
   * @param str 待检查的字符串
   * @returns true表示为空白，false表示不为空白
   */
  static isBlank(str: string | null | undefined): boolean {
    if (StringUtils.isEmpty(str)) {
      return true;
    }
    return str!.trim().length === 0;
  }

  /**
   * 判断字符串是否不为空白
   * @param str 待检查的字符串
   * @returns true表示不为空白，false表示为空白
   */
  static isNotBlank(str: string | null | undefined): boolean {
    return !StringUtils.isBlank(str);
  }

  /**
   * 去除字符串两端空格
   * @param str 待处理的字符串
   * @returns 处理后的字符串
   */
  static trim(str: string | null | undefined): string {
    if (StringUtils.isEmpty(str)) {
      return '';
    }
    return str!.trim();
  }

  /**
   * 字符串截取，超出长度用省略号代替
   * @param str 待处理的字符串
   * @param maxLength 最大长度
   * @param suffix 后缀，默认为'...'
   * @returns 处理后的字符串
   */
  static truncate(str: string, maxLength: number, suffix: string = '...'): string {
    if (StringUtils.isEmpty(str) || str!.length <= maxLength) {
      return str || '';
    }
    return str!.substring(0, maxLength) + suffix;
  }

  /**
   * 将字符串首字母大写
   * @param str 待处理的字符串
   * @returns 处理后的字符串
   */
  static capitalize(str: string): string {
    if (StringUtils.isEmpty(str)) {
      return '';
    }
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  /**
   * 将字符串首字母小写
   * @param str 待处理的字符串
   * @returns 处理后的字符串
   */
  static uncapitalize(str: string): string {
    if (StringUtils.isEmpty(str)) {
      return '';
    }
    return str.charAt(0).toLowerCase() + str.slice(1);
  }

  /**
   * 驼峰转下划线
   * @param str 待处理的字符串
   * @returns 处理后的字符串
   */
  static camelToSnake(str: string): string {
    return str.replace(/([A-Z])/g, '_$1').toLowerCase();
  }

  /**
   * 下划线转驼峰
   * @param str 待处理的字符串
   * @returns 处理后的字符串
   */
  static snakeToCamel(str: string): string {
    return str.replace(/_([a-z])/g, (match: string, letter: string): string => letter.toUpperCase());
  }

  /**
   * 生成随机字符串
   * @param length 字符串长度
   * @param charset 字符集，默认为字母和数字
   * @returns 随机字符串
   */
  static random(length: number, charset: string = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'): string {
    let result = '';
    for (let i = 0; i < length; i++) {
      result += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    return result;
  }

  /**
   * 判断字符串是否以指定前缀开头
   * @param str 待检查的字符串
   * @param prefix 前缀
   * @param ignoreCase 是否忽略大小写
   * @returns true表示以指定前缀开头
   */
  static startsWith(str: string, prefix: string, ignoreCase: boolean = false): boolean {
    if (ignoreCase) {
      return str.toLowerCase().startsWith(prefix.toLowerCase());
    }
    return str.startsWith(prefix);
  }

  /**
   * 判断字符串是否以指定后缀结尾
   * @param str 待检查的字符串
   * @param suffix 后缀
   * @param ignoreCase 是否忽略大小写
   * @returns true表示以指定后缀结尾
   */
  static endsWith(str: string, suffix: string, ignoreCase: boolean = false): boolean {
    if (ignoreCase) {
      return str.toLowerCase().endsWith(suffix.toLowerCase());
    }
    return str.endsWith(suffix);
  }

  /**
   * 字符串分割，自动过滤空字符串
   * @param str 待分割的字符串
   * @param delimiter 分隔符
   * @returns 分割后的字符串数组
   */
  static splitAndFilter(str: string, delimiter: string): Array<string> {
    if (StringUtils.isEmpty(str)) {
      return [];
    }
    const parts = str!.split(delimiter);
    const result: Array<string> = [];
    for (const s of parts) {
      if (StringUtils.isNotEmpty(s) && StringUtils.isNotBlank(s)) {
        result.push(s);
      }
    }
    return result;
  }

  /**
   * 数字格式化，添加千位分隔符
   * @param num 数字
   * @returns 格式化后的字符串
   */
  static formatNumber(num: number): string {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

  /**
   * 隐藏字符串中间部分（用于手机号、身份证等敏感信息）
   * @param str 待处理的字符串
   * @param startLen 开始保留长度
   * @param endLen 结尾保留长度
   * @param maskChar 掩码字符，默认为'*'
   * @returns 处理后的字符串
   */
  static mask(str: string, startLen: number = 3, endLen: number = 4, maskChar: string = '*'): string {
    if (StringUtils.isEmpty(str) || str!.length <= startLen + endLen) {
      return str || '';
    }
    const start = str!.substring(0, startLen);
    const end = str!.substring(str!.length - endLen);
    let mask = '';
    for (let i = 0; i < str!.length - startLen - endLen; i++) {
      mask += maskChar;
    }
    return start + mask + end;
  }
}
