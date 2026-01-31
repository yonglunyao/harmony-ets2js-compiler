/**
 * 日期工具类
 */
export class DateUtils {
  /**
   * 日期格式化
   * @param date 日期对象或时间戳
   * @param format 格式字符串，默认为'yyyy-MM-dd HH:mm:ss'
   * @returns 格式化后的日期字符串
   */
  static format(date: Date | number, format: string = 'yyyy-MM-dd HH:mm:ss'): string {
    const d = typeof date === 'number' ? new Date(date) : date;

    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const seconds = String(d.getSeconds()).padStart(2, '0');
    const milliseconds = String(d.getMilliseconds()).padStart(3, '0');

    return format
      .replace(/yyyy/g, year.toString())
      .replace(/MM/g, month)
      .replace(/dd/g, day)
      .replace(/HH/g, hours)
      .replace(/mm/g, minutes)
      .replace(/ss/g, seconds)
      .replace(/SSS/g, milliseconds);
  }

  /**
   * 解析日期字符串
   * @param dateStr 日期字符串
   * @param format 格式字符串
   * @returns 日期对象
   */
  static parse(dateStr: string, format: string = 'yyyy-MM-dd HH:mm:ss'): Date {
    // 简单实现，实际项目中可以使用更复杂的解析逻辑
    return new Date(dateStr);
  }

  /**
   * 获取当前时间戳（毫秒）
   * @returns 时间戳
   */
  static now(): number {
    return Date.now();
  }

  /**
   * 获取当前时间戳（秒）
   * @returns 时间戳（秒）
   */
  static nowInSeconds(): number {
    return Math.floor(Date.now() / 1000);
  }

  /**
   * 添加天数
   * @param date 日期对象
   * @param days 天数
   * @returns 新的日期对象
   */
  static addDays(date: Date, days: number): Date {
    const result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
  }

  /**
   * 添加小时
   * @param date 日期对象
   * @param hours 小时数
   * @returns 新的日期对象
   */
  static addHours(date: Date, hours: number): Date {
    const result = new Date(date);
    result.setHours(result.getHours() + hours);
    return result;
  }

  /**
   * 添加分钟
   * @param date 日期对象
   * @param minutes 分钟数
   * @returns 新的日期对象
   */
  static addMinutes(date: Date, minutes: number): Date {
    const result = new Date(date);
    result.setMinutes(result.getMinutes() + minutes);
    return result;
  }

  /**
   * 添加秒数
   * @param date 日期对象
   * @param seconds 秒数
   * @returns 新的日期对象
   */
  static addSeconds(date: Date, seconds: number): Date {
    const result = new Date(date);
    result.setSeconds(result.getSeconds() + seconds);
    return result;
  }

  /**
   * 计算两个日期之间的天数差
   * @param date1 日期1
   * @param date2 日期2
   * @returns 天数差
   */
  static daysBetween(date1: Date, date2: Date): number {
    const oneDay = 24 * 60 * 60 * 1000;
    return Math.round((date2.getTime() - date1.getTime()) / oneDay);
  }

  /**
   * 计算两个日期之间的小时差
   * @param date1 日期1
   * @param date2 日期2
   * @returns 小时差
   */
  static hoursBetween(date1: Date, date2: Date): number {
    return (date2.getTime() - date1.getTime()) / (1000 * 60 * 60);
  }

  /**
   * 判断是否为今天
   * @param date 日期对象
   * @returns true表示是今天
   */
  static isToday(date: Date): boolean {
    const today = new Date();
    return date.getFullYear() === today.getFullYear() &&
      date.getMonth() === today.getMonth() &&
      date.getDate() === today.getDate();
  }

  /**
   * 判断是否为本周
   * @param date 日期对象
   * @returns true表示是本周
   */
  static isThisWeek(date: Date): boolean {
    const today = new Date();
    const firstDay = new Date(today.setDate(today.getDate() - today.getDay()));
    const lastDay = new Date(today.setDate(today.getDate() - today.getDay() + 6));
    return date >= firstDay && date <= lastDay;
  }

  /**
   * 判断是否为本月
   * @param date 日期对象
   * @returns true表示是本月
   */
  static isThisMonth(date: Date): boolean {
    const today = new Date();
    return date.getFullYear() === today.getFullYear() &&
      date.getMonth() === today.getMonth();
  }

  /**
   * 获取日期所在周的第一天（周日）
   * @param date 日期对象
   * @returns 周的第一天
   */
  static getFirstDayOfWeek(date: Date): Date {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day;
    return new Date(d.setDate(diff));
  }

  /**
   * 获取日期所在周的最后一天（周六）
   * @param date 日期对象
   * @returns 周的最后一天
   */
  static getLastDayOfWeek(date: Date): Date {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day + 6;
    return new Date(d.setDate(diff));
  }

  /**
   * 获取月份的第一天
   * @param date 日期对象
   * @returns 月份的第一天
   */
  static getFirstDayOfMonth(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth(), 1);
  }

  /**
   * 获取月份的最后一天
   * @param date 日期对象
   * @returns 月份的最后一天
   */
  static getLastDayOfMonth(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0);
  }

  /**
   * 获取星期几的名称
   * @param date 日期对象
   * @param locale 语言环境，默认为中文
   * @returns 星期几的名称
   */
  static getWeekdayName(date: Date, locale: string = 'zh-CN'): string {
    if (locale === 'zh-CN') {
      const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      return days[date.getDay()];
    } else {
      const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
      return days[date.getDay()];
    }
  }

  /**
   * 日期比较
   * @param date1 日期1
   * @param date2 日期2
   * @returns -1表示date1<date2，0表示相等，1表示date1>date2
   */
  static compare(date1: Date, date2: Date): number {
    const time1 = date1.getTime();
    const time2 = date2.getTime();
    if (time1 < time2) return -1;
    if (time1 > time2) return 1;
    return 0;
  }

  /**
   * 时间戳转换为相对时间描述（如：刚刚、5分钟前、2小时前等）
   * @param timestamp 时间戳（毫秒）
   * @returns 相对时间描述
   */
  static getRelativeTime(timestamp: number): string {
    const now = Date.now();
    const diff = now - timestamp;

    if (diff < 60 * 1000) {
      return '刚刚';
    } else if (diff < 60 * 60 * 1000) {
      const minutes = Math.floor(diff / (60 * 1000));
      return minutes + '分钟前';
    } else if (diff < 24 * 60 * 60 * 1000) {
      const hours = Math.floor(diff / (60 * 60 * 1000));
      return hours + '小时前';
    } else if (diff < 30 * 24 * 60 * 60 * 1000) {
      const days = Math.floor(diff / (24 * 60 * 60 * 1000));
      return days + '天前';
    } else if (diff < 12 * 30 * 24 * 60 * 60 * 1000) {
      const months = Math.floor(diff / (30 * 24 * 60 * 60 * 1000));
      return months + '个月前';
    } else {
      const years = Math.floor(diff / (365 * 24 * 60 * 60 * 1000));
      return years + '年前';
    }
  }

  /**
   * 倒计时格式化
   * @param milliseconds 毫秒数
   * @returns 格式化的倒计时字符串（HH:mm:ss）
   */
  static formatCountdown(milliseconds: number): string {
    const totalSeconds = Math.floor(milliseconds / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    if (hours > 0) {
      return String(hours).padStart(2, '0') + ':' +
        String(minutes).padStart(2, '0') + ':' +
        String(seconds).padStart(2, '0');
    }
    return String(minutes).padStart(2, '0') + ':' +
      String(seconds).padStart(2, '0');
  }
}
