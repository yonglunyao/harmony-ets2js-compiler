// 095-advanced-patterns.ts
// Test advanced TypeScript patterns (runtime behavior)
function runTests() {
    console.log("=== Advanced Patterns ===");

    // Branded types (runtime simulation - just regular values)
    const userId = "user123";
    const accountId = "acc456";

    console.log("userId=" + userId);
    console.log("accountId=" + accountId);

    // Opaque types (runtime simulation)
    function money(amount) {
        return amount;
    }

    function addMoney(a, b) {
        return money(a + b);
    }

    const total = addMoney(money(10), money(20));
    console.log("money=" + total);

    // Type-state pattern (runtime simulation)
    const state1 = { status: "idle" };
    const state2 = { status: "loading", startTime: Date.now() };
    const state3 = { status: "success", data: "result" };

    console.log("state1=" + state1.status);
    console.log("state2=" + state2.status);
    console.log("state3=" + state3.status);

    // Function overload simulation (runtime simulation)
    const fn = (x) => x;
    console.log("overloadStr=" + fn("test"));
    console.log("overloadNum=" + fn(42));

    // Currying types (runtime simulation)
    function add(a, b) {
        return a + b;
    }

    const curriedAdd = (a) => (b) => a + b;

    console.log("curry=" + curriedAdd(5)(3));

    // Nominal typing simulation (runtime simulation)
    function celsius(n) {
        return { value: n };
    }

    function fahrenheit(n) {
        return { value: n };
    }

    const tempC = celsius(25);
    const tempF = fahrenheit(77);

    console.log("celsius=" + tempC.value);
    console.log("fahrenheit=" + tempF.value);

    console.log("=== Advanced Patterns Complete ===");
}

runTests();
