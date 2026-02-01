// 008-union-types.ts
// Test union types
function runTests() {
    console.log("=== Union Types ===");

    // String or number
    let value: string | number;
    value = "hello";
    console.log("value=" + value);

    value = 42;
    console.log("value=" + value);

    // Multiple types union
    let multi: string | number | boolean;
    multi = true;
    console.log("multi=" + multi);

    // Union with null
    let nullable: string | null;
    nullable = "test";
    console.log("nullable=" + nullable);

    nullable = null;
    console.log("nullable=" + nullable);

    // Union type parameter
    function display(input: string | number): void {
        console.log("input=" + input);
    }
    display("text");
    display(123);

    // Type narrowing with union
    function process(value: string | number): string {
        if (typeof value === "string") {
            return value.toUpperCase();
        } else {
            return (value * 2).toString();
        }
    }
    console.log("processStr=" + process("hello"));
    console.log("processNum=" + process(5));

    // Array of union types
    const mixed: (string | number)[] = [1, "two", 3, "four"];
    console.log("mixed=" + JSON.stringify(mixed));

    // Union with literal types
    let alignment: "left" | "center" | "right" = "center";
    console.log("align=" + alignment);

    // Union return type
    function getValue(useString: boolean): string | number {
        return useString ? "string" : 42;
    }
    console.log("str=" + getValue(true));
    console.log("num=" + getValue(false));

    console.log("=== Union Types Complete ===");
}

runTests();