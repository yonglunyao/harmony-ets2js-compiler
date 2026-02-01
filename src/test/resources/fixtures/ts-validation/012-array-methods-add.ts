// 012-array-methods-add.ts
// Test array addition methods
function runTests() {
    console.log("=== Array Add Methods ===");

    // push - add to end
    const fruits1: string[] = ["apple"];
    fruits1.push("banana");
    fruits1.push("cherry", "date");
    console.log("push=" + fruits1.join(","));

    // pop - remove from end
    const fruits2: string[] = ["a", "b", "c"];
    const popped = fruits2.pop();
    console.log("pop=" + popped);
    console.log("after=" + fruits2.join(","));

    // unshift - add to beginning
    const fruits3: string[] = ["banana", "cherry"];
    fruits3.unshift("apple");
    console.log("unshift=" + fruits3.join(","));

    // shift - remove from beginning
    const fruits4: string[] = ["a", "b", "c"];
    const shifted = fruits4.shift();
    console.log("shift=" + shifted);
    console.log("after=" + fruits4.join(","));

    // splice - insert
    const arr1: number[] = [1, 2, 4, 5];
    arr1.splice(2, 0, 3);
    console.log("spliceInsert=" + arr1.join(","));

    // splice - remove
    const arr2: number[] = [1, 2, 3, 4, 5];
    arr2.splice(2, 1);
    console.log("spliceRemove=" + arr2.join(","));

    // splice - replace
    const arr3: number[] = [1, 2, 99, 4, 5];
    arr3.splice(2, 1, 3);
    console.log("spliceReplace=" + arr3.join(","));

    // concat - merge arrays
    const arrA: number[] = [1, 2];
    const arrB: number[] = [3, 4];
    const merged: number[] = arrA.concat(arrB);
    console.log("concat=" + merged.join(","));

    // spread operator merge
    const arrC: number[] = [5, 6];
    const spreadMerge: number[] = [...arrA, ...arrC];
    console.log("spread=" + spreadMerge.join(","));

    console.log("=== Array Add Methods Complete ===");
}

runTests();