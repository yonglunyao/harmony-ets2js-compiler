// 091-null-undefined-handling.ts
// Test null and undefined handling
function runTests() {
    console.log("=== Null/Undefined Handling ===");

    // Null check
    let value = null;
    if (value !== null) {
        console.log("notNull=" + value);
    } else {
        console.log("isNull");
    }

    value = "test";
    if (value !== null) {
        console.log("nowNotNull=" + value);
    }

    // Undefined check
    let maybe;
    if (maybe !== undefined) {
        console.log("defined=" + maybe);
    } else {
        console.log("undefined");
    }

    maybe = "value";
    if (maybe !== undefined) {
        console.log("nowDefined=" + maybe);
    }

    // Nullish coalescing
    const input = null;
    const result = input ?? "default";
    console.log("coalesce=" + result);

    const input2 = undefined;
    const result2 = input2 ?? "fallback";
    console.log("coalesce2=" + result2);

    // Optional chaining
    const obj = { nested: { value: "test" } };
    const val = obj?.nested?.value ?? "none";
    console.log("chain=" + val);

    const empty = null;
    const emptyVal = empty?.nested ?? "empty";
    console.log("empty=" + emptyVal);

    // Null assertion operator (runtime simulation)
    let definitely = "value";
    console.log("assert=" + definitely);

    // Type guard for null/undefined (runtime simulation)
    function isNotNull(value) {
        return value !== null;
    }

    function isDefined(value) {
        return value !== undefined;
    }

    const nullable = "test";
    if (isNotNull(nullable)) {
        console.log("guard=" + nullable.toUpperCase());
    }

    // Non-null assertion with optional (runtime simulation)
    function process(str) {
        return str ?? "default";
    }
    console.log("process=" + process(null));
    console.log("process=" + process("value"));

    // Array filter null/undefined
    const items = ["a", null, "b", undefined, "c"];
    const clean = items.filter((item) => item != null);
    console.log("clean=" + clean.join(","));

    // Null check in object (runtime simulation)
    const opt = { name: "John" };
    console.log("optName=" + (opt.name ?? "unknown"));
    console.log("optAge=" + (opt.age ?? 0));

    // Default parameter for null/undefined
    function greet(name) {
        const n = name !== undefined && name !== null ? name : "Guest";
        return "Hello, " + n;
    }
    console.log("greet1=" + greet(null));
    console.log("greet2=" + greet("Alice"));

    console.log("=== Null/Undefined Handling Complete ===");
}

runTests();
