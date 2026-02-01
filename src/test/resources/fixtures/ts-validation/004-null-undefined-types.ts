// 004-null-undefined-types.ts
// Test null and undefined types
function runTests() {
    console.log("=== Null and Undefined Types ===");

    // Null
    const nullValue: null = null;
    console.log("null=" + nullValue);

    // Undefined variable
    let undefinedValue: undefined = undefined;
    console.log("undefined=" + undefinedValue);

    // Null check
    const maybeNull: string | null = null;
    const result1: string = maybeNull ?? "default";
    console.log("nullish=" + result1);

    // Undefined check
    let maybeUndefined: string | undefined;
    const result2: string = maybeUndefined ?? "default";
    console.log("undefCheck=" + result2);

    // typeof null
    const nullType: string = typeof null;
    console.log("typeOfNull=" + nullType);

    // typeof undefined
    const undefinedType: string = typeof undefined;
    console.log("typeOfUndef=" + undefinedType);

    // Null comparison
    const isNull: boolean = nullValue === null;
    console.log("isNull=" + isNull);

    // Undefined comparison
    const isUndef: boolean = undefinedValue === undefined;
    console.log("isUndef=" + isUndef);

    // Null or undefined check
    let value: string | null | undefined = "test";
    const hasValue: boolean = value != null;
    console.log("hasValue=" + hasValue);

    // Optional chaining with null/undefined
    const obj: { prop?: string } = {};
    const propValue: string | undefined = obj.prop;
    console.log("prop=" + (propValue ?? "none"));

    // Nullish coalescing
    const input: number | null = null;
    const count: number = input ?? 0;
    console.log("count=" + count);
}

runTests();