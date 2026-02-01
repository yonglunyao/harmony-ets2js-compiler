// 011-array-basics.ts
// Test array basics
function runTests() {
    console.log("=== Array Basics ===");

    // Array declaration
    const numbers: number[] = [1, 2, 3, 4, 5];
    console.log("numbers=" + numbers.join(","));

    // Generic array syntax
    const strings: Array<string> = ["a", "b", "c"];
    console.log("strings=" + strings.join(","));

    // Mixed type array
    const mixed: (string | number)[] = [1, "two", 3, "four"];
    console.log("mixed=" + JSON.stringify(mixed));

    // Empty array
    const empty: number[] = [];
    console.log("empty=" + empty.length);

    // Array length
    const arr: number[] = [1, 2, 3];
    console.log("length=" + arr.length);

    // Access by index
    const fruits: string[] = ["apple", "banana", "cherry"];
    console.log("first=" + fruits[0]);
    console.log("second=" + fruits[1]);
    console.log("last=" + fruits[fruits.length - 1]);

    // Modify array
    const nums: number[] = [10, 20, 30];
    nums[0] = 100;
    console.log("modified=" + nums.join(","));

    // Array with initial size
    const sized = new Array<string>(3);
    console.log("sizedLen=" + sized.length);

    // Array of arrays
    const matrix: number[][] = [[1, 2], [3, 4], [5, 6]];
    console.log("matrix=" + JSON.stringify(matrix));
    console.log("matrix[1][1]=" + matrix[1][1]);

    // Array destructuring
    const colors: string[] = ["red", "green", "blue"];
    const [first, second, third] = colors;
    console.log("first=" + first);
    console.log("second=" + second);
    console.log("third=" + third);

    console.log("=== Array Basics Complete ===");
}

runTests();