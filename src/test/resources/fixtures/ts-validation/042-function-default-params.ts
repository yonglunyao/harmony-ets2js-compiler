// 042-function-default-params.ts
// Test function default parameters
function runTests() {
    console.log("=== Function Default Parameters ===");

    // Basic default parameter
    function greet(name: string, greeting: string = "Hello"): string {
        return greeting + ", " + name + "!";
    }
    console.log("greet1=" + greet("John"));
    console.log("greet2=" + greet("Jane", "Hi"));

    // Multiple default parameters
    function createProfile(
        name: string,
        age: number = 30,
        city: string = "Unknown"
    ): string {
        return name + " is " + age + " from " + city;
    }
    console.log("profile1=" + createProfile("John"));
    console.log("profile2=" + createProfile("Jane", 25));
    console.log("profile3=" + createProfile("Bob", 35, "NYC"));

    // Default with expressions
    const defaultValue = 10;
    function calculate(x: number, y: number = defaultValue * 2): number {
        return x + y;
    }
    console.log("calc1=" + calculate(5));
    console.log("calc2=" + calculate(5, 20));

    // Default parameter before required
    function greetAll(greeting: string = "Hello", ...names: string[]): string {
        return greeting + " " + names.join(", ") + "!";
    }
    console.log("greetAll1=" + greetAll("Hi", "John", "Jane"));
    console.log("greetAll2=" + greetAll(undefined, "Bob"));

    // Default with undefined
    function testUndefined(value: string = "default"): string {
        return value;
    }
    console.log("undef1=" + testUndefined("custom"));
    console.log("undef2=" + testUndefined(undefined));
    console.log("undef3=" + testUndefined());

    // Default with null (null is valid)
    function testNull(value: string | null = null): string {
        return value ?? "default";
    }
    console.log("null1=" + testNull("value"));
    console.log("null2=" + testNull(null));

    // Object default
    interface IOptions {
        verbose?: boolean;
        timeout?: number;
    }

    function fetch(url: string, options: IOptions = {}): string {
        const verbose = options.verbose ?? false;
        const timeout = options.timeout ?? 5000;
        return url + " (v=" + verbose + ", t=" + timeout + ")";
    }
    console.log("fetch1=" + fetch("/api"));
    console.log("fetch2=" + fetch("/api", { verbose: true }));
    console.log("fetch3=" + fetch("/api", { timeout: 1000 }));

    console.log("=== Default Parameters Complete ===");
}

runTests();