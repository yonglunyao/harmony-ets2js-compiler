// 057-for-of-loop.ts
// Test for-of loops
function runTests() {
    console.log("=== For-Of Loops ===");

    // For-of with array
    const arr = [10, 20, 30, 40, 50];
    console.log("array:");
    for (const value of arr) {
        console.log("  val=" + value);
    }

    // For-of with string
    const str = "Hello";
    console.log("string:");
    for (const char of str) {
        console.log("  char=" + char);
    }

    // For-of with Set
    const set = new Set([1, 2, 3, 2, 1]);
    console.log("set:");
    for (const value of set) {
        console.log("  val=" + value);
    }

    // For-of with Map
    const map = new Map([["a", 1], ["b", 2], ["c", 3]]);
    console.log("map:");
    for (const [key, value] of map) {
        console.log("  " + key + "=" + value);
    }

    // For-of with entries
    const entries = Object.entries({ x: 10, y: 20 });
    console.log("entries:");
    for (const [key, value] of entries) {
        console.log("  " + key + "=" + value);
    }

    // For-of with keys
    const keys = Object.keys({ a: 1, b: 2, c: 3 });
    console.log("keys:");
    for (const key of keys) {
        console.log("  key=" + key);
    }

    // For-of with values
    const values = Object.values({ first: 1, second: 2 });
    console.log("values:");
    for (const value of values) {
        console.log("  val=" + value);
    }

    // For-of with break
    console.log("break:");
    for (const value of [1, 2, 3, 4, 5]) {
        if (value === 3) {
            break;
        }
        console.log("  val=" + value);
    }

    // For-of with continue
    console.log("continue:");
    for (const value of [1, 2, 3, 4, 5]) {
        if (value % 2 === 0) {
            continue;
        }
        console.log("  odd=" + value);
    }

    // For-of with index tracking
    const items = ["a", "b", "c"];
    console.log("withIndex:");
    let idx = 0;
    for (const item of items) {
        console.log("  [" + idx + "]=" + item);
        idx++;
    }

    // For-of with destructuring
    const pairs = [[1, "one"], [2, "two"], [3, "three"]];
    console.log("destruct:");
    for (const [num, word] of pairs) {
        console.log("  " + num + "=" + word);
    }

    console.log("=== For-Of Loops Complete ===");
}

runTests();