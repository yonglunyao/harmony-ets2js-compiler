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
    const result = [];
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
    if (compareFn) {
      result.sort(compareFn);
    }
    else {
      result.sort();
    }
    return result;
  }

  static sortBy(arr /* Array<T> */, key /* string */, order /* 'asc' | 'desc' */) {
    return arr.slice().sort((a, b) => {
    const valueA = (a)[key];
    const valueB = (b)[key];
    const result = 0;
    if (valueA < valueB) {
    result = -1;
    } else {
    if (valueA > valueB) {
    result = 1;
    }
    }
    return order === "asc" ? result : 0 - result;
  });
  }

  static groupBy(arr /* Array<T> */, key /* string | ((item: T) => string) */) {
    const result = {}
    for (const item of arr) {
  const groupKey = typeof key === "function" ? key(item) : (item)[key];
  if (!result[groupKey]) {
  result[groupKey] = [];
}
  result[groupKey].push(item);
}
    return result;
  }

  static chunk(arr /* Array<T> */, size /* number */) {
    const result = [];
    for (let i = 0; i < arr.length; i += size) {
  result.push(arr.slice(i, i + size));
}
    return result;
  }

  static shuffle(arr /* Array<T> */) {
    const result = arr.slice();
    for (let i = result.length - 1; i > 0; i--) {
  const j = Math.floor(Math.random() * (i + 1));
  const temp = result[i];
  result[i] = result[j];
  result[j] = temp;
}
    return result;
  }

  static first(arr /* Array<T> | null | undefined */, defaultValue /* T */) {
    if (ArrayUtils.isEmpty(arr)) {
      return defaultValue;
    }
    return arr[0];
  }

  static last(arr /* Array<T> | null | undefined */, defaultValue /* T */) {
    if (ArrayUtils.isEmpty(arr)) {
      return defaultValue;
    }
    return arr[arr.length - 1];
  }

  static slice(arr /* Array<T> */, start /* number */, end /* number */) {
    return arr.slice(start, end);
  }

  static sum(arr /* Array<number> */) {
    let sum = 0;
    for (const num of arr) {
  sum = sum + num;
}
    return sum;
  }

  static average(arr /* Array<number> */) {
    if (ArrayUtils.isEmpty(arr)) {
      return 0;
    }
    return ArrayUtils.sum(arr) / arr.length;
  }

  static max(arr /* Array<number> */) {
    return Math.max(...arr);
  }

  static min(arr /* Array<number> */) {
    return Math.min(...arr);
  }

  static intersect(arr1 /* Array<T> */, arr2 /* Array<T> */) {
    return arr1.filter((item) => arr2.includes(item));
  }

  static union(arr1 /* Array<T> */, arr2 /* Array<T> */) {
    return ArrayUtils.distinct([...arr1, ...arr2]);
  }

  static difference(arr1 /* Array<T> */, arr2 /* Array<T> */) {
    return arr1.filter((item) => !arr2.includes(item));
  }

  static remove(arr /* Array<T> */, item /* T */) {
    const index = arr.indexOf(item);
    if (index > -1) {
      return arr.slice(0, index).concat(arr.slice(index + 1));
    }
    return arr;
  }

  static removeAt(arr /* Array<T> */, index /* number */) {
    if (index < 0 || index >= arr.length) {
      return arr;
    }
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
    const indices = [];
    arr.forEach((element, index) => {
    if (element === item) {
    indices.push(index);
    }
  });
    return indices;
  }

}


//# sourceMappingURL=ArrayUtils.js.map