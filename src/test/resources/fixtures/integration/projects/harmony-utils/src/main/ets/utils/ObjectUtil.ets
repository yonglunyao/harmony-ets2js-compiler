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

import util from '@ohos.util';
import { ArrayList, HashMap, List } from '@kit.ArkTS';
import { CommonAllType, Constructor } from '../entity/constraints';
import { ObjectUtils } from '../other/ObjectUitls';
import { ClassTransformer } from '../other/ClassTransformer';

/**
 * TODO 对象工具类
 * author: 桃花镇童长老ᥫ᭡
 * since: 2024/05/01
 */
export class ObjectUtil {


  /**
   * 获取对象的Hash值。如果是第一次获取，则计算Hash值并保存到对象的Hash域（返回随机的Hash值）；如果不是第一次获取，则从Hash域中获取并返回Hash值（同一对象多次返回值保持不变）。
   * @param object
   * @returns
   */
  static getHash(object: Object): number {
    return util.getHash(object);
  }


  /**
   * 获取对象的Class名称
   * @param obj
   * @returns
   */
  static getClassName(obj: Object):string {
    return obj.constructor.name;
  }


  /**
   * 获取对象的所有方法名
   * @param obj
   * @returns
   */
  static getMethodsNames(obj: Object): string[] {
    return ObjectUtils.getMethodsNames(obj);
  }


  /**
   * 判断是否是String
   * @param source
   * @returns
   */
  static isString(source: string | Object | ArrayBuffer | undefined | null): boolean {
    return typeof source === 'string' || source instanceof String;
  }


  /**
   * 判断对象是否为空
   * @param source
   * @returns
   */
  static isNull(source: CommonAllType) {
    return source === null || source === undefined;
  }


  /**
   * 判断属性内容是否为空
   * @param property
   * @returns
   */
  static isEmpty(property: CommonAllType): boolean {
    if (ObjectUtil.isNull(property)) {
      return true
    } else if (Array.isArray(property) || property instanceof Array) {
      return property.length === 0
    } else if (property instanceof List) {
      return property.isEmpty()
    } else if (property instanceof ArrayList) {
      return property.isEmpty()
    } else if (property instanceof HashMap) {
      return property.isEmpty()
    }
    return property === '';
  }


  /**
   * 浅拷贝
   * @param obj
   * @returns
   */
  static shallowCopy<T extends Object | Array<Object>>(obj: T): T {
    return ObjectUtils.shallowCopy<T>(obj);
  }


  /**
   * 深度拷贝
   * @param obj 被拷贝对象
   * @returns
   */
  static deepCopy<T>(obj: T): T {
    return ObjectUtils.deepCopy<T>(obj);
  }


  /**
   * 合并多个对象到目标对象
   * @param target 目标对象（合并结果将修改此对象）
   * @param source  待合并的源对象（可多个）
   * @returns
   */
  static assign(target: Object, ...source: Object[]): Object {
    for (const items of source) {
      for (const key of Object.keys(items)) {
        Reflect.set(target, key, Reflect.get(items, key));
      }
    }
    return target;
  }


  /**
   * 检查对象是否具有指定的属性
   * @param target 目标对象
   * @param key
   * @returns
   */
  static has(target: Object, key: PropertyKey): boolean {
    return Reflect.has(target, key);
  }

  /**
   * 通过key获取对象值
   * @param target 目标对象
   * @param key
   * @returns
   */
  static getValue<T>(target: Object, key: PropertyKey): T {
    return Reflect.get(target,key);
  }

  /**
   * 给对象obj动态添加或者修改属性
   * @param target 目标对象
   * @param key
   * @param value
   */
  static setValue<P extends PropertyKey>(target: Object, key: P, value: Object): boolean {
    return Reflect.set(target, key, value);
  }

  /**
   * 删除指定的对象的属性
   * @param target 目标对象
   * @param key
   */
  static delete(target: Object, key: PropertyKey): boolean {
    return ObjectUtils.delete(target, key);
  }


  /**
   * 将普通纯对象转换为指定类的实例（嵌套类需要添加装饰器NestedClassV6）
   * @param clazz 目标类的构造函数
   * @param obj 普通JSON纯对象
   * @returns
   */
  static plainToClass<T>(clazz: Constructor<T>, obj: Object): T {
    return ClassTransformer.plainToClass(clazz, obj);
  }


  /**
   * 将普通纯对象数组转换为指定类的实例数组（嵌套类需要添加装饰器NestedClassV6）
   * @param clazz 目标类的构造函数
   * @param obj 普通JSON纯对象数组
   * @returns
   */
  static plainToClassArray<T>(clazz: Constructor<T>, plainArray: Object[]): T[] {
    return ClassTransformer.plainToClassArray(clazz, plainArray);
  }


  /**
   * 将类实例转换为普通对象
   * @param classInstance 类实例（或任意数据）
   * @returns 普通纯对象
   */
  static classToPlain<T>(classInstance: T): Record<string, Object> {
    return ClassTransformer.classToPlain(classInstance);
  }



  /**
   * 该方法已废弃，建议使用 plainToClass。
   */
  static objToClass<T extends Object>(clazz: Constructor<T>, obj: Object): T {
    return ObjectUtil.plainToClass(clazz,obj);;
  }

  /**
   * 该方法已废弃，建议使用 delete。
   */
  static deleteRecord(target: Object, key: PropertyKey) {
    ObjectUtil.delete(target, key);
  }
}