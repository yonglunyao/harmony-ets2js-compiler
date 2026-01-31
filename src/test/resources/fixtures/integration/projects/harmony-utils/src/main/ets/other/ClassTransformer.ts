
/**
 * class-transformer 功能替代
 */
export class ClassTransformer {
  /**
   * ArkTS plainToClass 工具：将普通纯对象转换为指定类的实例
   * @param cls 目标类的构造函数
   * @param plainObject 普通JSON纯对象
   * @returns 目标类的实例（包含类的原型方法和属性）
   */
  static plainToClass<T>(cls: new (...args: any[]) => T, plainObject: Record<string, any>): T {
    if (typeof cls!=='function' || !plainObject || typeof plainObject!=='object') {
      throw new Error('非法入参：cls必须是类构造函数，plainObject必须是有效纯对象');
    }
    const instance = Object.create(cls.prototype);
    //获取目标类的所有自有属性（包含实例属性和原型属性的自有属性）
    const clsProperties = Object.getOwnPropertyNames({ ...new cls(), ...cls.prototype });
    //遍历赋值：映射纯对象属性到类实例
    for (const key of clsProperties) {
      if (key === 'constructor') continue; //跳过构造函数属性
      //若纯对象中存在该属性，则进行赋值
      if (plainObject.hasOwnProperty(key)) {
        const value = plainObject[key];
        //嵌套转换支持：若属性值是对象且目标类属性是类类型，递归转换
        const propertyDescriptor = Object.getOwnPropertyDescriptor(cls.prototype, key);
        if (value && typeof value === 'object' && !Array.isArray(value) && propertyDescriptor &&
          typeof propertyDescriptor.value === 'function' && propertyDescriptor.value.prototype) { //判断是否为类构造函数
          //递归转换嵌套对象
          (instance as Record<string, any>)[key] = this.plainToClass(propertyDescriptor.value, value);
        } else if (Array.isArray(value) && value.length > 0 && value[0] && typeof value[0] === 'object') { //判断为对象数组
          //数组嵌套转换支持：若属性是对象数组，批量递归转换
          (instance as Record<string, any>)[key] = value.map((item) => this.plainToClass(propertyDescriptor?.value, item));
        } else {
          //普通属性直接赋值
          (instance as Record<string, any>)[key] = value;
        }
      }
    }
    //补充纯对象中存在但类原型中未定义的额外属性（可选，根据需求关闭）
    for (const [key, value] of Object.entries(plainObject)) {
      if (!clsProperties.includes(key) && key !== 'constructor') {
        (instance as Record<string, any>)[key] = value;
      }
    }
    return instance;
  }


  /**
   * 转换数组
   */
  static plainToClassArray<T>(cls: new () => T, plainArray: any[]): T[] {
    if (!Array.isArray(plainArray)) {
      return [];
    }
    return plainArray.map(item => this.plainToClass(cls, item));
  }


  /**
   * 将类实例转换为普通对象
   */
  static classToPlain<T>(classInstance: T): any {
    if (typeof classInstance !== 'object' || classInstance === null || classInstance === undefined) {
      return classInstance;
    }
    if (Array.isArray(classInstance)) {
      return classInstance.map(item => this.classToPlain(item));
    }
    const plainObj: Record<string, any> = {};
    Object.getOwnPropertyNames(classInstance).forEach(key => {
      const value = classInstance[key];
      if (typeof value !== 'function') {
        plainObj[key] = this.classToPlain(value); // 递归处理嵌套属性
      }
    });
    return plainObj;
  }

}


//辅助装饰器：标记嵌套类属性（可选，增强类型提示）
export function NestedClassV6(cls: { new(): any }, writable: boolean = true, enumerable: boolean = false) {
  return function (target: any, propertyKey: string) {
    Object.defineProperty(target, propertyKey, {
      value: cls,
      writable: writable,
      enumerable: enumerable
    });
  };
}
