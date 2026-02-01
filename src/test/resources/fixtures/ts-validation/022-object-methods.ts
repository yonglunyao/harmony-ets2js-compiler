// 022-object-methods.ts
// Test object methods
function runTests() {
    console.log("=== Object Methods ===");

    // Object with methods
    const calculator = {
        add: function(a, b) {
            return a + b;
        },
        subtract: (a, b) => a - b,
        multiply(a, b) {
            return a * b;
        }
    };

    console.log("add=" + calculator.add(5, 3));
    console.log("sub=" + calculator.subtract(10, 4));
    console.log("mul=" + calculator.multiply(6, 7));

    // Method shorthand
    const greeter = {
        name: "World",
        greet() {
            console.log("Hello, " + this.name);
        },
        greetFormal() {
            console.log("Greetings, " + this.name);
        }
    };
    greeter.greet();
    greeter.greetFormal();

    // Computed method names
    const methodName = "sayHello";
    const obj = {
        [methodName]() {
            console.log("Hello from computed");
        }
    };
    obj.sayHello();

    // Object.assign
    const target = { a: 1 };
    const source1 = { b: 2 };
    const source2 = { c: 3 };
    const merged = Object.assign(target, source1, source2);
    console.log("assign=" + JSON.stringify(merged));

    // Object.create
    const proto = { greet: function() { console.log("proto"); } };
    const obj2 = Object.create(proto);
    obj2.greet();

    // Object.freeze
    const frozen = Object.freeze({ x: 1, y: 2 });
    console.log("frozen=" + JSON.stringify(frozen));

    // Object.seal
    const sealed = Object.seal({ a: 1, b: 2 });
    console.log("sealed=" + JSON.stringify(sealed));

    console.log("=== Object Methods Complete ===");
}

runTests();
