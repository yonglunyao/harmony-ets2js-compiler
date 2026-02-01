// 084-interfaces.ts
// Test interfaces (runtime behavior without interface enforcement)
function runTests() {
    console.log("=== Interfaces ===");

    // Basic interface (runtime duck typing)
    const person = { name: "John", age: 30 };
    console.log("person=" + person.name + "," + person.age);

    // Optional properties (runtime duck typing)
    const product1 = { id: 1, name: "Widget" };
    const product2 = { id: 2, name: "Gadget", price: 9.99 };
    console.log("prod1=" + product1.name);
    console.log("prod2=" + product2.price);

    // Readonly properties (runtime duck typing)
    const config = { apiKey: "xxx", endpoint: "/api" };
    console.log("config=" + config.endpoint);

    // Interface methods (runtime duck typing)
    const greeter = {
        greet: (msg) => "Hello, " + msg,
        farewell: () => "Goodbye"
    };
    console.log("greet=" + greeter.greet("World"));

    // Interface with call signature (runtime duck typing)
    const factory = function(name) {
        return name.toUpperCase();
    };
    factory.description = "Converts to uppercase";
    console.log("factory=" + factory("test"));

    // Interface with index signature (runtime duck typing)
    const arr = ["a", "b", "c"];
    console.log("arr=" + arr[0]);

    const dict = { name: "John", city: "NYC" };
    console.log("dict=" + dict.name);

    // Interface extending (runtime duck typing)
    const dog = { name: "Buddy", breed: "Lab" };
    console.log("dog=" + dog.name + "," + dog.breed);

    // Multiple extends (runtime duck typing)
    const frog = {
        name: "Frog",
        walk: () => console.log("walking"),
        swim: () => console.log("swimming")
    };
    frog.walk();
    frog.swim();

    // Hybrid types (runtime duck typing)
    function getCounter() {
        const counter = function(start) {
            return "count=" + start;
        };
        counter.interval = 100;
        counter.reset = function() {};
        return counter;
    }

    const c = getCounter();
    console.log("counter=" + c(10));

    console.log("=== Interfaces Complete ===");
}

runTests();
