// 005-void-never-types.ts
// Test void and never types
function runTests() {
    console.log("=== Void and Never Types ===");

    // Void function
    function logMessage(): void {
        console.log("void function executed");
    }
    logMessage();

    // Void return explicit
    function returnNothing(): void {
        return;
    }
    returnNothing();

    // Arrow function void
    const voidArrow: () => void = () => {
        console.log("arrow void");
    };
    voidArrow();

    // Function returning undefined (void-like)
    function getUndefined(): undefined {
        return undefined;
    }
    console.log("undef=" + getUndefined());

    // Never type (function that throws)
    function throwError(message: string): never {
        throw new Error(message);
    }

    // Never type (infinite loop function)
    function infiniteLoop(): never {
        while (true) {
            break;
        }
    }

    // Type checking void
    function checkVoid(fn: () => void): void {
        fn();
    }
    checkVoid(() => console.log("check void"));

    // Void as variable type (only undefined)
    let voidVar: void = undefined;
    console.log("voidVar=" + voidVar);

    // Practical void usage
    function processData(data: string, callback: () => void): void {
        console.log("data=" + data);
        callback();
    }

    processData("test", () => console.log("callback"));

    console.log("=== Void/Never Complete ===");
}

runTests();