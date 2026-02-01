// 075-async-await.ts
// Test async/await
function runTests() {
    console.log("=== Async/Await ===");

    // Basic async function
    async function getValue(): Promise<string> {
        return "value";
    }

    getValue().then((v) => console.log("async=" + v));

    // Await with Promise
    async function waitAndLog(): Promise<void> {
        const result = await Promise.resolve("result");
        console.log("await=" + result);
    }

    waitAndLog();

    // Async with error handling
    async function withError(): Promise<void> {
        try {
            await Promise.reject(new Error("failed"));
        } catch (e: any) {
            console.log("caught=" + e.message);
        }
    }

    withError();

    // Multiple awaits
    async function multiple(): Promise<void> {
        const a = await Promise.resolve(1);
        const b = await Promise.resolve(2);
        const c = await Promise.resolve(3);
        console.log("multiple=" + (a + b + c));
    }

    multiple();

    // Parallel awaits
    async function parallel(): Promise<void> {
        const [x, y, z] = await Promise.all([
            Promise.resolve(1),
            Promise.resolve(2),
            Promise.resolve(3)
        ]);
        console.log("parallel=" + (x + y + z));
    }

    parallel();

    // Async with delay simulation
    async function delay(ms: number): Promise<void> {
        await new Promise((resolve) => setTimeout(resolve, ms));
    }

    async function withDelay(): Promise<void> {
        console.log("before");
        await delay(100);
        console.log("after");
    }

    withDelay();

    // Async returning Promise
    async function returnsPromise(): Promise<number> {
        return await Promise.resolve(42);
    }

    returnsPromise().then((v) => console.log("returnPromise=" + v));

    // Async with finally
    async function withFinally(): Promise<void> {
        try {
            await Promise.resolve("data");
            console.log("tryBlock");
        } catch (e) {
            console.log("catchBlock");
        } finally {
            console.log("finallyBlock");
        }
    }

    withFinally();

    // Async arrow function
    const asyncArrow = async (x: number): Promise<number> => {
        return await Promise.resolve(x * 2);
    };

    asyncArrow(5).then((v) => console.log("arrow=" + v));

    // Async method in class
    class AsyncClass {
        async getData(): Promise<string> {
            return await Promise.resolve("data");
        }
    }

    const instance = new AsyncClass();
    instance.getData().then((d) => console.log("method=" + d));

    console.log("=== Async/Await Complete ===");
}

runTests();