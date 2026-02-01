// 021-object-basics.ts
// Test object basics
function runTests() {
    console.log("=== Object Basics ===");

    // Object literal
    const person: { name: string; age: number } = {
        name: "John",
        age: 30
    };
    console.log("name=" + person.name);
    console.log("age=" + person.age);

    // Access properties
    console.log("dot=" + person.name);
    console.log("bracket=" + person["age"]);

    // Dynamic property access
    const key: string = "name";
    console.log("dynamic=" + person[key]);

    // Add properties
    const obj: any = { a: 1 };
    obj.b = 2;
    console.log("added=" + JSON.stringify(obj));

    // Delete properties
    const obj2: any = { a: 1, b: 2, c: 3 };
    delete obj2.b;
    console.log("deleted=" + JSON.stringify(obj2));

    // Check property existence
    const hasA: boolean = "a" in obj2;
    console.log("hasA=" + hasA);

    const hasB: boolean = "b" in obj2;
    console.log("hasB=" + hasB);

    // Object keys
    const keys: string[] = Object.keys(person);
    console.log("keys=" + keys.join(","));

    // Object values
    const values: any[] = Object.values(person);
    console.log("values=" + values.join(","));

    // Object entries
    const entries: [string, any][] = Object.entries(person);
    entries.forEach(([k, v]: [string, any]) => console.log(k + "=" + v));

    console.log("=== Object Basics Complete ===");
}

runTests();