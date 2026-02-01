// 060-try-catch-finally.ts
// Test try/catch/finally statements
function runTests() {
    console.log("=== Try-Catch-Finally ===");

    // Basic try-catch
    console.log("basic:");
    try {
        const result = 10 / 2;
        console.log("  result=" + result);
    } catch (e) {
        console.log("  error=" + e);
    }

    // Try-catch with error
    console.log("withError:");
    try {
        const result = (10 as any) / 0;
        console.log("  result=" + result);
    } catch (e) {
        console.log("  caught");
    }

    // Finally block
    console.log("finally:");
    let resource: string | null = null;
    try {
        resource = "acquired";
        console.log("  " + resource);
    } finally {
        resource = null;
        console.log("  cleaned");
    }

    // Try-catch-finally
    console.log("allBlocks:");
    try {
        console.log("  try");
        throw new Error("test error");
    } catch (e) {
        console.log("  catch");
    } finally {
        console.log("  finally");
    }

    // Catch with error variable
    console.log("errorVar:");
    try {
        throw new Error("custom error");
    } catch (error) {
        console.log("  " + error);
    }

    // Try with multiple throws
    console.log("multiple:");
    const value = 1;
    try {
        if (value === 1) {
            throw "string error";
        } else if (value === 2) {
            throw new Error("object error");
        }
    } catch (e) {
        console.log("  caught=" + e);
    }

    // Nested try-catch
    console.log("nested:");
    try {
        console.log("  outer try");
        try {
            console.log("  inner try");
            throw "inner error";
        } catch (e) {
            console.log("  inner catch=" + e);
        }
    } catch (e) {
        console.log("  outer catch=" + e);
    }

    // Finally executes even with return
    console.log("returnFinally:");
    function withFinally(): string {
        try {
            return "try return";
        } finally {
            console.log("  finally runs");
        }
    }
    console.log("  " + withFinally());

    // Catch specific error types
    console.log("specificError:");
    try {
        const obj: any = null;
        obj.method();
    } catch (e) {
        console.log("  typeError");
    }

    // Rethrow error
    console.log("rethrow:");
    try {
        try {
            throw new Error("original");
        } catch (e) {
            console.log("  inner catch");
            throw e;
        }
    } catch (e) {
        console.log("  outer catch");
    }

    console.log("=== Try-Catch-Finally Complete ===");
}

runTests();