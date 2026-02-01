// 070-destructuring-assignment.ts
// Test destructuring assignment
function runTests() {
    console.log("=== Destructuring Assignment ===");

    // Array destructuring
    const arr = [1, 2, 3, 4, 5];
    const [first, second, third] = arr;
    console.log("first=" + first + " second=" + second + " third=" + third);

    // Skipping elements
    const [a, , b] = arr;
    console.log("skip=" + a + " " + b);

    // Rest in array destructuring
    const [head, ...tail] = arr;
    console.log("head=" + head + " tail=" + tail.join(","));

    // Default values
    const [x = 10, y = 20, z = 30] = [1];
    console.log("default=" + x + " " + y + " " + z);

    // Swapping variables
    let p = 1, q = 2;
    [p, q] = [q, p];
    console.log("swap=" + p + " " + q);

    // Object destructuring
    const person = { name: "John", age: 30, city: "NYC" };
    const { name, age } = person;
    console.log("name=" + name + " age=" + age);

    // Renaming properties
    const { name: fullName, age: years } = person;
    console.log("full=" + fullName + " years=" + years);

    // Default values in objects
    const { country = "USA", city: town = "Unknown" } = person;
    console.log("country=" + country + " town=" + town);

    // Rest in object destructuring
    const { name: n, ...rest } = person;
    console.log("n=" + n + " rest=" + JSON.stringify(rest));

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
    console.log("user=" + userName + " city=" + city);

    // Destructuring in function parameters
    function greet({ name, age }: { name: string; age: number }): string {
        return name + " is " + age;
    }
    console.log("greet=" + greet({ name: "Bob", age: 25 }));

    // Array parameter destructuring
    function coordinates([x, y]: [number, number]): string {
        return x + "," + y;
    }
    console.log("coords=" + coordinates([10, 20]));

    // Destructuring return values
    function getTuple(): [string, number] {
        return ["result", 42];
    }
    const [result, value] = getTuple();
    console.log("tuple=" + result + " " + value);

    // For-of with destructuring
    const pairs = [["a", 1], ["b", 2], ["c", 3]];
    for (const [key, val] of pairs) {
        console.log("pair=" + key + "=" + val);
    }

    console.log("=== Destructuring Assignment Complete ===");
}

runTests();