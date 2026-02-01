// 017-array-tuple-types.ts
// Test tuple types
function runTests() {
    console.log("=== Tuple Types ===");

    // Basic tuple
    let tuple: [string, number];
    tuple = ["hello", 42];
    console.log("tuple=" + tuple[0] + "," + tuple[1]);

    // Destructuring tuple
    const [name, age] = ["John", 30];
    console.log("name=" + name);
    console.log("age=" + age);

    // Tuple with more elements
    let triple: [string, number, boolean];
    triple = ["test", 123, true];
    console.log("triple=" + triple.join(","));

    // Tuple array
    const tuples: [string, number][] = [
        ["a", 1],
        ["b", 2],
        ["c", 3]
    ];
    tuples.forEach((t: [string, number]) => console.log("t=" + t[0] + ":" + t[1]));

    // Tuple as return type
    function getNameAndAge(): [string, number] {
        return ["Jane", 25];
    }
    const [personName, personAge] = getNameAndAge();
    console.log("person=" + personName + "," + personAge);

    // Tuple with optional elements
    type OptionalTuple = [string, number?];
    const opt1: OptionalTuple = ["only"];
    const opt2: OptionalTuple = ["both", 42];
    console.log("opt1=" + opt1);
    console.log("opt2=" + opt2);

    // Tuple with rest elements
    type RestTuple = [string, ...number[]];
    const rest1: RestTuple = ["nums", 1, 2, 3];
    console.log("rest=" + rest1.join(","));

    // Readonly tuple
    const readonlyTuple: readonly [string, number] = ["fixed", 100];
    console.log("readonly=" + readonlyTuple[0] + "," + readonlyTuple[1]);

    console.log("=== Tuple Types Complete ===");
}

runTests();