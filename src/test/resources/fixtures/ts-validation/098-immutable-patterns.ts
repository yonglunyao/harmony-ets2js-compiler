// 098-immutable-patterns.ts
// Test immutable patterns
function runTests() {
    console.log("=== Immutable Patterns ===");

    // Readonly arrays (runtime simulation)
    const readonlyArr = [1, 2, 3];
    console.log("readonly=" + readonlyArr.join(","));

    // Readonly with spread
    const original = [1, 2, 3];
    const newArray = [...original, 4];
    console.log("immutablePush=" + newArray.join(","));

    // Array immutability helpers
    function immutablePush(arr, item) {
        return [...arr, item];
    }

    function immutablePop(arr) {
        return arr.slice(0, -1);
    }

    function immutableFilter(arr, predicate) {
        return arr.filter(predicate);
    }

    const arr1 = [1, 2, 3, 4];
    const pushed = immutablePush(arr1, 5);
    const popped = immutablePop(arr1);
    const filtered = immutableFilter(arr1, (x) => x > 2);

    console.log("push=" + pushed.join(","));
    console.log("pop=" + popped.join(","));
    console.log("filter=" + filtered.join(","));

    // Readonly objects (runtime simulation)
    const readonlyPerson = { name: "John", age: 30 };
    console.log("person=" + readonlyPerson.name);

    // Object spread for immutability
    const originalObj = { a: 1, b: 2 };
    const updatedObj = { ...originalObj, b: 3, c: 4 };
    console.log("updated=" + JSON.stringify(updatedObj));

    // Deep clone simulation
    function deepClone(obj) {
        return JSON.parse(JSON.stringify(obj));
    }

    const nested = { a: { b: { c: 1 } } };
    const cloned = deepClone(nested);
    cloned.a.b.c = 2;
    console.log("orig=" + nested.a.b.c);
    console.log("clone=" + cloned.a.b.c);

    // Immutable update helpers
    function updateProp(obj, key, value) {
        return { ...obj, [key]: value };
    }

    const person = { name: "Jane", age: 25 };
    const updated = updateProp(person, "age", 26);
    console.log("updatedProp=" + updated.age);

    // Immutable class
    class ImmutablePoint {
        constructor(x, y) {
            this.x = x;
            this.y = y;
        }

        withX(newX) {
            return new ImmutablePoint(newX, this.y);
        }

        withY(newY) {
            return new ImmutablePoint(this.x, newY);
        }
    }

    const point = new ImmutablePoint(10, 20);
    const newPoint = point.withX(15);
    console.log("point=" + point.x + "," + point.y);
    console.log("newPoint=" + newPoint.x + "," + newPoint.y);

    // Freeze
    const frozen = Object.freeze({ a: 1, b: 2 });
    console.log("frozen=" + JSON.stringify(frozen));

    console.log("=== Immutable Patterns Complete ===");
}

runTests();
