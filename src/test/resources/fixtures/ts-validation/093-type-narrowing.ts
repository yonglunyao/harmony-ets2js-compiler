// 093-type-narrowing.ts
// Test type narrowing (runtime behavior without type narrowing)
function runTests() {
    console.log("=== Type Narrowing ===");

    // typeof narrowing
    function double(value) {
        if (typeof value === "string") {
            console.log("str=" + value + value);
        } else {
            console.log("num=" + (value * 2));
        }
    }
    double("test");
    double(5);

    // Truthiness narrowing
    function printLength(str) {
        if (str) {
            console.log("len=" + str.length);
        } else {
            console.log("empty");
        }
    }
    printLength("hello");
    printLength("");
    printLength(null);

    // Equality narrowing
    function compare(a, b) {
        if (a === b) {
            console.log("both string=" + a);
        } else {
            console.log("different");
        }
    }
    compare("hello", "hello");
    compare(5, "test");

    // instanceof narrowing
    class Dog {
        bark() { console.log("woof"); }
    }
    class Cat {
        meow() { console.log("meow"); }
    }

    function speak(pet) {
        if (pet instanceof Dog) {
            pet.bark();
        } else {
            pet.meow();
        }
    }
    speak(new Dog());
    speak(new Cat());

    // Assignments narrowing
    let x = "hello";
    console.log("x=" + x.toUpperCase());
    x = 42;
    console.log("x=" + x * 2);

    // in operator narrowing
    function process(obj) {
        if ("a" in obj) {
            console.log("hasA=" + obj.a);
        } else {
            console.log("hasB=" + obj.b);
        }
    }
    process({ a: "test" });
    process({ b: 42 });

    // Discriminated unions
    function handle(result) {
        if (result.status === "success") {
            console.log("data=" + result.data);
        } else {
            console.log("error=" + result.error);
        }
    }
    handle({ status: "success", data: "value" });
    handle({ status: "error", error: "failed" });

    // Assertion functions (runtime simulation)
    function assertIsString(value) {
        if (typeof value !== "string") {
            throw new Error("not string");
        }
    }

    function processValue(value) {
        assertIsString(value);
        console.log("asserted=" + value.toUpperCase());
    }
    processValue("test");

    console.log("=== Type Narrowing Complete ===");
}

runTests();
