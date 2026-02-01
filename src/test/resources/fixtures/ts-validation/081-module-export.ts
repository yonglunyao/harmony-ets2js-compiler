// 081-module-export.ts
// Test module exports (runtime behavior without module system)
function runTests() {
    console.log("=== Module Exports ===");

    // Named exports - values (runtime simulation)
    const PI = 3.14159;
    console.log("PI=" + PI);

    // Named export - function (runtime simulation)
    function add(a, b) {
        return a + b;
    }
    console.log("add=" + add(5, 3));

    // Named export - class (runtime simulation)
    class Calculator {
        multiply(a, b) {
            return a * b;
        }
    }
    const calc = new Calculator();
    console.log("multiply=" + calc.multiply(4, 5));

    // Named export - interface (runtime simulation - duck typing)
    const user = { name: "John", age: 30 };
    console.log("user=" + user.name + "," + user.age);

    // Export list (runtime simulation)
    const value1 = 1;
    const value2 = 2;
    console.log("exported=" + value1 + "," + value2);

    // Export with rename (runtime simulation)
    const internalName = "internal";
    const externalName = internalName;
    console.log("renamed=" + externalName);

    // Default export - function (runtime simulation)
    function defaultFunction() {
        return "default export";
    }
    console.log("default=" + defaultFunction());

    // Export declaration (runtime simulation)
    const Exported = {
        value: "exported"
    };
    console.log("obj=" + Exported.value);

    // Inline export (runtime simulation)
    const inline = "inline export";
    console.log("inline=" + inline);

    console.log("=== Module Exports Complete ===");
}

runTests();
