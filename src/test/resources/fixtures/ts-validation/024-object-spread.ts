// 024-object-spread.ts
// Test object spread
function runTests() {
    console.log("=== Object Spread ===");

    // Basic spread
    const obj1 = { a: 1, b: 2 };
    const obj2 = { c: 3, d: 4 };
    const merged = { ...obj1, ...obj2 };
    console.log("merged=" + JSON.stringify(merged));

    // Spread with override
    const base = { x: 1, y: 2 };
    const override = { ...base, y: 20, z: 3 };
    console.log("override=" + JSON.stringify(override));

    // Spread for cloning
    const original = { name: "John", age: 30 };
    const clone = { ...original };
    console.log("clone=" + JSON.stringify(clone));

    // Spread with new properties
    const withNew = { ...original, city: "NYC" };
    console.log("withNew=" + JSON.stringify(withNew));

    // Spread with conditional properties
    const includeEmail = true;
    const partial = { ...original, ...(includeEmail && { email: "john@example.com" }) };
    console.log("conditional=" + JSON.stringify(partial));

    // Spread in array of objects
    const items = [{ id: 1 }, { id: 2 }];
    const withDefaults = items.map((item: { id: number }) => ({ ...item, active: true }));
    console.log("withDefaults=" + JSON.stringify(withDefaults));

    // Nested spread
    const nested = { a: { b: 1 } };
    const nestedSpread = { ...nested, a: { ...nested.a, c: 2 } };
    console.log("nested=" + JSON.stringify(nestedSpread));

    // Spread with computed properties
    const key = "dynamic";
    const computed = { ...{ [key]: "value" } };
    console.log("computed=" + JSON.stringify(computed));

    console.log("=== Object Spread Complete ===");
}

runTests();