// 088-mapped-types.ts
// Test mapped types (runtime behavior without mapped types)
function runTests() {
    console.log("=== Mapped Types ===");

    // Basic mapped type (runtime simulation)
    const readonlyPerson = { name: "John", age: 30 };
    console.log("readonly=" + readonlyPerson.name);

    // Optional mapped type (runtime simulation)
    const partialPerson = { name: "Jane" };
    console.log("partial=" + partialPerson.name);

    // Nullable mapped type (runtime simulation)
    const nullablePerson = { name: null, age: 25 };
    console.log("nullable=" + nullablePerson.name);

    // Pick mapped type (runtime simulation)
    const nameOnly = { name: "Bob" };
    console.log("pick=" + nameOnly.name);

    // Record mapped type (runtime simulation)
    const page = { title: "Home", url: "/" };
    console.log("record=" + page.title);

    // Modify properties with mapped type (runtime simulation)
    const mutable = { x: 10, y: 20 };
    mutable.x = 15;
    console.log("mutable=" + mutable.x);

    // Add modifiers with mapped type (runtime simulation)
    const required = { name: "Test", age: 0 };
    console.log("required=" + required.name);

    // Template literal key mapped type (runtime simulation)
    const getters = {
        getName: () => "John",
        getAge: () => 30
    };
    console.log("getters=" + getters.getName());

    console.log("=== Mapped Types Complete ===");
}

runTests();
