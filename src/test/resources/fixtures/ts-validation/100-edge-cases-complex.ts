// 100-edge-cases-complex.ts
// Test edge cases and complex scenarios
function runTests() {
    console.log("=== Edge Cases and Complex Scenarios ===");

    // Deep equality check
    function deepEqual(a, b) {
        if (a === b) return true;
        if (typeof a !== typeof b) return false;
        if (typeof a !== "object" || a === null || b === null) return false;
        const keysA = Object.keys(a);
        const keysB = Object.keys(b);
        if (keysA.length !== keysB.length) return false;
        return keysA.every((key) => deepEqual(a[key], b[key]));
    }

    const obj1 = { a: 1, b: { c: 2 } };
    const obj2 = { a: 1, b: { c: 2 } };
    const obj3 = { a: 1, b: { c: 3 } };
    console.log("deepEqual=" + deepEqual(obj1, obj2));
    console.log("deepNotEqual=" + deepEqual(obj1, obj3));

    // Circular reference handling
    function createCircular() {
        const obj = { name: "circular" };
        obj.self = obj;
        return obj;
    }

    const circular = createCircular();
    console.log("circular=" + circular.self.self.name);

    // Infinity and NaN edge cases
    console.log("inf=" + Infinity);
    console.log("negInf=" + -Infinity);
    console.log("nan=" + NaN);
    console.log("isNaN=" + isNaN(NaN));
    console.log("nanEquality=" + (NaN === NaN));

    // Max/Min safe integer
    console.log("maxSafe=" + Number.MAX_SAFE_INTEGER);
    console.log("minSafe=" + Number.MIN_SAFE_INTEGER);
    console.log("epsilon=" + Number.EPSILON);

    // Floating point precision
    console.log("float1=" + (0.1 + 0.2));
    console.log("float2=" + (0.1 + 0.2 === 0.3));

    // Array edge cases
    const sparse = [];
    sparse[5] = "value";
    console.log("sparseLen=" + sparse.length);
    console.log("sparse0=" + sparse[0]);

    const veryLarge = new Array(10).fill(0);
    console.log("large=" + veryLarge.length);

    // String edge cases
    const empty = "";
    console.log("emptyLen=" + empty.length);
    console.log("empty=" + (empty === ""));

    // Unicode handling
    const unicode = "hello \u{1F600}";
    console.log("unicode=" + unicode);

    // Mixed type array
    const mixed = [1, "two", true, null, undefined];
    console.log("mixed=" + JSON.stringify(mixed));

    // Complex object access
    const deep = { a: { b: { c: { d: "value" } } } };
    console.log("deepAccess=" + deep?.a?.b?.c?.d);
    console.log("deepMissing=" + deep?.a?.b?.x?.d);

    // Prototype chain edge case
    const base = { inherited: "base" };
    const derived = Object.create(base);
    derived.own = "derived";
    console.log("inherited=" + derived.inherited);
    console.log("own=" + derived.own);
    console.log("hasOwnProperty=" + derived.hasOwnProperty("inherited"));

    // This binding edge cases
    function showThis() {
        console.log("this=" + this.value);
    }

    const obj = { value: "obj", method: showThis };
    obj.method();

    const bound = showThis.bind({ value: "bound" });
    bound();

    // Arrow function this
    const arrowObj = {
        value: "arrow",
        method: () => console.log("arrowThis=" + undefined)
    };
    console.log("arrowGlobal=" + arrowObj.method());

    // Coercion edge cases
    console.log("strPlusNum=" + ("5" + 3));
    console.log("numPlusStr=" + (5 + "3"));
    console.log("strMinusNum=" + ("5" - 3));

    // Falsy values
    const falsy = [false, 0, "", null, undefined, NaN];
    console.log("falsy=" + falsy.map(f => Boolean(f)).join(","));

    // Empty array/object destructuring
    const {} = {};
    const [] = [];
    console.log("emptyDestruct=ok");

    // Delete edge cases
    const deletable = { a: 1, b: 2 };
    console.log("deleteResult=" + delete deletable.a);
    console.log("deleteUndefined=" + delete deletable.c);

    // Type conversion edge cases
    console.log("toNumStr=" + Number("123"));
    console.log("toNumStrInvalid=" + Number("abc"));
    console.log("toStrNum=" + String(123));
    console.log("toBool=" + Boolean(1));

    // Date edge cases
    const invalidDate = new Date("invalid");
    console.log("invalidDate=" + invalidDate.toString());
    console.log("isNaN=" + isNaN(invalidDate.getTime()));

    console.log("=== Edge Cases and Complex Scenarios Complete ===");
}

runTests();
