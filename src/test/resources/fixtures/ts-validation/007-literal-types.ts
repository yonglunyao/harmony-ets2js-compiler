// 007-literal-types.ts
// Test literal types
function runTests() {
    console.log("=== Literal Types ===");

    // String literal types
    let direction: "up" | "down" | "left" | "right";
    direction = "up";
    console.log("direction=" + direction);

    direction = "down";
    console.log("direction=" + direction);

    // Number literal types
    let diceRoll: 1 | 2 | 3 | 4 | 5 | 6;
    diceRoll = 6;
    console.log("dice=" + diceRoll);

    // Boolean literal types
    let success: true;
    success = true;
    console.log("success=" + success);

    // String literal in function
    function setColor(color: "red" | "green" | "blue"): void {
        console.log("color=" + color);
    }
    setColor("red");
    setColor("green");
    setColor("blue");

    // Combined literal types
    let size: "small" | "medium" | "large" = "medium";
    console.log("size=" + size);

    // Template literal type (runtime)
    const prefix: "http://" | "https://" = "https://";
    const domain: string = "example.com";
    const url: string = prefix + domain;
    console.log("url=" + url);

    // Literal type narrowing
    function printSize(s: "xs" | "s" | "m" | "l" | "xl"): void {
        console.log("size=" + s);
    }
    printSize("m");
    printSize("xl");

    // Mixed literal types
    type ID = number | string;
    const userId1: ID = 123;
    const userId2: ID = "abc";
    console.log("id1=" + userId1);
    console.log("id2=" + userId2);

    console.log("=== Literal Types Complete ===");
}

runTests();