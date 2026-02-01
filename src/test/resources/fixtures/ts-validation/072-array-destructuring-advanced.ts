// 072-array-destructuring-advanced.ts
// Test advanced array destructuring
function runTests() {
    console.log("=== Advanced Array Destructuring ===");

    // Destructuring nested arrays
    const nested = [1, [2, [3, 4]]];
    const [a, [b, [c, d]]] = nested;
    console.log("nested=" + a + "," + b + "," + c + "," + d);

    // Destructuring with computed index
    const arr = [10, 20, 30, 40];
    const { [1]: second, [3]: fourth } = arr;
    console.log("computed=" + second + "," + fourth);

    // Destructuring in for-of
    const matrix = [
        [1, 2],
        [3, 4],
        [5, 6]
    ];
    console.log("matrixLoop:");
    for (const [x, y] of matrix) {
        console.log("  " + x + "," + y);
    }

    // Destructuring with Map
    const map = new Map([["a", 1], ["b", 2]]);
    for (const [key, value] of map) {
        console.log("map=" + key + "=" + value);
    }

    // Destructuring function parameters
    function process([first, second, ...rest]: number[]): string {
        return first + "," + second + " rest=" + rest.join(",");
    }
    console.log("funcDestruct=" + process([1, 2, 3, 4, 5]));

    // Destructuring with default functions
    function withDefaults([a = 1, b = 2, c = 3] = []): string {
        return a + "," + b + "," + c;
    }
    console.log("defaults=" + withDefaults([5]));
    console.log("emptyDefaults=" + withDefaults());

    // Destructuring assignment expression
    let x, y;
    ([x, y] = [1, 2]);
    console.log("expr=" + x + "," + y);

    // Destructuring with ignore
    const [, , third] = [1, 2, 3, 4];
    console.log("third=" + third);

    // Destructuring return from function
    function getArray(): [string, number, boolean] {
        return ["test", 42, true];
    }
    const [str, num, bool] = getArray();
    console.log("return=" + str + "," + num + "," + bool);

    // Destructuring in.forEach callback
    const pairs = [[1, "one"], [2, "two"]];
    pairs.forEach(([num, word]) => {
        console.log("forEach=" + num + "=" + word);
    });

    // Deep nesting
    const deep = [1, [2, [3, [4, [5]]]]];
    const [v1, [v2, [v3, [v4, [v5]]]]] = deep;
    console.log("deep=" + v1 + "," + v2 + "," + v3 + "," + v4 + "," + v5);

    console.log("=== Advanced Array Destructuring Complete ===");
}

runTests();