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
    for (const item of arr) {
  const value = (item)[key];
  if (!seen.has(value)) {
  seen.add(value);
  result.push(item);
}
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
    for (const item of arr) {
  const groupKey = typeof key === function ? key(item) : (item)[key];
  if (!result[groupKey]) {
  result[groupKey] = [];
}
  result[groupKey].push(item);
}
    return result;
  }

  static chunk(arr /* Array<T> */, size /* number */) {
    const result: Array<Array<T>> = [];
    for (const i = 0; i < arr.length; i += size) {
  result.push(arr.slice(i, i + size));
}
    return result;
  }

  static shuffle(arr /* Array<T> */) {
    const result = arr.slice();
    for (const i = result.length - 1; i > 0; i--) {
  const j = Math.floor(Math.random() * (i + 1));
  const temp = result[i];
  result[i] = result[j];
  result[j] = temp;
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
    for (const num of arr) {
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


//# sourceMappingURL=output-loop-test3.js.map