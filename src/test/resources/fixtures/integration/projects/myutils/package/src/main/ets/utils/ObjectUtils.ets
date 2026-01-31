/**
 * 对象工具类
 */
export class ObjectUtils {
  /**
   * 判断对象是否为空
   * @param obj 对象
   * @returns true表示为空
   */
  static isEmpty(obj: object | null | undefined): boolean {
    if (obj === null || obj === undefined) {
      return true;
    }
    return Object.keys(obj).length === 0;
  }

  /**
   * 判断对象是否不为空
   * @param obj 对象
   * @returns true表示不为空
   */
  static isNotEmpty(obj: object | null | undefined): boolean {
    return !ObjectUtils.isEmpty(obj);
  }

  /**
   * 判断是否为对象
   * @param obj 待判断的值
   * @returns true表示是对象
   */
  static isObject(obj: Object | null | undefined): boolean {
    return obj !== null && obj !== undefined && typeof obj === 'object' && !Array.isArray(obj);
  }

  /**
   * 浅克隆对象（仅支持简单对象）
   * @param obj 对象
   * @returns 克隆后的对象
   */
  static shallowClone<T extends Record<string, Object>>(obj: T): T {
    if (obj === null || typeof obj !== 'object') {
      return obj;
    }
    const result: Record<string, Object> = {};
    const keys = Object.keys(obj);
    for (let i = 0; i < keys.length; i++) {
      const key = keys[i];
      result[key] = obj[key];
    }
    return result as T;
  }

  /**
   * 获取对象的键
   * @param obj 对象
   * @returns 键数组
   */
  static keys<T extends object>(obj: T): Array<string> {
    return Object.keys(obj);
  }

  /**
   * 获取对象的值
   * @param obj 对象
   * @returns 值数组
   */
  static values<T extends object>(obj: T): Array<Object> {
    return Object.values(obj);
  }

  /**
   * 获取对象属性值
   * @param obj 对象
   * @param path 属性路径，如'a.b.c'
   * @param defaultValue 默认值
   * @returns 属性值或默认值
   */
  static get(obj: Record<string, Object>, path: string, defaultValue?: Object): Object | undefined {
    const keys = path.split('.');
    let result: Object | undefined = obj;
    for (let i = 0; i < keys.length; i++) {
      const key = keys[i];
      if (result === null || result === undefined) {
        return defaultValue;
      }
      result = (result as Record<string, Object>)[key];
    }
    return result !== undefined ? result : defaultValue;
  }

  /**
   * 判断两个对象是否相等（简单比较）
   * @param obj1 对象1
   * @param obj2 对象2
   * @returns true表示相等
   */
  static isEqual(obj1: Object, obj2: Object): boolean {
    if (obj1 === obj2) {
      return true;
    }
    return JSON.stringify(obj1) === JSON.stringify(obj2);
  }
}
