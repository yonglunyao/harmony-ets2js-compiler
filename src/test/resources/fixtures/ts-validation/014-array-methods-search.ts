// 014-array-methods-search.ts
// Test array search methods
function runTests() {
    console.log("=== Array Search Methods ===");

    const fruits: string[] = ["apple", "banana", "cherry", "date", "elderberry"];

    // indexOf
    const idx1: number = fruits.indexOf("cherry");
    console.log("indexOf=" + idx1);

    const idx2: number = fruits.indexOf("fig");
    console.log("notFound=" + idx2);

    // lastIndexOf
    const nums: number[] = [1, 2, 3, 2, 1];
    const lastIdx: number = nums.lastIndexOf(2);
    console.log("lastIndexOf=" + lastIdx);

    // includes
    const hasApple: boolean = fruits.includes("apple");
    console.log("includes=" + hasApple);

    const hasFig: boolean = fruits.includes("fig");
    console.log("notIncludes=" + hasFig);

    // find
    const found: string | undefined = fruits.find((f: string) => f.startsWith("b"));
    console.log("find=" + found);

    // findIndex
    const foundIdx: number = fruits.findIndex((f: string) => f.length > 6);
    console.log("findIndex=" + foundIdx);

    // findLast
    const lastLong: string | undefined = fruits.findLast((f: string) => f.length > 6);
    console.log("findLast=" + lastLong);

    // findLastIndex
    const lastLongIdx: number = fruits.findLastIndex((f: string) => f.length > 6);
    console.log("findLastIndex=" + lastLongIdx);

    // indexOf with start
    const nums2: number[] = [1, 2, 3, 1, 2, 3];
    const idxFrom: number = nums2.indexOf(1, 2);
    console.log("indexOfFrom=" + idxFrom);

    // includes with fromIndex
    const included: boolean = nums2.includes(2, 3);
    console.log("includesFrom=" + included);

    console.log("=== Array Search Methods Complete ===");
}

runTests();