// 049-function-callbacks.ts
// Test callback functions
function runTests() {
    console.log("=== Callback Functions ===");

    // Simple callback
    function greet(name: string, callback: (msg: string) => void): void {
        const message = "Hello, " + name;
        callback(message);
    }

    greet("John", (msg: string) => console.log("callback=" + msg));

    // Callback with return value
    function compute(x: number, y: number, operation: (a: number, b: number) => number): number {
        return operation(x, y);
    }

    console.log("add=" + compute(5, 3, (a, b) => a + b));
    console.log("mul=" + compute(5, 3, (a, b) => a * b));

    // Array methods with callbacks
    const numbers = [1, 2, 3, 4, 5];

    numbers.forEach((n: number, i: number) => {
        console.log("forEach=" + i + ":" + n);
    });

    const doubled = numbers.map((n: number) => n * 2);
    console.log("map=" + doubled.join(","));

    const evens = numbers.filter((n: number) => n % 2 === 0);
    console.log("filter=" + evens.join(","));

    const sum = numbers.reduce((acc: number, n: number) => acc + n, 0);
    console.log("reduce=" + sum);

    // Async callback simulation
    function fetchData(url: string, callback: (data: string) => void): void {
        console.log("fetching=" + url);
        callback("data from " + url);
    }

    fetchData("/api/users", (data: string) => {
        console.log("received=" + data);
    });

    // Error-first callback pattern
    function readFile(path: string, callback: (err: string | null, data?: string) => void): void {
        if (path.includes("error")) {
            callback("File not found");
        } else {
            callback(null, "Content of " + path);
        }
    }

    readFile("/data.txt", (err, data) => {
        if (err) {
            console.log("error=" + err);
        } else {
            console.log("file=" + data);
        }
    });

    // Multiple callbacks
    function process(value: number, onSuccess: (r: number) => void, onError: (e: string) => void): void {
        if (value > 0) {
            onSuccess(value * 2);
        } else {
            onError("Invalid value");
        }
    }

    process(5, (result) => console.log("success=" + result), (error) => console.log("fail=" + error));
    process(-1, (result) => console.log("success=" + result), (error) => console.log("fail=" + error));

    // Callback as parameter
    function executeCallbacks(callbacks: (() => void)[]): void {
        callbacks.forEach((cb) => cb());
    }

    executeCallbacks([
        () => console.log("cb1"),
        () => console.log("cb2"),
        () => console.log("cb3")
    ]);

    // Chained callbacks
    function first(callback: () => void): void {
        console.log("first");
        callback();
    }

    function second(callback: () => void): void {
        console.log("second");
        callback();
    }

    function third(): void {
        console.log("third");
    }

    first(() => second(() => third()));

    console.log("=== Callback Functions Complete ===");
}

runTests();