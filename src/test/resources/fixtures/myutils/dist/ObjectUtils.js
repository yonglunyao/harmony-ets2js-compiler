export class ObjectUtils {
  static isEmpty(obj /* object | null | undefined */) {
    if (obj === null || obj === undefined) {
      return true;
    }
    return Object.keys(obj).length === 0;
  }

  static isNotEmpty(obj /* object | null | undefined */) {
    return !ObjectUtils.isEmpty(obj);
  }

  static isObject(obj /* Object | null | undefined */) {
    return obj !== null && obj !== undefined && typeof obj === "object" && !Array.isArray(obj);
  }

  static shallowClone(obj /* T */) {
    if (obj === null || typeof obj !== "object") {
      return obj;
    }
    const result = {}
    const keys = Object.keys(obj);
    for (let i = 0; i < keys.length; i++) {
  const key = keys[i];
  result[key] = obj[key];
}
    return result;
  }

  static keys(obj /* T */) {
    return Object.keys(obj);
  }

  static values(obj /* T */) {
    return Object.values(obj);
  }

  static get(obj /* Record<string, Object> */, path /* string */, defaultValue /* Object */) {
    const keys = path.split(".");
    let result = obj;
    for (let i = 0; i < keys.length; i++) {
  const key = keys[i];
  if (result === null || result === undefined) {
  return defaultValue;
}
  result = (result)[key];
}
    return result !== undefined ? result : defaultValue;
  }

  static isEqual(obj1 /* Object */, obj2 /* Object */) {
    if (obj1 === obj2) {
      return true;
    }
    return JSON.stringify(obj1) === JSON.stringify(obj2);
  }

}


//# sourceMappingURL=ObjectUtils.js.map