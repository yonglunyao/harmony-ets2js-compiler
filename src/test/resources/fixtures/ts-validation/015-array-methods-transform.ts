// 015-array-methods-transform.ts
// Test array transform methods
function runTests() {
    console.log("=== Array Transform Methods ===");

    const numbers: number[] = [1, 2, 3, 4, 5];

    // slice
    const sliced: number[] = numbers.slice(1, 4);
    console.log("slice=" + sliced.join(","));

    // slice to end
    const sliceEnd: number[] = numbers.slice(2);
    console.log("sliceEnd=" + sliceEnd.join(","));

    // slice clone
    const cloned: number[] = numbers.slice();
    console.log("cloned=" + cloned.join(","));

    // reverse (mutates)
    const arr1: number[] = [1, 2, 3];
    arr1.reverse();
    console.log("reverse=" + arr1.join(","));

    // sort (mutates)
    const arr2: number[] = [3, 1, 4, 1, 5];
    arr2.sort();
    console.log("sort=" + arr2.join(","));

    // sort with comparator
    const arr3: number[] = [3, 1, 4, 1, 5];
    arr3.sort((a: number, b: number) => b - a);
    console.log("sortDesc=" + arr3.join(","));

    // flat
    const nested: number[][] = [[1, 2], [3, 4], [5]];
    const flattened: number[] = nested.flat();
    console.log("flat=" + flattened.join(","));

    // flat with depth
    const deep: number[][][] = [[[1, 2]], [[3, 4]]];
    const flat2: number[] = deep.flat(2);
    console.log("flatDeep=" + flat2.join(","));

    // join
    const words: string[] = ["Hello", "World"];
    const joined: string = words.join(" ");
    console.log("join=" + joined);

    // toString
    const arr4: number[] = [1, 2, 3];
    const str: string = arr4.toString();
    console.log("toString=" + str);

    // toLocaleString
    const dates: Date[] = [new Date(), new Date()];
    const localeStr: string = dates.toLocaleString();
    console.log("locale=" + localeStr);

    console.log("=== Array Transform Methods Complete ===");
}

runTests();