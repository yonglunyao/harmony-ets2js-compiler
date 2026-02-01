// 089-template-literal-types.ts
// Test template literal types (runtime behavior without types)
function runTests() {
    console.log("=== Template Literal Types ===");

    // Basic template literal type (runtime simulation)
    const g1 = "hello world";
    const g2 = "hello there";
    console.log("greeting=" + g1 + "," + g2);

    // Template literal with union (runtime simulation)
    const size = "small-red";
    console.log("size=" + size);

    // Template literal for CSS (runtime simulation)
    const width = "100px";
    console.log("css=" + width);

    // Uppercase template literal (runtime simulation)
    const click = "onClick";
    console.log("event=" + click);

    // Lowercase template literal (runtime simulation)
    const lower = "id-test";
    const upper = "ID-TEST";
    console.log("case=" + lower + "," + upper);

    // Uncapitalize (runtime simulation)
    const uncap = "hello";

    // Template literal in generics (runtime simulation)
    const getName = "getName";
    const setName = "setName";
    console.log("accessor=" + getName + "," + setName);

    // String manipulation in types (runtime simulation)
    const t1 = "  hello";
    console.log("path=" + t1);

    // Template literal for key paths (runtime simulation)
    const path = "a";
    console.log("path=" + path);

    // Combine with mapped types (runtime simulation)
    const events = {
        onClick: (e) => console.log("click"),
        onHover: (e) => console.log("hover")
    };
    console.log("events=" + Object.keys(events).join(","));

    console.log("=== Template Literal Types Complete ===");
}

runTests();
