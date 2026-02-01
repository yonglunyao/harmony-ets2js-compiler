// 018-array-spread-rest.ts
// Test array spread and rest
function runTests() {
    console.log("=== Array Spread and Rest ===");

    // Spread in array literal
    const arr1: number[] = [1, 2];
    const arr2: number[] = [3, 4];
    const combined: number[] = [...arr1, ...arr2];
    console.log("spread=" + combined.join(","));

    // Spread with values
    const withValues: number[] = [0, ...arr1, 5, ...arr2];
    console.log("withValues=" + withValues.join(","));

    // Spread for cloning
    const original: number[] = [1, 2, 3];
    const clone: number[] = [...original];
    console.log("clone=" + clone.join(","));

    // Spread in function call
    const nums: number[] = [10, 20, 30];
    function sum(a: number, b: number, c: number): number {
        return a + b + c;
    }
    console.log("spreadCall=" + sum(...nums));

    // Rest parameter
    function sumAll(...numbers: number[]): number {
        return numbers.reduce((acc: number, n: number) => acc + n, 0);
    }
    console.log("rest=" + sumAll(1, 2, 3, 4, 5));

    // Rest with other params
    function greet(greeting: string, ...names: string[]): void {
        names.forEach((name: string) => console.log(greeting + " " + name));
    }
    greet("Hello", "Alice", "Bob", "Charlie");

    // Destructuring with rest
    const colors: string[] = ["red", "green", "blue", "yellow"];
    const [first, second, ...rest] = colors;
    console.log("first=" + first);
    console.log("second=" + second);
    console.log("rest=" + rest.join(","));

    // Spread in object from array
    const arr: number[] = [1, 2, 3];
    const obj = { ...arr };
    console.log("obj=" + JSON.stringify(obj));

    // Array from arguments-like
    function toArray(...args: unknown[]): unknown[] {
        return args;
    }
    console.log("toArray=" + JSON.stringify(toArray(1, "a", true)));

    console.log("=== Array Spread/Rest Complete ===");
}

runTests();