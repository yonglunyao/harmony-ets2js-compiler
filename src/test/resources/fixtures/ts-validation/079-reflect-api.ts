// 079-reflect-api.ts
// Test Reflect API
function runTests() {
    console.log("=== Reflect API ===");

    const obj = { a: 1, b: 2, c: 3 };

    // Reflect.get
    const val = Reflect.get(obj, "a");
    console.log("get=" + val);

    // Reflect.set
    Reflect.set(obj, "d", 4);
    console.log("set=" + obj.d);

    // Reflect.has
    const hasA = Reflect.has(obj, "a");
    const hasX = Reflect.has(obj, "x");
    console.log("has=" + hasA + "," + hasX);

    // Reflect.deleteProperty
    const deleted = Reflect.deleteProperty(obj, "d");
    console.log("deleted=" + deleted);
    console.log("hasD=" + Reflect.has(obj, "d"));

    // Reflect.ownKeys
    const keys = Reflect.ownKeys(obj);
    console.log("keys=" + keys.join(","));

    // Reflect.getOwnPropertyDescriptor
    const desc = Reflect.getOwnPropertyDescriptor(obj, "a");
    console.log("desc=" + desc?.value);

    // Reflect.defineProperty
    Reflect.defineProperty(obj, "e", {
        value: 5,
        writable: true,
        enumerable: true,
        configurable: true
    });
    console.log("defined=" + obj.e);

    // Reflect.getPrototypeOf
    const proto = Reflect.getPrototypeOf(obj);
    console.log("proto=" + (proto === Object.prototype));

    // Reflect.setPrototypeOf
    const newProto = { inherited: "value" };
    Reflect.setPrototypeOf(obj, newProto);
    console.log("inherited=" + obj.inherited);

    // Reflect.apply
    function sum(a, b) {
        return a + b;
    }
    const applied = Reflect.apply(sum, null, [5, 3]);
    console.log("apply=" + applied);

    // Reflect.construct
    class Person {
        constructor(name) {
            this.name = name;
        }
    }
    const instance = Reflect.construct(Person, ["John"]);
    console.log("construct=" + instance.name);

    // Reflect.preventExtensions
    const locked = {};
    Reflect.preventExtensions(locked);
    const canExtend = Reflect.isExtensible(locked);
    console.log("extensible=" + canExtend);

    console.log("=== Reflect API Complete ===");
}

runTests();
