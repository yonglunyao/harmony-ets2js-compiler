// 041-function-basics.ts
// Test function basics
function runTests() {
    console.log("=== Function Basics ===");

    // Function declaration
    function add(a, b) {
        return a + b;
    }
    console.log("add=" + add(5, 3));

    // Function expression
    const subtract = function(a, b) {
        return a - b;
    };
    console.log("subtract=" + subtract(10, 4));

    // Arrow function
    const multiply = (a, b) => {
        return a * b;
    };
    console.log("multiply=" + multiply(6, 7));

    // Concise arrow function
    const divide = (a, b) => a / b;
    console.log("divide=" + divide(20, 4));

    // No return type (void)
    function logMessage(message) {
        console.log("log=" + message);
    }
    logMessage("Hello");

    // Multiple parameters
    function greet(greeting, name) {
        return greeting + ", " + name + "!";
    }
    console.log("greet=" + greet("Hello", "World"));

    // Function as parameter
    function execute(fn) {
        fn();
    }
    execute(() => console.log("executed"));

    // Function returning function
    function createMultiplier(factor) {
        return (x) => x * factor;
    }
    const triple = createMultiplier(3);
    console.log("triple=" + triple(5));

    // Optional parameter
    function buildName(firstName, lastName) {
        return lastName ? firstName + " " + lastName : firstName;
    }
    console.log("name1=" + buildName("John"));
    console.log("name2=" + buildName("John", "Doe"));

    console.log("=== Function Basics Complete ===");
}

runTests();
