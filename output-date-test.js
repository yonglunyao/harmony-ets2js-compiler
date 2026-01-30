export class DateUtils {
  static format(date /* Date | number */, format /* string */) {
    const d = typeof date === "number" ? new Date(date) : date;
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const seconds = String(d.getSeconds()).padStart(2, '0');
    const milliseconds = String(d.getMilliseconds()).padStart(3, '0');
    return format.replace(/yyyy/g, year.toString()).replace(/MM/g, month).replace(/dd/g, day).replace(/HH/g, hours).replace(/mm/g, minutes).replace(/ss/g, seconds).replace(/SSS/g, milliseconds);
  }

  static parse(dateStr /* string */, format /* string */) {
    return new Date(dateStr);
  }

  static now() {
    return Date.now();
  }

  static nowInSeconds() {
    return Math.floor(Date.now() / 1000);
  }

  static addDays(date /* Date */, days /* number */) {
    const result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
  }

  static addHours(date /* Date */, hours /* number */) {
    const result = new Date(date);
    result.setHours(result.getHours() + hours);
    return result;
  }

  static addMinutes(date /* Date */, minutes /* number */) {
    const result = new Date(date);
    result.setMinutes(result.getMinutes() + minutes);
    return result;
  }

  static addSeconds(date /* Date */, seconds /* number */) {
    const result = new Date(date);
    result.setSeconds(result.getSeconds() + seconds);
    return result;
  }

  static daysBetween(date1 /* Date */, date2 /* Date */) {
    const oneDay = 24 * 60 * 60 * 1000;
    return Math.round((date2.getTime() - date1.getTime()) / oneDay);
  }

  static hoursBetween(date1 /* Date */, date2 /* Date */) {
    return (date2.getTime() - date1.getTime()) / (1000 * 60 * 60);
  }

  static isToday(date /* Date */) {
    const today = new Date();
    return date.getFullYear() === today.getFullYear() && date.getMonth() === today.getMonth() && date.getDate() === today.getDate();
  }

  static isThisWeek(date /* Date */) {
    const today = new Date();
    const firstDay = new Date(today.setDate(today.getDate() - today.getDay()));
    const lastDay = new Date(today.setDate(today.getDate() - today.getDay() + 6));
    return date >= firstDay && date <= lastDay;
  }

  static isThisMonth(date /* Date */) {
    const today = new Date();
    return date.getFullYear() === today.getFullYear() && date.getMonth() === today.getMonth();
  }

  static getFirstDayOfWeek(date /* Date */) {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day;
    return new Date(d.setDate(diff));
  }

  static getLastDayOfWeek(date /* Date */) {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day + 6;
    return new Date(d.setDate(diff));
  }

  static getFirstDayOfMonth(date /* Date */) {
    return new Date(date.getFullYear(), date.getMonth(), 1);
  }

  static getLastDayOfMonth(date /* Date */) {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0);
  }

  static getWeekdayName(date /* Date */, locale /* string */) {
    If.create();
    if (locale === "zh-CN") {
      If.branchId(0);
      const days = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
      return days[date.getDay()];
    }
    else {
      If.branchId(1);
      const days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
      return days[date.getDay()];
    }
    If.pop();
  }

  static compare(date1 /* Date */, date2 /* Date */) {
    const time1 = date1.getTime();
    const time2 = date2.getTime();
    If.create();
    if (time1 < time2) {
      If.branchId(0);
      return -1;
    }
    If.pop();
    If.create();
    if (time1 > time2) {
      If.branchId(0);
      return 1;
    }
    If.pop();
    return 0;
  }

  static getRelativeTime(timestamp /* number */) {
    const now = Date.now();
    const diff = now - timestamp;
    If.create();
    if (diff < 60 * 1000) {
      If.branchId(0);
      return "刚刚";
    }
    else {
      If.branchId(1);
      If.create();
      if (diff < 60 * 60 * 1000) {
        If.branchId(0);
        const minutes = Math.floor(diff / (60 * 1000));
        return minutes + "分钟前";
      }
      else {
        If.branchId(1);
        If.create();
        if (diff < 24 * 60 * 60 * 1000) {
          If.branchId(0);
          const hours = Math.floor(diff / (60 * 60 * 1000));
          return hours + "小时前";
        }
        else {
          If.branchId(1);
          If.create();
          if (diff < 30 * 24 * 60 * 60 * 1000) {
            If.branchId(0);
            const days = Math.floor(diff / (24 * 60 * 60 * 1000));
            return days + "天前";
          }
          else {
            If.branchId(1);
            If.create();
            if (diff < 12 * 30 * 24 * 60 * 60 * 1000) {
              If.branchId(0);
              const months = Math.floor(diff / (30 * 24 * 60 * 60 * 1000));
              return months + "个月前";
            }
            else {
              If.branchId(1);
              const years = Math.floor(diff / (365 * 24 * 60 * 60 * 1000));
              return years + "年前";
            }
            If.pop();
          }
          If.pop();
        }
        If.pop();
      }
      If.pop();
    }
    If.pop();
  }

  static formatCountdown(milliseconds /* number */) {
    const totalSeconds = Math.floor(milliseconds / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
    If.create();
    if (hours > 0) {
      If.branchId(0);
      return String(hours).padStart(2, '0') + ":" + String(minutes).padStart(2, '0') + ":" + String(seconds).padStart(2, '0');
    }
    If.pop();
    return String(minutes).padStart(2, '0') + ":" + String(seconds).padStart(2, '0');
  }

}


//# sourceMappingURL=output-date-test.js.map