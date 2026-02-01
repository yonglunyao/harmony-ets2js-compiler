// 026-object-prototype.ts
// Test object prototype
function runTests() {
    console.log("=== Object Prototype ===");

    // Create object with prototype
    const proto = {
        greet: function() {
            console.log("Hello, " + this.name);
        }
    };

    const obj1 = Object.create(proto);
    obj1.name = "John";
    obj1.greet();

    // Prototype chain
    const animal = {
        eat: function() {
            console.log("eating");
        }
    };

    const dog = Object.create(animal);
    dog.bark = function() {
        console.log("barking");
    };

    dog.eat();
    dog.bark();

    // Check prototype
    const isProto = Object.prototype.isPrototypeOf.call(proto, obj1);
    console.log("isProto=" + isProto);

    // Get prototype
    const protoOf = Object.getPrototypeOf(obj1);
    console.log("protoHasGreet=" + ("greet" in protoOf));

    // Set prototype
    const newProto = { newMethod: () => console.log("new") };
    const obj2 = { x: 1 };
    Object.setPrototypeOf(obj2, newProto);
    newProto.newMethod();

    // hasOwnProperty
    const obj3 = { own: "value", toString: () => "custom" };
    console.log("hasOwn=" + obj3.hasOwnProperty("own"));
    console.log("hasToString=" + obj3.hasOwnProperty("toString"));

    // PropertyIsEnumerable
    const obj4 = { a: 1, b: 2 };
    console.log("enumerable=" + obj4.propertyIsEnumerable("a"));

    console.log("=== Object Prototype Complete ===");
}

runTests();
