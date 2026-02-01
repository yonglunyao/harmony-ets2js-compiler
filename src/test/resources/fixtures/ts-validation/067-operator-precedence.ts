// 067-operator-precedence.ts
// Test operator precedence
function runTests() {
    console.log("=== Operator Precedence ===");

    // Multiplication before addition
    const expr1 = 2 + 3 * 4;
    console.log("mulAdd=" + expr1);

    // Parentheses override
    const expr2 = (2 + 3) * 4;
    console.log("paren=" + expr2);

    // Comparison before logical
    const expr3 = 5 > 3 && 10 < 20;
    console.log("compLog=" + expr3);

    // Equality before AND
    const expr4 = 5 == 5 && 10 == 10;
    console.log("eqAnd=" + expr4);

    // Bitwise before comparison
    const expr5 = (5 & 3) < 5;
    console.log("bitComp=" + expr5);

    // Unary before multiplicative
    const expr6 = -2 * 3;
    console.log("unaryMul=" + expr6);

    // Exponentiation before unary
    const expr7 = -2 ** 2;
    const expr8 = -(2 ** 2);
    console.log("exp=" + expr7 + "," + expr8);

    // Logical OR after AND
    const expr9 = true && false || true;
    console.log("andOr=" + expr9);

    // Ternary before assignment
    let result = 5 > 3 ? 10 : 20;
    console.log("ternAssign=" + result);

    // Complex expression
    const expr10 = 2 + 3 * 4 ** 2 - 5 / 2;
    console.log("complex=" + expr10);

    // Equality vs strict equality
    const expr11 = 5 == 5 === true;
    console.log("eqEq=" + expr11);

    // Member access before call
    const obj = { value: 42 };
    const val = obj.value.toString();
    console.log("member=" + val);

    // Array index before call
    const arr = [1, 2, 3];
    const item = arr[0].toString();
    console.log("index=" + item);

    // New before member access
    class Test {
        constructor() {
            this.value = 5;
        }
    }
    const test = new Test().value;
    console.log("newMember=" + test);

    // Void in expression
    const expr12 = (void 0) || 5;
    console.log("void=" + expr12);

    // Comma operator
    const expr13 = (1, 2, 3);
    console.log("comma=" + expr13);

    console.log("=== Operator Precedence Complete ===");
}

runTests();
