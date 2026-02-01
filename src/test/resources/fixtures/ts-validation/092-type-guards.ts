// 092-type-guards.ts
// Test type guards (runtime behavior without type guards)
function runTests() {
    console.log("=== Type Guards ===");

    // typeof type guard
    function process(value) {
        if (typeof value === "string") {
            return "string: " + value.toUpperCase();
        } else {
            return "number: " + (value * 2);
        }
    }
    console.log("typeofStr=" + process("hello"));
    console.log("typeofNum=" + process(5));

    // instanceof type guard
    class Dog {
        bark() { console.log("woof"); }
    }

    class Cat {
        meow() { console.log("meow"); }
    }

    function makeSound(animal) {
        if (animal instanceof Dog) {
            animal.bark();
        } else {
            animal.meow();
        }
    }
    makeSound(new Dog());
    makeSound(new Cat());

    // in operator type guard
    function move(creature) {
        if ("fly" in creature) {
            creature.fly();
        } else {
            creature.swim();
        }
    }
    move({ fly: () => console.log("flying") });
    move({ swim: () => console.log("swimming") });

    // Custom type guard (runtime simulation)
    function isString(value) {
        return typeof value === "string";
    }

    function isNumber(value) {
        return typeof value === "number";
    }

    function processUnknown(value) {
        if (isString(value)) {
            return "str=" + value;
        } else if (isNumber(value)) {
            return "num=" + value;
        }
        return "unknown";
    }
    console.log("guardStr=" + processUnknown("test"));
    console.log("guardNum=" + processUnknown(42));

    // Type guard for array (runtime simulation)
    function isArray(value) {
        return Array.isArray(value);
    }

    function processArray(value) {
        if (isArray(value)) {
            return "array=" + value.join(",");
        }
        return "not array";
    }
    console.log("arrGuard=" + processArray([1, 2, 3]));
    console.log("arrGuard=" + processArray("not array"));

    // Type guard with null check (runtime simulation)
    function isDefined(value) {
        return value != null;
    }

    const items = ["a", null, "b"];
    const valid = items.filter(isDefined);
    console.log("filter=" + valid.join(","));

    // Discriminated union type guard (runtime simulation)
    function area(shape) {
        if (shape.kind === "circle") {
            return Math.PI * shape.radius * shape.radius;
        } else {
            return shape.side * shape.side;
        }
    }
    console.log("circleArea=" + area({ kind: "circle", radius: 5 }));
    console.log("squareArea=" + area({ kind: "square", side: 4 }));

    console.log("=== Type Guards Complete ===");
}

runTests();
