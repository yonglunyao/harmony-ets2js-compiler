// 080-iterators-iterables.ts
// Test iterators and iterables
function runTests() {
    console.log("=== Iterators and Iterables ===");

    // Array iterator
    const arr = [1, 2, 3];
    const arrIter = arr[Symbol.iterator]();
    console.log("arrNext1=" + arrIter.next().value);
    console.log("arrNext2=" + arrIter.next().value);
    console.log("arrNext3=" + arrIter.next().value);
    console.log("arrDone=" + arrIter.next().done);

    // String iterator
    const str = "abc";
    const strIter = str[Symbol.iterator]();
    console.log("str=" + [...strIter].join(","));

    // Map iterator
    const map = new Map([["a", 1], ["b", 2]]);
    const mapIter = map[Symbol.iterator]();
    console.log("map=" + [...mapIter].map(e => e[0] + "=" + e[1]).join(","));

    // Set iterator
    const set = new Set([1, 2, 3]);
    const setIter = set[Symbol.iterator]();
    console.log("set=" + [...setIter].join(","));

    // Custom iterable
    class Counter {
        constructor(max) {
            this.max = max;
        }
        [Symbol.iterator]() {
            let count = 0;
            const max = this.max;
            return {
                next() {
                    if (count < max) {
                        return { value: count++, done: false };
                    }
                    return { value: 0, done: true };
                }
            };
        }
    }

    const counter = new Counter(5);
    console.log("counter=" + [...counter].join(","));

    // Custom iterator with return
    class Range {
        constructor(start, end) {
            this.start = start;
            this.end = end;
        }
        [Symbol.iterator]() {
            let current = this.start;
            const end = this.end;
            return {
                next() {
                    if (current <= end) {
                        return { value: current++, done: false };
                    }
                    return { value: 0, done: true };
                },
                return(value) {
                    console.log("iteratorReturn");
                    return { value, done: true };
                }
            };
        }
    }

    const range = new Range(1, 5);
    for (const n of range) {
        console.log("range=" + n);
    }

    // Iterator protocol
    function createIterator(items) {
        let index = 0;
        return {
            next() {
                if (index < items.length) {
                    return { value: items[index++], done: false };
                }
                return { value: 0, done: true };
            }
        };
    }

    const iter = createIterator([10, 20, 30]);
    console.log("custom=" + iter.next().value);
    console.log("custom=" + iter.next().value);

    // for-of with custom iterable
    class Numbers {
        constructor(data) {
            this.data = data;
        }
        [Symbol.iterator]() {
            return this.data[Symbol.iterator]();
        }
    }

    const numbers = new Numbers([1, 2, 3]);
    for (const n of numbers) {
        console.log("numbers=" + n);
    }

    console.log("=== Iterators and Iterables Complete ===");
}

runTests();
