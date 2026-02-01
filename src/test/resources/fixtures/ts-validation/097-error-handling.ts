// 097-error-handling.ts
// Test error handling patterns
function runTests() {
    console.log("=== Error Handling ===");

    // Custom error class
    class CustomError extends Error {
        constructor(message, code) {
            super(message);
            this.name = "CustomError";
            this.code = code;
        }
    }

    // Try-catch with custom error
    try {
        throw new CustomError("Something went wrong", 404);
    } catch (e) {
        if (e instanceof CustomError) {
            console.log("customError=" + e.message + " code=" + e.code);
        }
    }

    // Error type guard
    function isError(error) {
        return error instanceof Error;
    }

    function processError(error) {
        if (isError(error)) {
            console.log("error=" + error.message);
        } else {
            console.log("unknown=" + String(error));
        }
    }
    processError(new Error("test"));
    processError("string error");

    // Result type pattern
    function divide(a, b) {
        if (b === 0) {
            return { success: false, error: new Error("Division by zero") };
        }
        return { success: true, data: a / b };
    }

    const result1 = divide(10, 2);
    if (result1.success) {
        console.log("divide=" + result1.data);
    } else {
        console.log("error=" + result1.error.message);
    }

    const result2 = divide(10, 0);
    if (result2.success) {
        console.log("divide=" + result2.data);
    } else {
        console.log("error=" + result2.error.message);
    }

    // Option type pattern
    function findItem(arr, predicate) {
        const found = arr.find(predicate);
        return found ? { type: "some", value: found } : { type: "none" };
    }

    const items = [1, 2, 3, 4, 5];
    const found = findItem(items, (x) => x > 3);
    if (found.type === "some") {
        console.log("found=" + found.value);
    } else {
        console.log("notFound");
    }

    const notFound = findItem(items, (x) => x > 10);
    if (notFound.type === "some") {
        console.log("found=" + notFound.value);
    } else {
        console.log("notFound");
    }

    // Async error handling
    function fetchData(success) {
        return new Promise((resolve, reject) => {
            if (success) {
                resolve("data");
            }
            reject(new Error("Network error"));
        });
    }

    fetchData(true)
        .then((data) => console.log("async=" + data))
        .catch((e) => console.log("asyncError=" + e.message));

    fetchData(false)
        .then((data) => console.log("async=" + data))
        .catch((e) => console.log("asyncError=" + e.message));

    // Finally block
    let resource = null;
    try {
        resource = "acquired";
        console.log("resource=" + resource);
        throw new Error("error");
    } catch (e) {
        console.log("caught");
    } finally {
        resource = null;
        console.log("cleaned");
    }

    console.log("=== Error Handling Complete ===");
}

runTests();
