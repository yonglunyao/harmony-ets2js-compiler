// 044-function-destructuring.ts
// Test function destructuring
function runTests() {
    console.log("=== Function Destructuring ===");

    // Destructuring in parameters - object
    interface IUser {
        name: string;
        age: number;
        city?: string;
    }

    function printUser({ name, age }: IUser): void {
        console.log("user=" + name + "," + age);
    }
    printUser({ name: "John", age: 30 });

    // Destructuring with rename
    function greetUser({ name: userName, age: userAge }: IUser): string {
        return userName + " is " + userAge + " years old";
    }
    console.log("greet=" + greetUser({ name: "Jane", age: 25 }));

    // Destructuring with default
    function printLocation({ city = "Unknown" }: IUser): void {
        console.log("city=" + city);
    }
    printLocation({ name: "Bob", age: 35 });
    printLocation({ name: "Alice", age: 28, city: "NYC" });

    // Destructuring in parameters - array
    function getFirst([first, second]: number[]): string {
        return "first=" + first + " second=" + second;
    }
    console.log("first=" + getFirst([1, 2, 3]));

    // Nested destructuring
    interface IAddress {
        street: string;
        city: string;
    }

    interface IPerson {
        name: string;
        address: IAddress;
    }

    function printCity({ address: { city } }: IPerson): void {
        console.log("city=" + city);
    }
    printCity({ name: "John", address: { street: "Main St", city: "NYC" } });

    // Complex destructuring
    function extractInfo({
        name,
        address: { city, street }
    }: IPerson): string {
        return name + " lives at " + street + " in " + city;
    }
    console.log("extract=" + extractInfo({
        name: "Jane",
        address: { street: "Oak Ave", city: "LA" }
    }));

    // Destructuring with rest
    function printDetails({ name, ...rest }: IUser): void {
        console.log("name=" + name);
        console.log("rest=" + JSON.stringify(rest));
    }
    printDetails({ name: "Bob", age: 40, city: "Chicago" });

    // Multiple destructured parameters
    function mergeInfo(
        { name: n1 }: IUser,
        { name: n2 }: IUser
    ): string {
        return n1 + " and " + n2;
    }
    console.log("merge=" + mergeInfo(
        { name: "Alice", age: 30 },
        { name: "Bob", age: 35 }
    ));

    // Destructuring return values
    function getCoords(): [number, number] {
        return [10, 20];
    }
    const [x, y] = getCoords();
    console.log("coords=" + x + "," + y);

    function getPosition(): { x: number; y: number } {
        return { x: 5, y: 15 };
    }
    const { x: px, y: py } = getPosition();
    console.log("pos=" + px + "," + py);

    console.log("=== Function Destructuring Complete ===");
}

runTests();