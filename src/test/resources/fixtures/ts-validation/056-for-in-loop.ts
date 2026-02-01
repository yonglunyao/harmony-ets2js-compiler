// 056-for-in-loop.ts
// Test for-in loops
function runTests() {
    console.log("=== For-In Loops ===");

    // For-in with object
    const obj = { a: 1, b: 2, c: 3 };
    console.log("object:");
    for (const key in obj) {
        console.log("  key=" + key + " val=" + obj[key]);
    }

    // For-in with array (indexes)
    const arr = [10, 20, 30, 40];
    console.log("array:");
    for (const index in arr) {
        console.log("  index=" + index + " val=" + arr[index]);
    }

    // For-in with string
    const str = "Hello";
    console.log("string:");
    for (const index in str) {
        console.log("  [" + index + "]=" + str[index]);
    }

    // For-in with nested object
    const nested = {
        person: {
            name: "John",
            age: 30
        },
        address: {
            city: "NYC",
            zip: "10001"
        }
    };
    console.log("nested:");
    for (const key in nested) {
        console.log("  key=" + key);
    }

    // For-in with hasOwnProperty check
    const base = { a: 1 };
    const derived = Object.create(base);
    derived.b = 2;
    derived.c = 3;
    console.log("ownProps:");
    for (const key in derived) {
        if (derived.hasOwnProperty(key)) {
            console.log("  own=" + key);
        }
    }

    // For-in with Object.keys
    const data = { x: 10, y: 20, z: 30 };
    console.log("keys:");
    for (const key of Object.keys(data)) {
        console.log("  " + key + "=" + data[key]);
    }

    // For-in to get values
    const values = { first: 1, second: 2, third: 3 };
    console.log("values:");
    for (const key in values) {
        console.log("  val=" + values[key]);
    }

    // For-in with dynamic property access
    const config = { host: "localhost", port: 8080 };
    console.log("config:");
    for (const key in config) {
        console.log("  " + key + "=" + config[key]);
    }

    // For-in with number keys
    const numObj: { [key: string]: number } = {};
    numObj["1"] = 100;
    numObj["2"] = 200;
    numObj["3"] = 300;
    console.log("numKeys:");
    for (const key in numObj) {
        console.log("  key=" + key + " val=" + numObj[key]);
    }

    console.log("=== For-In Loops Complete ===");
}

runTests();