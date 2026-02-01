// 061-arithmetic-operators.ts
// Test arithmetic operators
function runTests() {
    console.log("=== Arithmetic Operators ===");

    // Addition
    const sum = 10 + 5;
    console.log("add=" + sum);

    // Subtraction
    const diff = 10 - 5;
    console.log("sub=" + diff);

    // Multiplication
    const product = 10 * 5;
    console.log("mul=" + product);

    // Division
    const quotient = 10 / 5;
    console.log("div=" + quotient);

    // Remainder
    const remainder = 10 % 3;
    console.log("rem=" + remainder);

    // Exponentiation
    const power = 2 ** 8;
    console.log("pow=" + power);

    // Increment
    let inc = 5;
    inc++;
    console.log("inc=" + inc);

    // Decrement
    let dec = 5;
    dec--;
    console.log("dec=" + dec);

    // Prefix increment
    let preInc = 5;
    const preResult = ++preInc;
    console.log("preInc=" + preInc + " result=" + preResult);

    // Postfix increment
    let postInc = 5;
    const postResult = postInc++;
    console.log("postInc=" + postInc + " result=" + postResult);

    // Unary plus
    const uplus = +"5";
    console.log("uplus=" + uplus);

    // Unary minus
    const uminus = -5;
    console.log("uminus=" + uminus);

    // String concatenation with +
    const concat = "Hello" + " " + "World";
    console.log("concat=" + concat);

    // Number and string concatenation
    const mixed = "Value: " + 42;
    console.log("mixed=" + mixed);

    // Complex expressions
    const expr1 = (10 + 5) * 2;
    console.log("expr1=" + expr1);

    const expr2 = 10 + 5 * 2;
    console.log("expr2=" + expr2);

    const expr3 = (10 + 5) * (2 - 1);
    console.log("expr3=" + expr3);

    // Modulo with negatives
    const mod1 = -10 % 3;
    const mod2 = 10 % -3;
    const mod3 = -10 % -3;
    console.log("mods=" + mod1 + "," + mod2 + "," + mod3);

    console.log("=== Arithmetic Operators Complete ===");
}

runTests();