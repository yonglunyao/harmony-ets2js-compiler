/**
 * 数组工具类
 */
export class ArrayUtils {
  /**
   * 判断数组是否为空
   * @param arr 数组
   * @returns true表示为空
   */
  static isEmpty<T>(arr: Array<T> | null | undefined): boolean {
    return arr === null || arr === undefined || arr.length === 0;
  }

  /**
   * 判断数组是否不为空
   * @param arr 数组
   * @returns true表示不为空
   */
  static isNotEmpty<T>(arr: Array<T> | null | undefined): boolean {
    return !ArrayUtils.isEmpty(arr);
  }

  /**
   * 数组去重
   * @param arr 数组
   * @returns 去重后的数组
   */
  static distinct<T>(arr: Array<T>): Array<T> {
    return Array.from(new Set(arr));
  }

  /**
   * 根据指定属性去重对象数组
   * @param arr 对象数组
   * @param key 属性名
   * @returns 去重后的数组
   */
  static distinctBy<T>(arr: Array<T>, key: string): Array<T> {
    const seen = new Set<string | number | boolean>();
    const result: Array<T> = [];

    for (const item of arr) {
      const value = (item as Record<string, Object>)[key] as string | number | boolean;
      if (!seen.has(value)) {
        seen.add(value);
        result.push(item);
      }
    }

    return result;
  }

  /**
   * 数组排序
   * @param arr 数组
   * @param compareFn 比较函数
   * @returns 排序后的数组
   */
  static sort<T>(arr: Array<T>, compareFn?: (a: T, b: T) => number): Array<T> {
    const result = arr.slice();
    if (compareFn) {
      result.sort(compareFn);
    } else {
      result.sort();
    }
    return result;
  }

  /**
   * 根据指定属性排序对象数组
   * @param arr 对象数组
   * @param key 属性名
   * @param order 排序方式，'asc'升序，'desc'降序
   * @returns 排序后的数组
   */
  static sortBy<T>(arr: Array<T>, key: string, order: 'asc' | 'desc' = 'asc'): Array<T> {
    return arr.slice().sort((a: T, b: T): number => {
      const valueA = (a as Record<string, Object>)[key] as number;
      const valueB = (b as Record<string, Object>)[key] as number;
      let result = 0;
      if (valueA < valueB) {
        result = -1;
      } else if (valueA > valueB) {
        result = 1;
      }
      return order === 'asc' ? result : 0 - result;
    });
  }

  /**
   * 数组分组
   * @param arr 数组
   * @param key 分组属性或分组函数
   * @returns 分组后的对象
   */
  static groupBy<T>(arr: Array<T>, key: string | ((item: T) => string)): Record<string, Array<T>> {
    const result: Record<string, Array<T>> = {};

    for (const item of arr) {
      const groupKey = typeof key === 'function' ? key(item) : (item as Record<string, Object>)[key] as string;
      if (!result[groupKey]) {
        result[groupKey] = [];
      }
      result[groupKey].push(item);
    }

    return result;
  }

  /**
   * 数组分块
   * @param arr 数组
   * @param size 每块大小
   * @returns 分块后的二维数组
   */
  static chunk<T>(arr: Array<T>, size: number): Array<Array<T>> {
    const result: Array<Array<T>> = [];
    for (let i = 0; i < arr.length; i += size) {
      result.push(arr.slice(i, i + size));
    }
    return result;
  }

  /**
   * 数组打乱
   * @param arr 数组
   * @returns 打乱后的数组
   */
  static shuffle<T>(arr: Array<T>): Array<T> {
    const result = arr.slice();
    for (let i = result.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      const temp = result[i];
      result[i] = result[j];
      result[j] = temp;
    }
    return result;
  }

  /**
   * 获取数组中的第一个元素
   * @param arr 数组
   * @param defaultValue 默认值
   * @returns 第一个元素或默认值
   */
  static first<T>(arr: Array<T> | null | undefined, defaultValue?: T): T | undefined {
    if (ArrayUtils.isEmpty(arr)) {
      return defaultValue;
    }
    return arr![0];
  }

  /**
   * 获取数组中的最后一个元素
   * @param arr 数组
   * @param defaultValue 默认值
   * @returns 最后一个元素或默认值
   */
  static last<T>(arr: Array<T> | null | undefined, defaultValue?: T): T | undefined {
    if (ArrayUtils.isEmpty(arr)) {
      return defaultValue;
    }
    return arr![arr!.length - 1];
  }

  /**
   * 获取数组中指定范围的元素
   * @param arr 数组
   * @param start 开始索引
   * @param end 结束索引
   * @returns 指定范围的元素
   */
  static slice<T>(arr: Array<T>, start?: number, end?: number): Array<T> {
    return arr.slice(start, end);
  }

  /**
   * 数组求和
   * @param arr 数字数组
   * @returns 总和
   */
  static sum(arr: Array<number>): number {
    let sum = 0;
    for (const num of arr) {
      sum = sum + num;
    }
    return sum;
  }

  /**
   * 数组求平均值
   * @param arr 数字数组
   * @returns 平均值
   */
  static average(arr: Array<number>): number {
    if (ArrayUtils.isEmpty(arr)) {
      return 0;
    }
    return ArrayUtils.sum(arr) / arr.length;
  }

  /**
   * 获取数组中的最大值
   * @param arr 数字数组
   * @returns 最大值
   */
  static max(arr: Array<number>): number {
    return Math.max(...arr);
  }

  /**
   * 获取数组中的最小值
   * @param arr 数字数组
   * @returns 最小值
   */
  static min(arr: Array<number>): number {
    return Math.min(...arr);
  }

  /**
   * 数组求交集
   * @param arr1 数组1
   * @param arr2 数组2
   * @returns 交集
   */
  static intersect<T>(arr1: Array<T>, arr2: Array<T>): Array<T> {
    return arr1.filter(item => arr2.includes(item));
  }

  /**
   * 数组求并集
   * @param arr1 数组1
   * @param arr2 数组2
   * @returns 并集
   */
  static union<T>(arr1: Array<T>, arr2: Array<T>): Array<T> {
    return ArrayUtils.distinct([...arr1, ...arr2]);
  }

  /**
   * 数组求差集
   * @param arr1 数组1
   * @param arr2 数组2
   * @returns 差集（arr1中有但arr2中没有的元素）
   */
  static difference<T>(arr1: Array<T>, arr2: Array<T>): Array<T> {
    return arr1.filter(item => !arr2.includes(item));
  }

  /**
   * 从数组中删除指定元素
   * @param arr 数组
   * @param item 要删除的元素
   * @returns 删除后的数组
   */
  static remove<T>(arr: Array<T>, item: T): Array<T> {
    const index = arr.indexOf(item);
    if (index > -1) {
      return arr.slice(0, index).concat(arr.slice(index + 1));
    }
    return arr;
  }

  /**
   * 从数组中删除指定索引的元素
   * @param arr 数组
   * @param index 索引
   * @returns 删除后的数组
   */
  static removeAt<T>(arr: Array<T>, index: number): Array<T> {
    if (index < 0 || index >= arr.length) {
      return arr;
    }
    return arr.slice(0, index).concat(arr.slice(index + 1));
  }

  /**
   * 数组扁平化
   * @param arr 多维数组
   * @param depth 扁平化深度，默认为1
   * @returns 扁平化后的数组
   */
  static flatten<T>(arr: Array<T>, depth: number = 1): Array<T> {
    return arr.flat(depth) as Array<T>;
  }

  /**
   * 数组深度扁平化
   * @param arr 多维数组
   * @returns 扁平化后的数组
   */
  static flattenDeep<T>(arr: Array<T>): Array<T> {
    return arr.flat(Infinity) as Array<T>;
  }

  /**
   * 根据条件过滤数组
   * @param arr 数组
   * @param predicate 过滤条件函数
   * @returns 过滤后的数组
   */
  static filter<T>(arr: Array<T>, predicate: (item: T, index: number) => boolean): Array<T> {
    return arr.filter(predicate);
  }

  /**
   * 根据条件查找元素
   * @param arr 数组
   * @param predicate 查找条件函数
   * @returns 找到的元素或undefined
   */
  static find<T>(arr: Array<T>, predicate: (item: T, index: number) => boolean): T | undefined {
    return arr.find(predicate);
  }

  /**
   * 数组映射
   * @param arr 数组
   * @param mapper 映射函数
   * @returns 映射后的数组
   */
  static map<T, R>(arr: Array<T>, mapper: (item: T, index: number) => R): Array<R> {
    return arr.map(mapper);
  }

  /**
   * 数组归约
   * @param arr 数组
   * @param reducer 归约函数
   * @param initialValue 初始值
   * @returns 归约结果
   */
  static reduce<T, R>(arr: Array<T>, reducer: (accumulator: R, item: T, index: number) => R, initialValue: R): R {
    return arr.reduce(reducer, initialValue);
  }

  /**
   * 判断数组是否包含指定元素
   * @param arr 数组
   * @param item 元素
   * @returns true表示包含
   */
  static contains<T>(arr: Array<T>, item: T): boolean {
    return arr.includes(item);
  }

  /**
   * 获取数组中指定元素的所有索引
   * @param arr 数组
   * @param item 元素
   * @returns 索引数组
   */
  static indexOfAll<T>(arr: Array<T>, item: T): Array<number> {
    const indices: Array<number> = [];
    arr.forEach((element: T, index: number): void => {
      if (element === item) {
        indices.push(index);
      }
    });
    return indices;
  }
}
