export class ArrayUtils {
  static isEmpty(arr /* Array<T> | null | undefined */) {
    return arr === null || arr === undefined || arr.length === 0;
  }

  static isNotEmpty(arr /* Array<T> | null | undefined */) {
    return !ArrayUtils.isEmpty(arr);
  }

  static distinct(arr /* Array<T> */) {
    return Array.from(new Set(arr));
  }

  static distinctBy(arr /* Array<T> */, key /* string */) {
    const seen = new Set();
    const result: Array<T> = [];
    for (item of arr) {
  {"kind":244,"kindName":"FirstStatement","declarationList":{"kind":262,"kindName":"VariableDeclarationList","declarations":[{"kind":261,"kindName":"VariableDeclaration","name":"value","type":"","initializer":{"kind":235,"kindName":"AsExpression","expression":{"kind":213,"kindName":"ElementAccessExpression","expression":{"kind":218,"kindName":"ParenthesizedExpression","expression":{"kind":235,"kindName":"AsExpression","expression":{"kind":80,"kindName":"Identifier","name":"item","text":"item"},"type":"Record<string, Object>"}},"argumentExpression":{"kind":80,"kindName":"Identifier","name":"key","text":"key"}},"type":"string | number | boolean"}}]}}
  {"kind":246,"kindName":"IfStatement","expression":{"kind":225,"kindName":"PrefixUnaryExpression","operator":"!","operand":{"kind":214,"kindName":"CallExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"seen","text":"seen"},"name":"has"},"arguments":["value"]}},"thenStatement":{"kind":242,"kindName":"Block","statements":[{"kind":245,"kindName":"ExpressionStatement","expression":{"kind":214,"kindName":"CallExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"seen","text":"seen"},"name":"add"},"arguments":["value"]}},{"kind":245,"kindName":"ExpressionStatement","expression":{"kind":214,"kindName":"CallExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"result","text":"result"},"name":"push"},"arguments":["item"]}}]},"elseStatement":null}
}
    return result;
  }

  static sort(arr /* Array<T> */, compareFn /* (a: T, b: T) => number */) {
    const result = arr.slice();
    If.create();
    if (compareFn) {
      If.branchId(0);
      result.sort(compareFn);
    }
    else {
      If.branchId(1);
      result.sort();
    }
    If.pop();
    return result;
  }

  static sortBy(arr /* Array<T> */, key /* string */, order /* 'asc' | 'desc' */) {
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

  static groupBy(arr /* Array<T> */, key /* string | ((item: T) => string) */) {
    const result: Record<string, Array<T>> = {}
    for (item of arr) {
  {"kind":244,"kindName":"FirstStatement","declarationList":{"kind":262,"kindName":"VariableDeclarationList","declarations":[{"kind":261,"kindName":"VariableDeclaration","name":"groupKey","type":"","initializer":{"kind":228,"kindName":"ConditionalExpression","condition":{"kind":227,"kindName":"BinaryExpression","left":{"kind":222,"kindName":"TypeOfExpression","expression":{"kind":80,"kindName":"Identifier","name":"key","text":"key"}},"operator":"===","right":{"kind":11,"kindName":"StringLiteral","text":"function"}},"whenTrue":{"kind":214,"kindName":"CallExpression","expression":{"kind":80,"kindName":"Identifier","name":"key","text":"key"},"arguments":["item"]},"whenFalse":{"kind":235,"kindName":"AsExpression","expression":{"kind":213,"kindName":"ElementAccessExpression","expression":{"kind":218,"kindName":"ParenthesizedExpression","expression":{"kind":235,"kindName":"AsExpression","expression":{"kind":80,"kindName":"Identifier","name":"item","text":"item"},"type":"Record<string, Object>"}},"argumentExpression":{"kind":80,"kindName":"Identifier","name":"key","text":"key"}},"type":"string"}}}]}}
  {"kind":246,"kindName":"IfStatement","expression":{"kind":225,"kindName":"PrefixUnaryExpression","operator":"!","operand":{"kind":213,"kindName":"ElementAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"result","text":"result"},"argumentExpression":{"kind":80,"kindName":"Identifier","name":"groupKey","text":"groupKey"}}},"thenStatement":{"kind":242,"kindName":"Block","statements":[{"kind":245,"kindName":"ExpressionStatement","expression":{"kind":227,"kindName":"BinaryExpression","left":{"kind":213,"kindName":"ElementAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"result","text":"result"},"argumentExpression":{"kind":80,"kindName":"Identifier","name":"groupKey","text":"groupKey"}},"operator":"=","right":{"kind":210,"kindName":"ArrayLiteralExpression","elements":[]}}}]},"elseStatement":null}
  result[].push("item");
}
    return result;
  }

  static chunk(arr /* Array<T> */, size /* number */) {
    const result: Array<Array<T>> = [];
    for (i = 0; i < arr.length; i += size) {
  result.push("arr.slice(i, i + size)");
}
    return result;
  }

  static shuffle(arr /* Array<T> */) {
    const result = arr.slice();
    for (i = result.length - 1; i > 0; i--) {
  {"kind":244,"kindName":"FirstStatement","declarationList":{"kind":262,"kindName":"VariableDeclarationList","declarations":[{"kind":261,"kindName":"VariableDeclaration","name":"j","type":"","initializer":{"kind":214,"kindName":"CallExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"Math","text":"Math"},"name":"floor"},"arguments":["Math.random() * (i + 1)"]}}]}}
  {"kind":244,"kindName":"FirstStatement","declarationList":{"kind":262,"kindName":"VariableDeclarationList","declarations":[{"kind":261,"kindName":"VariableDeclaration","name":"temp","type":"","initializer":{"kind":213,"kindName":"ElementAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"result","text":"result"},"argumentExpression":{"kind":80,"kindName":"Identifier","name":"i","text":"i"}}}]}}
  result[] = result[];
  result[] = temp;
}
    return result;
  }

  static first(arr /* Array<T> | null | undefined */, defaultValue /* T */) {
    If.create();
    if (ArrayUtils.isEmpty(arr)) {
      If.branchId(0);
      return defaultValue;
    }
    If.pop();
    return arr[0];
  }

  static last(arr /* Array<T> | null | undefined */, defaultValue /* T */) {
    If.create();
    if (ArrayUtils.isEmpty(arr)) {
      If.branchId(0);
      return defaultValue;
    }
    If.pop();
    return arr[arr.length - 1];
  }

  static slice(arr /* Array<T> */, start /* number */, end /* number */) {
    return arr.slice(start, end);
  }

  static sum(arr /* Array<number> */) {
    const sum = 0;
    for (num of arr) {
  sum = sum + num;
}
    return sum;
  }

  static average(arr /* Array<number> */) {
    If.create();
    if (ArrayUtils.isEmpty(arr)) {
      If.branchId(0);
      return 0;
    }
    If.pop();
    return ArrayUtils.sum(arr) / arr.length;
  }

  static max(arr /* Array<number> */) {
    return Math.max(...arr);
  }

  static min(arr /* Array<number> */) {
    return Math.min(...arr);
  }

  static intersect(arr1 /* Array<T> */, arr2 /* Array<T> */) {
    return arr1.filter(item => arr2.includes(item));
  }

  static union(arr1 /* Array<T> */, arr2 /* Array<T> */) {
    return ArrayUtils.distinct([...arr1, ...arr2]);
  }

  static difference(arr1 /* Array<T> */, arr2 /* Array<T> */) {
    return arr1.filter(item => !arr2.includes(item));
  }

  static remove(arr /* Array<T> */, item /* T */) {
    const index = arr.indexOf(item);
    If.create();
    if (index > -1) {
      If.branchId(0);
      return arr.slice(0, index).concat(arr.slice(index + 1));
    }
    If.pop();
    return arr;
  }

  static removeAt(arr /* Array<T> */, index /* number */) {
    If.create();
    if (index < 0 || index >= arr.length) {
      If.branchId(0);
      return arr;
    }
    If.pop();
    return arr.slice(0, index).concat(arr.slice(index + 1));
  }

  static flatten(arr /* Array<T> */, depth /* number */) {
    return arr.flat(depth);
  }

  static flattenDeep(arr /* Array<T> */) {
    return arr.flat(Infinity);
  }

  static filter(arr /* Array<T> */, predicate /* (item: T, index: number) => boolean */) {
    return arr.filter(predicate);
  }

  static find(arr /* Array<T> */, predicate /* (item: T, index: number) => boolean */) {
    return arr.find(predicate);
  }

  static map(arr /* Array<T> */, mapper /* (item: T, index: number) => R */) {
    return arr.map(mapper);
  }

  static reduce(arr /* Array<T> */, reducer /* (accumulator: R, item: T, index: number) => R */, initialValue /* R */) {
    return arr.reduce(reducer, initialValue);
  }

  static contains(arr /* Array<T> */, item /* T */) {
    return arr.includes(item);
  }

  static indexOfAll(arr /* Array<T> */, item /* T */) {
    const indices: Array<number> = [];
    arr.forEach((element: T, index: number): void => {
      if (element === item) {
        indices.push(index);
      }
    });
    return indices;
  }

}


//# sourceMappingURL=output-loop-test.js.map