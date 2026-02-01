// 013-array-methods-iterate.ts
// Test array iteration methods
function runTests() {
    console.log("=== Array Iterate Methods ===");

    const numbers: number[] = [1, 2, 3, 4, 5];

    // forEach
    console.log("forEach:");
    numbers.forEach((n: number) => console.log("  " + n));

    // map
    const doubled: number[] = numbers.map((n: number) => n * 2);
    console.log("map=" + doubled.join(","));

    // filter
    const evens: number[] = numbers.filter((n: number) => n % 2 === 0);
    console.log("filter=" + evens.join(","));

    // find
    const found: number | undefined = numbers.find((n: number) => n > 3);
    console.log("find=" + found);

    // findIndex
    const index: number = numbers.findIndex((n: number) => n === 3);
    console.log("findIndex=" + index);

    // some
    const hasEven: boolean = numbers.some((n: number) => n % 2 === 0);
    console.log("some=" + hasEven);

    // every
    const allPositive: boolean = numbers.every((n: number) => n > 0);
    console.log("every=" + allPositive);

    // reduce - sum
    const sum: number = numbers.reduce((acc: number, n: number) => acc + n, 0);
    console.log("reduce=" + sum);

    // reduce - max
    const max: number = numbers.reduce((acc: number, n: number) => Math.max(acc, n), 0);
    console.log("max=" + max);

    // reduce - object
    const fruits: string[] = ["apple", "banana", "apple"];
    const count: { [key: string]: number } = fruits.reduce((acc: { [key: string]: number }, fruit: string) => {
        acc[fruit] = (acc[fruit] || 0) + 1;
        return acc;
    }, {});
    console.log("count=" + JSON.stringify(count));

    // flatMap
    const nested: number[][] = [[1, 2], [3, 4]];
    const flat: number[] = nested.flatMap((arr: number[]) => arr);
    console.log("flatMap=" + flat.join(","));

    console.log("=== Array Iterate Methods Complete ===");
}

runTests();