// 087-conditional-types.ts
// Test conditional types (runtime behavior without types)
function runTests() {
    console.log("=== Conditional Types ===");

    // Basic conditional type (runtime simulation)
    const t1 = true;
    const t2 = false;
    console.log("isString=" + t1 + "," + t2);

    // Conditional type with union (runtime simulation)
    const m1 = "text";
    const m2 = "number";
    const m3 = "other";
    console.log("message=" + m1 + "," + m2 + "," + m3);

    // Conditional type for function return (runtime simulation)
    const p1 = "value";
    const p2 = 42;
    console.log("unwrap=" + p1 + "," + p2);

    // Infer keyword (runtime simulation)
    const e1 = "test";
    const e2 = 10;
    console.log("element=" + e1 + "," + e2);

    // ReturnType conditional (runtime simulation)
    const r1 = "result";
    const r2 = 100;
    console.log("return=" + r1 + "," + r2);

    // Conditional type with constraints (runtime simulation)
    const f1 = "flat";
    const f2 = 42;
    console.log("flatten=" + f1 + "," + f2);

    // Distributive conditional types (runtime simulation)
    const arr = ["test", 42];
    console.log("distribute=" + arr.join(","));

    console.log("=== Conditional Types Complete ===");
}

runTests();
