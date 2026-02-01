// 016-array-methods-check.ts
// Test array check methods
function runTests() {
    console.log("=== Array Check Methods ===");

    const numbers: number[] = [1, 2, 3, 4, 5];

    // some
    const hasEven: boolean = numbers.some((n: number) => n % 2 === 0);
    console.log("someEven=" + hasEven);

    const hasNegative: boolean = numbers.some((n: number) => n < 0);
    console.log("someNegative=" + hasNegative);

    // every
    const allPositive: boolean = numbers.every((n: number) => n > 0);
    console.log("everyPositive=" + allPositive);

    const allLessThan10: boolean = numbers.every((n: number) => n < 10);
    console.log("everyLess10=" + allLessThan10);

    // includes
    const hasThree: boolean = numbers.includes(3);
    console.log("includes=" + hasThree);

    const hasSix: boolean = numbers.includes(6);
    console.log("notIncludes=" + hasSix);

    // indexOf for check
    const idx: number = numbers.indexOf(3);
    const exists: boolean = idx !== -1;
    console.log("exists=" + exists);

    // Array.isArray
    const arr: number[] = [1, 2, 3];
    const isArr: boolean = Array.isArray(arr);
    console.log("isArray=" + isArr);

    const notArr: boolean = Array.isArray("not array");
    console.log("notArray=" + notArr);

    // Check if empty
    const empty: number[] = [];
    const isEmpty: boolean = empty.length === 0;
    console.log("isEmpty=" + isEmpty);

    const notEmpty: boolean = numbers.length > 0;
    console.log("notEmpty=" + notEmpty);

    console.log("=== Array Check Methods Complete ===");
}

runTests();