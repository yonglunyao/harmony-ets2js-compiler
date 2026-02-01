// 028-object-nullish-coalescing.ts
// Test nullish coalescing
function runTests() {
    console.log("=== Nullish Coalescing ===");

    // Basic nullish coalescing
    const value1: string | null = null;
    const result1: string = value1 ?? "default";
    console.log("null=" + result1);

    // With undefined
    const value2: string | undefined = undefined;
    const result2: string = value2 ?? "default";
    console.log("undefined=" + result2);

    // With valid value
    const value3: string | null = "actual";
    const result3: string = value3 ?? "default";
    console.log("value=" + result3);

    // Chained nullish coalescing
    const value4: string | null | undefined = null;
    const result4: string = value4 ?? value1 ?? "fallback";
    console.log("chained=" + result4);

    // With numbers
    const count: number | null = null;
    const total: number = count ?? 0;
    console.log("num=" + total);

    // With objects
    const config: { setting?: string } | null = null;
    const setting: string = config?.setting ?? "default";
    console.log("obj=" + setting);

    // With falsy values
    const zero: number | null = 0;
    const zeroResult: number = zero ?? 10;
    console.log("zero=" + zeroResult);

    const emptyStr: string | null = "";
    const emptyResult: string = emptyStr ?? "default";
    console.log("empty=" + emptyResult);

    const falseVal: boolean | null = false;
    const falseResult: boolean = falseVal ?? true;
    console.log("false=" + falseResult);

    // Combined with optional chaining
    const data: any = { items: null };
    const items: any[] = data?.items ?? [];
    console.log("items=" + items.length);

    // Practical example
    function getLength(str: string | null | undefined): number {
        return str?.length ?? 0;
    }
    console.log("len=" + getLength("hello"));
    console.log("nullLen=" + getLength(null));

    console.log("=== Nullish Coalescing Complete ===");
}

runTests();