// 023-object-destructuring.ts
// Test object destructuring
function runTests() {
    console.log("=== Object Destructuring ===");

    // Basic destructuring
    const person = { name: "John", age: 30, city: "NYC" };
    const { name, age } = person;
    console.log("name=" + name);
    console.log("age=" + age);

    // Destructuring with rename
    const { name: fullName, age: years } = person;
    console.log("fullName=" + fullName);
    console.log("years=" + years);

    // Destructuring with default
    const { country = "USA" } = person;
    console.log("country=" + country);

    // Nested destructuring
    const data = {
        user: {
            name: "Jane",
            address: {
                city: "LA",
                zip: "90210"
            }
        }
    };
    const { user: { name: userName, address: { city } } } = data;
    console.log("userName=" + userName);
    console.log("city=" + city);

    // Destructuring in function parameter
    function printPerson({ name, age }: { name: string; age: number }): void {
        console.log("print=" + name + "," + age);
    }
    printPerson({ name: "Bob", age: 25 });

    // Destructuring with rest
    const { name: n, ...rest } = person;
    console.log("n=" + n);
    console.log("rest=" + JSON.stringify(rest));

    // Array of objects destructuring
    const users = [
        { id: 1, name: "Alice" },
        { id: 2, name: "Bob" }
    ];
    users.forEach(({ id, name }: { id: number; name: string }) => {
        console.log("user=" + id + ":" + name);
    });

    console.log("=== Object Destructuring Complete ===");
}

runTests();