// 046-arrow-functions.ts
// Test arrow functions
function runTests() {
    console.log("=== Arrow Functions ===");

    // Basic arrow function
    const add = (a, b) => {
        return a + b;
    };
    console.log("add=" + add(5, 3));

    // Concise arrow (implicit return)
    const multiply = (a, b) => a * b;
    console.log("multiply=" + multiply(6, 7));

    // Single parameter (no parentheses needed)
    const double = (x) => x * 2;
    console.log("double=" + double(5));

    // No parameters
    const getValue = () => 42;
    console.log("value=" + getValue());

    // Arrow function with object literal (need parentheses)
    const createPerson = (name) => ({ name: name, age: 30 });
    console.log("person=" + JSON.stringify(createPerson("John")));

    // Arrow function as callback
    const numbers = [1, 2, 3, 4, 5];
    const doubled = numbers.map((n) => n * 2);
    console.log("doubled=" + doubled.join(","));

    // Arrow function in filter
    const evens = numbers.filter((n) => n % 2 === 0);
    console.log("evens=" + evens.join(","));

    // Arrow function in reduce
    const sum = numbers.reduce((acc, n) => acc + n, 0);
    console.log("sum=" + sum);

    // Arrow function with this (lexical this)
    class Counter {
        constructor() {
            this.count = 0;
        }

        increment = () => {
            this.count++;
        };

        getCount = () => {
            return this.count;
        };
    }

    const counter = new Counter();
    counter.increment();
    counter.increment();
    console.log("counter=" + counter.getCount());

    // Arrow function as method
    const calculator = {
        add: (a, b) => a + b,
        subtract: (a, b) => a - b,
        multiply: (a, b) => a * b
    };
    console.log("calc=" + calculator.add(5, 3));

    // Arrow function with destructuring
    const users = [{ name: "John", age: 30 }, { name: "Jane", age: 25 }];
    const names = users.map(({ name }) => name);
    console.log("names=" + names.join(","));

    // IIFE arrow function
    ((message) => {
        console.log("iife=" + message);
    })("Hello IIFE");

    // Arrow function returning function
    const createMultiplier = (factor) => (x) => x * factor;
    const triple = createMultiplier(3);
    console.log("triple=" + triple(5));

    console.log("=== Arrow Functions Complete ===");
}

runTests();
