// 069-spread-operator.ts
// Test spread operator
function runTests() {
    console.log("=== Spread Operator ===");

    // Spread in array literals
    const arr1 = [1, 2, 3];
    const arr2 = [4, 5, 6];
    const combined = [...arr1, ...arr2];
    console.log("combined=" + combined.join(","));

    // Spread with values
    const withValues = [0, ...arr1, 3.5, ...arr2];
    console.log("withValues=" + withValues.join(","));

    // Spread for cloning
    const original = [1, 2, 3];
    const clone = [...original];
    console.log("clone=" + clone.join(","));

    // Spread in object literals
    const obj1 = { a: 1, b: 2 };
    const obj2 = { c: 3, d: 4 };
    const merged = { ...obj1, ...obj2 };
    console.log("merged=" + JSON.stringify(merged));

    // Spread with override
    const base = { x: 1, y: 2 };
    const override = { ...base, y: 20, z: 3 };
    console.log("override=" + JSON.stringify(override));

    // Spread for function arguments
    const numbers = [1, 2, 3];
    function sum(a: number, b: number, c: number): number {
        return a + b + c;
    }
    console.log("sumArgs=" + sum(...numbers));

    // Spread with strings
    const str = "hello";
    const chars = [...str];
    console.log("chars=" + chars.join(","));

    // Spread with Set
    const set = new Set([1, 2, 3, 2, 1]);
    const unique = [...set];
    console.log("unique=" + unique.join(","));

    // Spread with Map
    const map = new Map([["a", 1], ["b", 2]]);
    const mapArr = [...map];
    console.log("map=" + JSON.stringify(mapArr));

    // Spread in array methods
    const nums = [3, 1, 4, 1, 5];
    const max = Math.max(...nums);
    const min = Math.min(...nums);
    console.log("max=" + max + " min=" + min);

    // Nested spread
    const nested = [[1, 2], [3, 4]];
    const flat = [...nested[0], ...nested[1]];
    console.log("flat=" + flat.join(","));

    // Spread with conditions
    const includeExtra = true;
    const conditional = [1, 2, ...(includeExtra ? [3, 4] : [])];
    console.log("conditional=" + conditional.join(","));

    // Spread in object with computed properties
    const key = "dynamic";
    const computed = { ...{ [key]: "value" } };
    console.log("computed=" + JSON.stringify(computed));

    console.log("=== Spread Operator Complete ===");
}

runTests();