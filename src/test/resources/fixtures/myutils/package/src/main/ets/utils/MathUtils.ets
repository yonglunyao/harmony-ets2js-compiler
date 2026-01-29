/**
 * 数学工具类
 */
export class MathUtils {
  /**
   * 生成指定范围内的随机整数
   * @param min 最小值
   * @param max 最大值
   * @returns 随机整数
   */
  static randomInt(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  /**
   * 生成指定范围内的随机数
   * @param min 最小值
   * @param max 最大值
   * @returns 随机数
   */
  static random(min: number, max: number): number {
    return Math.random() * (max - min) + min;
  }

  /**
   * 生成随机UUID
   * @returns UUID字符串
   */
  static uuid(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c: string): string => {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }

  /**
   * 限制数值在指定范围内
   * @param value 数值
   * @param min 最小值
   * @param max 最大值
   * @returns 限制后的数值
   */
  static clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
  }

  /**
   * 判断数值是否在指定范围内
   * @param value 数值
   * @param min 最小值
   * @param max 最大值
   * @returns true表示在范围内
   */
  static inRange(value: number, min: number, max: number): boolean {
    return value >= min && value <= max;
  }

  /**
   * 计算百分比
   * @param value 当前值
   * @param total 总值
   * @param precision 小数位数，默认为0
   * @returns 百分比
   */
  static percentage(value: number, total: number, precision: number = 0): number {
    if (total === 0) return 0;
    const factor = Math.pow(10, precision);
    return Math.round(((value / total) * 100) * factor) / factor;
  }

  /**
   * 四舍五入到指定小数位
   * @param value 数值
   * @param precision 小数位数
   * @returns 四舍五入后的数值
   */
  static round(value: number, precision: number = 0): number {
    const factor = Math.pow(10, precision);
    return Math.round(value * factor) / factor;
  }

  /**
   * 判断是否为偶数
   * @param num 数值
   * @returns true表示是偶数
   */
  static isEven(num: number): boolean {
    return num % 2 === 0;
  }

  /**
   * 判断是否为奇数
   * @param num 数值
   * @returns true表示是奇数
   */
  static isOdd(num: number): boolean {
    return num % 2 !== 0;
  }

  /**
   * 格式化数字，添加千位分隔符
   * @param num 数字
   * @param separator 分隔符，默认为','
   * @returns 格式化后的字符串
   */
  static formatNumber(num: number, separator: string = ','): string {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, separator);
  }

  /**
   * 格式化文件大小
   * @param bytes 字节数
   * @param decimals 小数位数，默认为2
   * @returns 格式化后的字符串
   */
  static formatBytes(bytes: number, decimals: number = 2): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
  }

  /**
   * 计算两点之间的距离
   * @param x1 点1的x坐标
   * @param y1 点1的y坐标
   * @param x2 点2的x坐标
   * @param y2 点2的y坐标
   * @returns 距离
   */
  static distance(x1: number, y1: number, x2: number, y2: number): number {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  /**
   * 求数组的平均值
   * @param arr 数字数组
   * @returns 平均值
   */
  static average(arr: Array<number>): number {
    if (arr.length === 0) return 0;
    let sum = 0;
    for (const num of arr) {
      sum = sum + num;
    }
    return sum / arr.length;
  }

  /**
   * 求数组的中位数
   * @param arr 数字数组
   * @returns 中位数
   */
  static median(arr: Array<number>): number {
    if (arr.length === 0) return 0;
    const sorted = arr.slice().sort((a: number, b: number): number => a - b);
    const mid = Math.floor(sorted.length / 2);
    return sorted.length % 2 !== 0 ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2;
  }

  /**
   * 求数组的标准差
   * @param arr 数字数组
   * @returns 标准差
   */
  static standardDeviation(arr: Array<number>): number {
    if (arr.length === 0) return 0;
    const avg = MathUtils.average(arr);
    let sumSquareDiff = 0;
    for (const value of arr) {
      sumSquareDiff = sumSquareDiff + Math.pow(value - avg, 2);
    }
    const avgSquareDiff = sumSquareDiff / arr.length;
    return Math.sqrt(avgSquareDiff);
  }
}
