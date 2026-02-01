// 006-any-unknown-types.ts
// Test any and unknown types
function runTests() {
    console.log("=== Any and Unknown Types ===");

    // Any type
    let anyValue: any = 42;
    console.log("anyNum=" + anyValue);

    anyValue = "string";
    console.log("anyStr=" + anyValue);

    anyValue = true;
    console.log("anyBool=" + anyValue);

    anyValue = { key: "value" };
    console.log("anyObj=" + JSON.stringify(anyValue));

    // Unknown type
    let unknownValue: unknown = 42;
    console.log("unknownNum=" + unknownValue);

    // Type guard for unknown
    function processUnknown(value: unknown): void {
        if (typeof value === "string") {
            console.log("string=" + value);
        } else if (typeof value === "number") {
            console.log("number=" + value);
        } else {
            console.log("other");
        }
    }

    processUnknown("hello");
    processUnknown(123);
    processUnknown(true);

    // Any array
    const anyArray: any[] = [1, "two", true, { four: 4 }];
    console.log("anyArray=" + JSON.stringify(anyArray));

    // Unknown with type narrowing
    function isString(value: unknown): value is string {
        return typeof value === "string";
    }

    const val: unknown = "test";
    if (isString(val)) {
        console.log("isString=" + val);
    }

    // Any function
    const anyFn: any = () => "result";
    console.log("anyFn=" + anyFn());

    // Practical any usage (JSON parse)
    const jsonStr: string = '{"name":"test","value":123}';
    const parsed: any = JSON.parse(jsonStr);
    console.log("parsed=" + JSON.stringify(parsed));

    console.log("=== Any/Unknown Complete ===");
}

runTests();