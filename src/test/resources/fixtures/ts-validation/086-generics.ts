// 086-generics.ts
// Test generics (runtime behavior without generics)
function runTests() {
    console.log("=== Generics ===");

    // Generic function (runtime simulation)
    function identity(arg) {
        return arg;
    }

    const num = identity(42);
    const str = identity("hello");
    console.log("generic=" + num + "," + str);

    // Type inference (runtime simulation)
    const inferred = identity(true);
    console.log("inferred=" + inferred);

    // Generic with multiple types (runtime simulation)
    function pair(first, second) {
        return [first, second];
    }

    const p = pair("hello", 42);
    console.log("pair=" + p[0] + "," + p[1]);

    // Generic class (runtime simulation)
    class Stack {
        constructor() {
            this.items = [];
        }

        push(item) {
            this.items.push(item);
        }

        pop() {
            return this.items.pop();
        }

        peek() {
            return this.items[this.items.length - 1];
        }
    }

    const stack = new Stack();
    stack.push(1);
    stack.push(2);
    console.log("stack=" + stack.peek());
    console.log("pop=" + stack.pop());

    // Generic constraints (runtime simulation)
    function getLength(arg) {
        return arg.length;
    }

    console.log("len=" + getLength("hello"));
    console.log("len=" + getLength([1, 2, 3]));

    // Using type parameter in generic constraints (runtime simulation)
    function getProperty(obj, key) {
        return obj[key];
    }

    const person = { name: "John", age: 30 };
    console.log("prop=" + getProperty(person, "name"));

    // Generic with default type (runtime simulation)
    function createArray(length, value) {
        return Array(length).fill(value);
    }

    const arr1 = createArray(3, 5);
    const arr2 = createArray(2, "test");
    console.log("arr1=" + arr1.join(","));
    console.log("arr2=" + arr2.join(","));

    // Generic factory (runtime simulation)
    function createInstance(ctor, ...args) {
        return new ctor(...args);
    }

    class Product {
        constructor(name) {
            this.name = name;
        }
    }

    const product = createInstance(Product, "Widget");
    console.log("factory=" + product.name);

    console.log("=== Generics Complete ===");
}

runTests();
