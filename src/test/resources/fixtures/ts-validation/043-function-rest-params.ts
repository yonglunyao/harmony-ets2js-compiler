// 043-function-rest-params.ts
// Test function rest parameters
function runTests() {
    console.log("=== Function Rest Parameters ===");

    // Basic rest parameter
    function sum(...numbers: number[]): number {
        return numbers.reduce((acc, n) => acc + n, 0);
    }
    console.log("sum=" + sum(1, 2, 3, 4, 5));

    // Rest with other parameters
    function greet(greeting: string, ...names: string[]): string {
        return greeting + " " + names.join(", ") + "!";
    }
    console.log("greet=" + greet("Hello", "John", "Jane", "Bob"));

    // Rest parameter type
    function logMessages(prefix: string, ...messages: string[]): void {
        messages.forEach((msg) => console.log(prefix + ": " + msg));
    }
    logMessages("INFO", "Starting", "Processing", "Done");

    // Rest in arrow function
    const multiplyAll = (...nums: number[]): number => {
        return nums.reduce((acc, n) => acc * n, 1);
    };
    console.log("multiply=" + multiplyAll(2, 3, 4));

    // Rest with destructuring
    interface IPoint {
        x: number;
        y: number;
    }

    function getBounds(...points: IPoint[]): string {
        const xs = points.map(p => p.x);
        const ys = points.map(p => p.y);
        return "x[" + Math.min(...xs) + "-" + Math.max(...xs) + "] y[" + Math.min(...ys) + "-" + Math.max(...ys) + "]";
    }
    console.log("bounds=" + getBounds({ x: 0, y: 0 }, { x: 10, y: 10 }, { x: 5, y: 5 }));

    // Rest parameter as array
    function join(separator: string, ...parts: string[]): string {
        return parts.join(separator);
    }
    console.log("join=" + join("-", "a", "b", "c"));

    // Multiple rest parameters (only last can be rest)
    function format(template: string, ...values: any[]): string {
        return template.replace(/\{(\d+)\}/g, (_, idx) => values[idx] ?? "");
    }
    console.log("format=" + format("Hello {0}, you are {1} years old", "John", 30));

    // Rest with default
    function process(action: string, ...items: string[]): string {
        if (items.length === 0) {
            return action + " (no items)";
        }
        return action + " " + items.join(", ");
    }
    console.log("process1=" + process("delete"));
    console.log("process2=" + process("add", "item1", "item2"));

    // Spread into rest parameter
    const numbers = [1, 2, 3, 4, 5];
    console.log("spread=" + sum(...numbers));

    console.log("=== Rest Parameters Complete ===");
}

runTests();