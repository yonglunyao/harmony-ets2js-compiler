// 030-object-advanced.ts
// Test advanced object features
function runTests() {
    console.log("=== Object Advanced ===");

    // Object.is
    console.log("isSame=" + Object.is("foo", "foo"));
    console.log("isDiff=" + Object.is("foo", "bar"));
    console.log("isNegZero=" + Object.is(-0, -0));
    console.log("isPosZero=" + Object.is(0, -0));
    console.log("isNaNSame=" + Object.is(NaN, NaN));

    // Object comparison
    const obj1 = { a: 1 };
    const obj2 = { a: 1 };
    const obj3 = obj1;
    console.log("sameRef=" + (obj1 === obj3));
    console.log("diffRef=" + (obj1 === obj2));

    // Shallow equality
    function shallowEqual(a: any, b: any): boolean {
        const keysA = Object.keys(a);
        const keysB = Object.keys(b);
        if (keysA.length !== keysB.length) return false;
        return keysA.every((key) => a[key] === b[key]);
    }
    console.log("shallow=" + shallowEqual(obj1, obj2));

    // Deep clone simple
    function deepClone(obj: any): any {
        return JSON.parse(JSON.stringify(obj));
    }
    const original = { a: 1, b: { c: 2 } };
    const cloned = deepClone(original);
    cloned.b.c = 3;
    console.log("orig=" + original.b.c);
    console.log("clone=" + cloned.b.c);

    // Object.fromEntries
    const entries = [["a", 1], ["b", 2], ["c", 3]];
    const fromEntries = Object.fromEntries(entries);
    console.log("fromEntries=" + JSON.stringify(fromEntries));

    // Object grouping (primitive implementation)
    const people = [
        { name: "Alice", group: "A" },
        { name: "Bob", group: "B" },
        { name: "Charlie", group: "A" }
    ];
    const grouped: { [key: string]: any[] } = {};
    people.forEach((p: any) => {
        (grouped[p.group] = grouped[p.group] || []).push(p);
    });
    console.log("grouped=" + Object.keys(grouped).join(","));

    // Dynamic property access
    const key = "dynamic";
    const dynamicObj: any = {};
    dynamicObj[key] = "value";
    console.log("dynamic=" + dynamicObj.dynamic);

    // Computed property
    const computedKey = "computed";
    const computedObj = {
        [computedKey]: "value",
        ["prefix_" + "suffix"]: "combined"
    };
    console.log("computed=" + computedObj.computed);
    console.log("combined=" + computedObj.prefix_suffix);

    console.log("=== Object Advanced Complete ===");
}

runTests();