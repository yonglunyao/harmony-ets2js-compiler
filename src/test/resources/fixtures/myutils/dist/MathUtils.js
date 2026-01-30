export class MathUtils {
  static randomInt(min /* number */, max /* number */) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  static random(min /* number */, max /* number */) {
    return Math.random() * (max - min) + min;
  }

  static uuid() {
    return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
    const r = Math.random() * 16 | 0;
    const v = c === "x" ? r : (r & 3 | 8);
    return v.toString(16);
  });
  }

  static clamp(value /* number */, min /* number */, max /* number */) {
    return Math.min(Math.max(value, min), max);
  }

  static inRange(value /* number */, min /* number */, max /* number */) {
    return value >= min && value <= max;
  }

  static percentage(value /* number */, total /* number */, precision /* number */) {
    if (total === 0) {
      return 0;
    }
    const factor = Math.pow(10, precision);
    return Math.round(((value / total) * 100) * factor) / factor;
  }

  static round(value /* number */, precision /* number */) {
    const factor = Math.pow(10, precision);
    return Math.round(value * factor) / factor;
  }

  static isEven(num /* number */) {
    return num % 2 === 0;
  }

  static isOdd(num /* number */) {
    return num % 2 !== 0;
  }

  static formatNumber(num /* number */, separator /* string */) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, separator);
  }

  static formatBytes(bytes /* number */, decimals /* number */) {
    if (bytes === 0) {
      return "0 Bytes";
    }
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ["Bytes", "KB", "MB", "GB", "TB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + " " + sizes[i];
  }

  static distance(x1 /* number */, y1 /* number */, x2 /* number */, y2 /* number */) {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  static average(arr /* Array<number> */) {
    if (arr.length === 0) {
      return 0;
    }
    let sum = 0;
    for (const num of arr) {
  sum = sum + num;
}
    return sum / arr.length;
  }

  static median(arr /* Array<number> */) {
    if (arr.length === 0) {
      return 0;
    }
    const sorted = arr.slice().sort((a, b) => a - b);
    const mid = Math.floor(sorted.length / 2);
    return sorted.length % 2 !== 0 ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2;
  }

  static standardDeviation(arr /* Array<number> */) {
    if (arr.length === 0) {
      return 0;
    }
    const avg = MathUtils.average(arr);
    let sumSquareDiff = 0;
    for (const value of arr) {
  sumSquareDiff = sumSquareDiff + Math.pow(value - avg, 2);
}
    const avgSquareDiff = sumSquareDiff / arr.length;
    return Math.sqrt(avgSquareDiff);
  }

}


//# sourceMappingURL=MathUtils.js.map