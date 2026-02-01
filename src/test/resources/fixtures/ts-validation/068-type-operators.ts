// 068-type-operators.ts
// Test type operators
function runTests() {
    console.log("=== Type Operators ===");

    // typeof operator
    const num = 42;
    const str = "hello";
    const bool = true;
    const obj = { a: 1 };
    const arr = [1, 2, 3];
    const nul: null = null;
    const undef = undefined;
    const func = function() {};

    console.log("number=" + typeof num);
    console.log("string=" + typeof str);
    console.log("boolean=" + typeof bool);
    console.log("object=" + typeof obj);
    console.log("array=" + typeof arr);
    console.log("null=" + typeof nul);
    console.log("undefined=" + typeof undef);
    console.log("function=" + typeof func);

    // instanceof operator
    const date = new Date();
    const regex = /test/;
    const error = new Error("test");

    console.log("date is Date=" + (date instanceof Date));
    console.log("regex is RegExp=" + (regex instanceof RegExp));
    console.log("error is Error=" + (error instanceof Error));
    console.log("arr is Array=" + (arr instanceof Array));
    console.log("obj is Object=" + (obj instanceof Object));

    // instanceof with custom classes
    class Animal {}
    class Dog extends Animal {}

    const animal = new Animal();
    const dog = new Dog();

    console.log("dog is Dog=" + (dog instanceof Dog));
    console.log("dog is Animal=" + (dog instanceof Animal));
    console.log("animal is Dog=" + (animal instanceof Dog));

    // in operator
    const person = { name: "John", age: 30 };
    console.log("name in person=" + ("name" in person));
    console.log("email in person=" + ("email" in person));

    const arr2 = [1, 2, 3];
    console.log("0 in arr=" + (0 in arr2));
    console.log("3 in arr=" + (3 in arr2));

    // delete operator
    const obj2: any = { a: 1, b: 2, c: 3 };
    console.log("before delete=" + ("b" in obj2));
    delete obj2.b;
    console.log("after delete=" + ("b" in obj2));

    // void operator
    const voidResult = void 0;
    console.log("void=" + voidResult);

    // typeof as type guard
    function process(value: string | number): string {
        if (typeof value === "string") {
            return value.toUpperCase();
        } else {
            return (value * 2).toString();
        }
    }
    console.log("processStr=" + process("hello"));
    console.log("processNum=" + process(5));

    // instanceof as type guard
    function identify(value: Date | RegExp): string {
        if (value instanceof Date) {
            return "Date: " + value.toISOString();
        } else {
            return "RegExp: " + value.source;
        }
    }
    console.log("identify1=" + identify(new Date()));
    console.log("identify2=" + identify(/test/));

    console.log("=== Type Operators Complete ===");
}

runTests();