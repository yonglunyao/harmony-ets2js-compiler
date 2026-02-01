// 029-object-property-descriptors.ts
// Test object property descriptors
function runTests() {
    console.log("=== Object Property Descriptors ===");

    // Define property with descriptor
    const obj = {};
    Object.defineProperty(obj, "name", {
        value: "John",
        writable: true,
        enumerable: true,
        configurable: true
    });
    console.log("name=" + obj.name);

    // Read-only property
    Object.defineProperty(obj, "readonly", {
        value: 42,
        writable: false
    });
    console.log("readonly=" + obj.readonly);

    // Property with getter
    let _value = 0;
    Object.defineProperty(obj, "dynamic", {
        get: () => _value,
        set: (val) => { _value = val; },
        enumerable: true
    });
    obj.dynamic = 10;
    console.log("dynamic=" + obj.dynamic);

    // Non-enumerable property
    Object.defineProperty(obj, "hidden", {
        value: "secret",
        enumerable: false
    });
    console.log("keys=" + Object.keys(obj).join(","));
    console.log("hasHidden=" + ("hidden" in obj));

    // Get descriptor
    const desc = Object.getOwnPropertyDescriptor(obj, "name");
    console.log("desc=" + JSON.stringify(desc));

    // Get all descriptors
    const allDescs = Object.getOwnPropertyDescriptors(obj);
    console.log("allDescs=" + Object.keys(allDescs).join(","));

    // Define multiple properties
    const newObj = {};
    Object.defineProperties(newObj, {
        a: { value: 1, writable: true },
        b: { value: 2, writable: true }
    });
    console.log("multi=" + newObj.a + "," + newObj.b);

    console.log("=== Property Descriptors Complete ===");
}

runTests();
