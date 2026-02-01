// 010-type-assertions.ts
// Test runtime behavior (assertions removed)
function runTests() {
    console.log("=== Type Assertions ===");

    // String operations (no assertions needed at runtime)
    const value1 = "Hello World";
    const length1 = value1.length;
    console.log("length=" + length1);

    // String operations
    const value2 = "TypeScript";
    const length2 = value2.length;
    console.log("length=" + length2);

    // Element access
    const canvas = document.getElementById("canvas");
    console.log("canvas=" + (canvas ? "found" : "not found"));

    // Function call
    const processData = (value) => {
        const str = String(value);
        console.log("processed=" + str);
    };
    processData("data");

    // String operations
    function getLength(input) {
        const str = String(input);
        return str.length;
    }
    console.log("len=" + getLength("hello"));

    // Object literal
    const config = {
        mode: "strict",
        level: 5
    };
    console.log("mode=" + config.mode);
    console.log("level=" + config.level);

    // String operations
    function greet(name) {
        const greeting = "Hello, " + (name || "World");
        console.log(greeting);
    }
    greet("World");

    // JSON parsing
    const jsonString = '{"key":"value"}';
    const parsed = JSON.parse(jsonString);
    console.log("parsed=" + parsed.key);

    console.log("=== Type Assertions Complete ===");
}

runTests();
