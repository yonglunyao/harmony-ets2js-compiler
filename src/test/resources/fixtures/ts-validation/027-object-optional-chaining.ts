// 027-object-optional-chaining.ts
// Test object optional chaining
function runTests() {
    console.log("=== Object Optional Chaining ===");

    // Basic optional chaining
    const user = { name: "John", address: { city: "NYC" } };
    console.log("city=" + user?.address?.city);

    // Optional chaining with null
    const empty = null;
    console.log("null=" + (empty?.address ?? "none"));

    // Optional chaining with undefined
    const obj = { prop: undefined };
    console.log("undef=" + (obj?.prop ?? "default"));

    // Optional method call
    const listener = { onEvent: (msg) => console.log("event=" + msg) };
    listener?.onEvent("test");

    // Optional method with null
    const noListener = null;
    noListener?.onEvent("test");

    // Optional array access
    const arr = { items: [1, 2, 3] };
    console.log("item=" + arr?.items?.[0]);

    // Optional chaining with delete
    const data = { temp: { value: 1 } };
    delete data?.temp?.value;
    console.log("afterDelete=" + JSON.stringify(data));

    // Chained optional
    const deep = { a: { b: { c: { d: "value" } } } };
    console.log("deep=" + deep?.a?.b?.c?.d);

    // Optional chaining with expression
    const key = "name";
    const person = { name: "Jane" };
    console.log("expr=" + person?.[key]);

    // Multiple optional
    function getValue(obj) {
        return obj?.data?.value ?? "default";
    }
    console.log("func=" + getValue({ data: { value: "test" } }));
    console.log("nullFunc=" + getValue(null));

    console.log("=== Optional Chaining Complete ===");
}

runTests();
