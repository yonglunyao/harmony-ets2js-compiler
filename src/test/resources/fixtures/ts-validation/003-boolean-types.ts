// 003-boolean-types.ts
// Test boolean types and operations
function runTests() {
    console.log("=== Boolean Types ===");

    // True
    const boolTrue: boolean = true;
    console.log("true=" + boolTrue);

    // False
    const boolFalse: boolean = false;
    console.log("false=" + boolFalse);

    // Boolean from expression
    const isEqual: boolean = 10 === 10;
    console.log("isEqual=" + isEqual);

    const isNotEqual: boolean = 5 !== 3;
    console.log("notEqual=" + isNotEqual);

    // Greater than
    const greater: boolean = 15 > 10;
    console.log("greater=" + greater);

    // Less than
    const less: boolean = 5 < 8;
    console.log("less=" + less);

    // Greater or equal
    const greaterEqual: boolean = 10 >= 10;
    console.log("greaterEqual=" + greaterEqual);

    // Less or equal
    const lessEqual: boolean = 5 <= 7;
    console.log("lessEqual=" + lessEqual);

    // Logical AND
    const andResult: boolean = true && false;
    console.log("and=" + andResult);

    // Logical OR
    const orResult: boolean = true || false;
    console.log("or=" + orResult);

    // Logical NOT
    const notResult: boolean = !true;
    console.log("not=" + notResult);

    // Boolean constructor
    const boolObj: boolean = Boolean(1);
    console.log("boolObj=" + boolObj);

    // Double negation
    const value: number = 5;
    const isTruthy: boolean = !!value;
    console.log("isTruthy=" + isTruthy);
}

runTests();