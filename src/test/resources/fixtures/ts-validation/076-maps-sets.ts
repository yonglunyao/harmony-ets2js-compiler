// 076-maps-sets.ts
// Test Map and Set
function runTests() {
    console.log("=== Map and Set ===");

    // Map basics
    const map = new Map<string, number>();
    map.set("a", 1);
    map.set("b", 2);
    map.set("c", 3);
    console.log("map=" + map.get("a"));
    console.log("hasB=" + map.has("b"));
    console.log("size=" + map.size);

    // Map with various key types
    const mixedKeyMap = new Map<any, string>();
    mixedKeyMap.set("string", "strKey");
    mixedKeyMap.set(123, "numKey");
    mixedKeyMap.set(true, "boolKey");
    mixedKeyMap.set({ id: 1 }, "objKey");

    console.log("mixed=" + mixedKeyMap.get(123));

    // Map from array
    const fromArr = new Map([["x", 1], ["y", 2]]);
    console.log("fromArr=" + fromArr.get("x"));

    // Map iteration
    console.log("mapIter:");
    for (const [key, value] of map) {
        console.log("  " + key + "=" + value);
    }

    // Map keys and values
    console.log("keys=" + [...map.keys()].join(","));
    console.log("values=" + [...map.values()].join(","));

    // Map entries
    for (const [key, value] of map.entries()) {
        console.log("entry=" + key + "=" + value);
    }

    // Map forEach
    map.forEach((value, key) => {
        console.log("forEach=" + key + "=" + value);
    });

    // Set basics
    const set = new Set<number>();
    set.add(1);
    set.add(2);
    set.add(3);
    set.add(2); // duplicate ignored
    console.log("set=" + [...set].join(","));
    console.log("has2=" + set.has(2));
    console.log("setSize=" + set.size);

    // Set from array
    const fromArray = new Set([1, 2, 2, 3, 3, 3]);
    console.log("unique=" + [...fromArray].join(","));

    // Set operations
    const setA = new Set([1, 2, 3]);
    const setB = new Set([3, 4, 5]);

    // Union
    const union = new Set([...setA, ...setB]);
    console.log("union=" + [...union].join(","));

    // Intersection
    const intersection = new Set([...setA].filter(x => setB.has(x)));
    console.log("intersect=" + [...intersection].join(","));

    // Difference
    const difference = new Set([...setA].filter(x => !setB.has(x)));
    console.log("diff=" + [...difference].join(","));

    // WeakMap
    const weakMap = new WeakMap<object, string>();
    const key1 = { id: 1 };
    const key2 = { id: 2 };
    weakMap.set(key1, "value1");
    weakMap.set(key2, "value2");
    console.log("weak=" + weakMap.get(key1));

    // WeakSet
    const weakSet = new WeakSet<object>();
    const obj1 = { a: 1 };
    const obj2 = { b: 2 };
    weakSet.add(obj1);
    weakSet.add(obj2);
    console.log("weakHas=" + weakSet.has(obj1));

    console.log("=== Map and Set Complete ===");
}

runTests();