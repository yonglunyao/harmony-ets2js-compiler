export class StringUtils {
  static isEmpty(str /* string | null | undefined */) {
    return str === null || str === undefined || str.length === 0;
  }

  static isNotEmpty(str /* string | null | undefined */) {
    return !StringUtils.isEmpty(str);
  }

  static isBlank(str /* string | null | undefined */) {
    if (StringUtils.isEmpty(str)) {
      return true;
    }
    return str.trim().length === 0;
  }

  static isNotBlank(str /* string | null | undefined */) {
    return !StringUtils.isBlank(str);
  }

  static trim(str /* string | null | undefined */) {
    if (StringUtils.isEmpty(str)) {
      return "";
    }
    return str.trim();
  }

  static truncate(str /* string */, maxLength /* number */, suffix /* string */) {
    if (StringUtils.isEmpty(str) || str.length <= maxLength) {
      return str || "";
    }
    return str.substring(0, maxLength) + suffix;
  }

  static capitalize(str /* string */) {
    if (StringUtils.isEmpty(str)) {
      return "";
    }
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  static uncapitalize(str /* string */) {
    if (StringUtils.isEmpty(str)) {
      return "";
    }
    return str.charAt(0).toLowerCase() + str.slice(1);
  }

  static camelToSnake(str /* string */) {
    return str.replace(/([A-Z])/g, "_$1").toLowerCase();
  }

  static snakeToCamel(str /* string */) {
    return str.replace(/_([a-z])/g, (match, letter) => letter.toUpperCase());
  }

  static random(length /* number */, charset /* string */) {
    let result = "";
    for (let i = 0; i < length; i++) {
  result += charset.charAt(Math.floor(Math.random() * charset.length));
}
    return result;
  }

  static startsWith(str /* string */, prefix /* string */, ignoreCase /* boolean */) {
    if (ignoreCase) {
      return str.toLowerCase().startsWith(prefix.toLowerCase());
    }
    return str.startsWith(prefix);
  }

  static endsWith(str /* string */, suffix /* string */, ignoreCase /* boolean */) {
    if (ignoreCase) {
      return str.toLowerCase().endsWith(suffix.toLowerCase());
    }
    return str.endsWith(suffix);
  }

  static splitAndFilter(str /* string */, delimiter /* string */) {
    if (StringUtils.isEmpty(str)) {
      return [];
    }
    const parts = str.split(delimiter);
    const result = [];
    for (const s of parts) {
  if (StringUtils.isNotEmpty(s) && StringUtils.isNotBlank(s)) {
  result.push(s);
}
}
    return result;
  }

  static formatNumber(num /* number */) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  }

  static mask(str /* string */, startLen /* number */, endLen /* number */, maskChar /* string */) {
    if (StringUtils.isEmpty(str) || str.length <= startLen + endLen) {
      return str || "";
    }
    const start = str.substring(0, startLen);
    const end = str.substring(str!.length - endLen);
    let mask = "";
    for (let i = 0; i < str.length - startLen - endLen; i++) {
  mask += maskChar;
}
    return start + mask + end;
  }

}


//# sourceMappingURL=StringUtils.js.map