// 019-array-readonly.ts
// Test readonly arrays
function runTests() {
    console.log("=== Readonly Arrays ===");

    // Readonly array type
    const readonlyArr: ReadonlyArray<number> = [1, 2, 3];
    console.log("readonly=" + readonlyArr.join(","));

    // Readonly modifier
    const readonlyNums: readonly number[] = [10, 20, 30];
    console.log("readonlyNums=" + readonlyNums.join(","));

    // Can read
    console.log("read=" + readonlyArr[0]);
    console.log("length=" + readonlyArr.length);

    // Can use read-only methods
    const mapped: number[] = readonlyArr.map((n: number) => n * 2);
    console.log("mapped=" + mapped.join(","));

    const filtered: number[] = readonlyArr.filter((n: number) => n > 1);
    console.log("filtered=" + filtered.join(","));

    // Readonly and as const
    const constArray = [1, 2, 3] as const;
    console.log("const=" + constArray.join(","));

    // Readonly tuple
    const readonlyTuple: readonly [string, number] = ["fixed", 100];
    console.log("tuple=" + readonlyTuple[0] + "," + readonlyTuple[1]);

    // Function accepting readonly
    function processArray(arr: ReadonlyArray<string>): void {
        console.log("process=" + arr.join(","));
    }
    processArray(["a", "b", "c"]);

    // Readonly from normal array
    const normal: number[] = [1, 2, 3];
    const readonly: ReadonlyArray<number> = normal;
    console.log("fromNormal=" + readonly.join(","));

    console.log("=== Readonly Arrays Complete ===");
}

runTests();