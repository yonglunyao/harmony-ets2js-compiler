// 064-comparison-operators.ts
// Test comparison operators
function runTests() {
    console.log("=== Comparison Operators ===");

    // Equal
    const eq1 = 5 == 5;
    const eq2 = 5 == "5";
    console.log("eq=" + eq1 + "," + eq2);

    // Not equal
    const ne1 = 5 != 3;
    const ne2 = 5 != "5";
    console.log("ne=" + ne1 + "," + ne2);

    // Strict equal
    const seq1 = 5 === 5;
    const seq2 = 5 === "5";
    console.log("seq=" + seq1 + "," + seq2);

    // Strict not equal
    const sne1 = 5 !== 3;
    const sne2 = 5 !== "5";
    console.log("sne=" + sne1 + "," + sne2);

    // Greater than
    const gt = 10 > 5;
    console.log("gt=" + gt);

    // Greater than or equal
    const gte = 10 >= 10;
    console.log("gte=" + gte);

    // Less than
    const lt = 5 < 10;
    console.log("lt=" + lt);

    // Less than or equal
    const lte = 5 <= 5;
    console.log("lte=" + lte);

    // String comparison
    const strGt = "b" > "a";
    const strLt = "a" < "b";
    console.log("str=" + strGt + "," + strLt);

    // Null and undefined
    const nullUndef = null == undefined;
    const nullUndefStrict = null === undefined;
    console.log("nullUndef=" + nullUndef + "," + nullUndefStrict);

    // NaN comparison
    const nan1 = NaN == NaN;
    const nan2 = isNaN(NaN);
    console.log("nan=" + nan1 + "," + nan2);

    // Object comparison (reference)
    const obj1 = { a: 1 };
    const obj2 = { a: 1 };
    const obj3 = obj1;
    console.log("objRef=" + (obj1 === obj3) + "," + (obj1 === obj2));

    // Array comparison
    const arr1 = [1, 2];
    const arr2 = [1, 2];
    console.log("arrRef=" + (arr1 === arr2));

    // Boolean comparison
    const boolEq = true == 1;
    const boolSeq = true === 1;
    console.log("bool=" + boolEq + "," + boolSeq);

    // Mixed type comparisons
    const mix1 = "5" < 6;
    const mix2 = null == 0;
    const mix3 = undefined == 0;
    console.log("mix=" + mix1 + "," + mix2 + "," + mix3);

    console.log("=== Comparison Operators Complete ===");
}

runTests();