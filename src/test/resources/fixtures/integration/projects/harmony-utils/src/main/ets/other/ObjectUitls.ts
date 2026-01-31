/*
 * Copyright (C) 2024 桃花镇童长老 @pura/harmony-utils
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


export class ObjectUtils {

  /**
   * 获取对象的所有方法名
   * @param obj
   * @returns
   */
  static getMethodsNames(obj: Object) {
    const protoType = Object.getPrototypeOf(obj);
    const methodsNames: string[] = Object.getOwnPropertyNames(protoType);
    return methodsNames;
  }


  /**
   * 手动实现浅拷贝函数（支持普通对象和数组，可扩展）
   * @param target 待拷贝的目标对象/数组
   * @returns 拷贝后的新对象/数组
   */
  static shallowCopy<T extends object | object[]>(target: T): T {
    if (target === null || typeof target !== "object") {
      return target;
    }
    let cloneResult: any;
    //处理数组
    if (Array.isArray(target)) {
      cloneResult = [];
      for (let i = 0; i < target.length; i++) {
        cloneResult[i] = target[i]; //遍历数组元素，拷贝表层引用/值
      }
      return cloneResult as T;
    }
    //处理普通对象（支持保留原型链）
    cloneResult = Object.create(Object.getPrototypeOf(target)); //继承原对象原型链
    //拷贝字符串键的自身可枚举属性
    Object.keys(target).forEach((key) => {
      cloneResult[key] = (target as Record<string, any>)[key];
    });
    //拷贝Symbol类型的属性（可选，默认浅拷贝方案可能忽略）
    Object.getOwnPropertySymbols(target as object).forEach((symbolKey) => {
      cloneResult[symbolKey] = (target as Record<symbol, any>)[symbolKey];
    });
    return cloneResult as T;
  }



  /**
   * 完整深度拷贝 - 递归实现（支持特殊类型、循环引用）
   * @param target 待拷贝的目标值
   * @param cache 缓存容器，用于解决循环引用（默认自动创建，外部无需传入）
   * @returns 拷贝后的新值
   */
  static deepCopy<T>(target: T, cache: WeakMap<object, object> = new WeakMap()): T {
    if (target === null || typeof target !== "object") {
      return target;
    }
    //处理循环引用：如果缓存中已有该对象，直接返回缓存的拷贝结果
    if (cache.has(target as object)) {
      return cache.get(target as object) as T;
    }
    let cloneResult: any;
    //处理Date对象
    if (target instanceof Date) {
      cloneResult = new Date((target as Date).getTime());
      cache.set(target as object, cloneResult);
      return cloneResult as T;
    }
    //处理RegExp对象
    if (target instanceof RegExp) {
      cloneResult = new RegExp((target as RegExp).source, (target as RegExp).flags);
      cache.set(target as object, cloneResult);
      return cloneResult as T;
    }
    //处理数组
    if (target instanceof Array) {
      cloneResult = [];
      cache.set(target as object, cloneResult); //先缓存，避免递归中再次处理循环引用
      (target as Array<any>).forEach((item, index) => {
        cloneResult[index] = this.deepCopy(item, cache); //递归拷贝数组元素
      });
      return cloneResult as T;
    }
    //处理普通对象（包括自定义对象，保留原型链）
    //保留原对象的原型链，确保拷贝后的对象继承原有方法
    cloneResult = Object.create(Object.getPrototypeOf(target));
    cache.set(target as object, cloneResult); // 先缓存，避免递归循环
    //遍历对象自身可枚举属性（不包含原型链属性）
    Object.keys(target as object).forEach((key) => {
      const propertyValue = (target as Record<string, any>)[key];
      cloneResult[key] = this.deepCopy(propertyValue, cache); //递归拷贝属性值
    });
    //处理Symbol类型的属性（JSON方案会忽略Symbol属性，此处补充支持）
    Object.getOwnPropertySymbols(target as object).forEach((symbolKey) => {
      const propertyValue = (target as Record<symbol, any>)[symbolKey];
      cloneResult[symbolKey] = this.deepCopy(propertyValue, cache);
    });
    return cloneResult as T;
  }


  /**
   * 删除Record中的元素
   * @param record
   * @param key
   */
  static delete(record: object, key: PropertyKey):boolean {
    // delete record[key];
   return Reflect.deleteProperty(record, key);
  }

}