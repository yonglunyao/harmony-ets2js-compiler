// 085-type-aliases.ts
// Test type aliases (runtime behavior without type aliases)
function runTests() {
    console.log("=== Type Aliases ===");

    // Basic type alias (runtime simulation - just values)
    const id1 = "abc";
    const id2 = 123;
    console.log("id=" + id1 + "," + id2);

    // Object type alias (runtime simulation)
    const person = { name: "John", age: 30 };
    console.log("person=" + person.name);

    // Union type alias (runtime simulation)
    function process(value) {
        return String(value);
    }
    console.log("union=" + process(42));

    // Intersection type alias (runtime simulation)
    const person2 = { name: "Jane", age: 25 };
    console.log("intersect=" + person2.name);

    // Function type alias (runtime simulation)
    const add = (a, b) => a + b;
    console.log("funcType=" + add(5, 3));

    // Array type alias (runtime simulation)
    const nums = [1, 2, 3];
    console.log("array=" + nums.join(","));

    // Tuple type alias (runtime simulation)
    const coord = [100, 200];
    console.log("tuple=" + coord[0] + "," + coord[1]);

    console.log("=== Type Aliases Complete ===");
}

runTests();
