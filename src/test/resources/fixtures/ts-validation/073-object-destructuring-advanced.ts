// 073-object-destructuring-advanced.ts
// Test advanced object destructuring
function runTests() {
    console.log("=== Advanced Object Destructuring ===");

    // Deep nested destructuring
    const deep = {
        a: {
            b: {
                c: {
                    d: "value"
                }
            }
        }
    };
    const { a: { b: { c: { d } } } } = deep;
    console.log("deep=" + d);

    // Destructuring with computed keys
    const key = "name";
    const obj = { name: "John", age: 30 };
    const { [key]: propName } = obj;
    console.log("computed=" + propName);

    // Destructuring from function return
    function getPerson() {
        return { name: "Jane", age: 25, city: "LA" };
    }
    const { name: pName, age: pAge } = getPerson();
    console.log("funcReturn=" + pName + "," + pAge);

    // Destructuring with spread and override
    const base = { a: 1, b: 2, c: 3 };
    const { a, ...rest } = base;
    console.log("spreadRest=" + a + " rest=" + JSON.stringify(rest));

    // Multiple object destructuring
    const obj1 = { x: 1, y: 2 };
    const obj2 = { z: 3, w: 4 };
    const { x, y } = obj1;
    const { z, w } = obj2;
    console.log("multi=" + x + "," + y + "," + z + "," + w);

    // Destructuring with type coercion
    const str = "abc";
    const { length } = str;
    console.log("length=" + length);

    // Destructuring array-like object
    const arrayLike = { 0: "a", 1: "b", 2: "c", length: 3 };
    const { 0: first, 1: second, length: len } = arrayLike;
    console.log("arrayLike=" + first + "," + second + "," + len);

    // Destructuring in class methods
    class Processor {
        process({ value, multiplier = 1 }) {
            return value * multiplier;
        }
    }
    const proc = new Processor();
    console.log("method=" + proc.process({ value: 10, multiplier: 2 }));

    // Destructuring with rename and default
    const config = { timeout: 1000 };
    const { timeout: t = 5000, retries: r = 3 } = config;
    console.log("renameDefault=" + t + "," + r);

    // Destructuring nested with defaults
    const data = {};
    const { user: { name = "Anonymous" } = {} } = data;
    console.log("nestedDefault=" + name);

    // Destructuring from JSON
    const json = '{"id":1,"name":"test"}';
    const { id, name } = JSON.parse(json);
    console.log("json=" + id + "," + name);

    console.log("=== Advanced Object Destructuring Complete ===");
}

runTests();
