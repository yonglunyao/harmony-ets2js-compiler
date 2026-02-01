// 001-basic-number-types.ts
// Test basic number types and operations
function runTests() {
    console.log("=== Basic Number Types ===");

    // Integer
    const intNum: number = 42;
    console.log("int=" + intNum);

    // Float
    const floatNum: number = 3.14;
    console.log("float=" + floatNum);

    // Negative
    const negNum: number = -10;
    console.log("negative=" + negNum);

    // Hexadecimal
    const hexNum: number = 0xFF;
    console.log("hex=" + hexNum);

    // Binary
    const binNum: number = 0b1010;
    console.log("binary=" + binNum);

    // Octal
    const octNum: number = 0o755;
    console.log("octal=" + octNum);

    // Exponential
    const expNum: number = 1.5e3;
    console.log("exponential=" + expNum);

    // Infinity
    const infNum: number = Infinity;
    console.log("infinity=" + infNum);

    // NaN
    const nanNum: number = NaN;
    console.log("nan=" + nanNum);

    // Arithmetic operations
    const sum: number = 10 + 20;
    console.log("sum=" + sum);

    const diff: number = 50 - 15;
    console.log("difference=" + diff);

    const product: number = 6 * 7;
    console.log("product=" + product);

    const quotient: number = 100 / 4;
    console.log("quotient=" + quotient);

    const remainder: number = 17 % 5;
    console.log("remainder=" + remainder);
}

runTests();
