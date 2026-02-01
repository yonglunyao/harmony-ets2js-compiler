// 009-intersection-types.ts
// Test intersection types (runtime behavior)
function runTests() {
    console.log("=== Intersection Types ===");

    // Basic intersection - runtime object with combined properties
    const obj = { a: "hello", b: 42 };
    console.log("a=" + obj.a);
    console.log("b=" + obj.b);

    // Multiple intersection - object with all properties
    const person = {
        name: "John",
        age: 30,
        email: "john@example.com"
    };
    console.log("name=" + person.name);
    console.log("age=" + person.age);
    console.log("email=" + person.email);

    // Intersection with methods
    const performer = {
        sing: () => console.log("singing"),
        dance: () => console.log("dancing")
    };
    performer.sing();
    performer.dance();

    // Practical intersection - object with both methods
    const item = {
        toJSON: () => JSON.stringify({ id: 1 }),
        save: () => console.log("saved")
    };
    console.log("json=" + item.toJSON());
    item.save();

    console.log("=== Intersection Types Complete ===");
}

runTests();
