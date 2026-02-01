// 094-utility-types.ts
// Test utility types (runtime behavior without utility types)
function runTests() {
    console.log("=== Utility Types ===");

    // Partial (runtime simulation)
    const partial = { name: "John" };
    console.log("partial=" + partial.name);

    // Required (runtime simulation)
    const required = { name: "Jane", age: 30 };
    console.log("required=" + required.name);

    // Readonly (runtime simulation)
    const readonly = { name: "Bob", age: 25, email: "bob@test.com" };
    console.log("readonly=" + readonly.name);

    // Record (runtime simulation)
    const record = {
        user1: { name: "Alice", age: 28, email: "alice@test.com" }
    };
    console.log("record=" + record.user1.name);

    // Pick (runtime simulation)
    const picked = { name: "Charlie", email: "charlie@test.com" };
    console.log("pick=" + picked.name);

    // Omit (runtime simulation)
    const omitted = { name: "David", age: 35 };
    console.log("omit=" + omitted.name);

    // Exclude (runtime simulation)
    const excluded = "test";
    console.log("exclude=" + excluded);

    // Extract (runtime simulation)
    const extracted = 42;
    console.log("extract=" + extracted);

    // NonNullable (runtime simulation)
    const nonNullable = "value";
    console.log("nonNull=" + nonNullable);

    // ReturnType (runtime simulation)
    function getUser() {
        return { name: "Eve", age: 22, email: "eve@test.com" };
    }
    const userReturn = { name: "Frank", age: 40, email: "frank@test.com" };
    console.log("return=" + userReturn.name);

    // Parameters (runtime simulation)
    function hasParams() {
        return false;
    }
    console.log("params=" + hasParams());

    // ConstructorParameters (runtime simulation)
    class Person {
        constructor(name, age) {}
    }
    const personParams = ["George", 45];
    console.log("ctorParams=" + personParams[0]);

    console.log("=== Utility Types Complete ===");
}

runTests();
