// 066-assignment-operators.ts
// Test assignment operators
function runTests() {
    console.log("=== Assignment Operators ===");

    // Simple assignment
    let a = 10;
    console.log("assign=" + a);

    // Addition assignment
    let b = 5;
    b += 3;
    console.log("addAssign=" + b);

    // Subtraction assignment
    let c = 10;
    c -= 3;
    console.log("subAssign=" + c);

    // Multiplication assignment
    let d = 5;
    d *= 2;
    console.log("mulAssign=" + d);

    // Division assignment
    let e = 20;
    e /= 4;
    console.log("divAssign=" + e);

    // Remainder assignment
    let f = 10;
    f %= 3;
    console.log("remAssign=" + f);

    // Exponentiation assignment
    let g = 2;
    g **= 3;
    console.log("powAssign=" + g);

    // Left shift assignment
    let h = 8;
    h <<= 1;
    console.log("leftAssign=" + h);

    // Right shift assignment
    let i = 8;
    i >>= 1;
    console.log("rightAssign=" + i);

    // Unsigned right shift assignment
    let j = -8;
    j >>>= 1;
    console.log("unsignedAssign=" + j);

    // Bitwise AND assignment
    let k = 15;
    k &= 7;
    console.log("andAssign=" + k);

    // Bitwise OR assignment
    let l = 5;
    l |= 3;
    console.log("orAssign=" + l);

    // Bitwise XOR assignment
    let m = 5;
    m ^= 3;
    console.log("xorAssign=" + m);

    // Logical AND assignment
    let n: number | null = null;
    n &&= 5;
    console.log("andLogicAssign=" + n);

    n = 10;
    n &&= 5;
    console.log("andLogicAssign2=" + n);

    // Logical OR assignment
    let o: number | null = null;
    o ||= 5;
    console.log("orLogicAssign=" + o);

    // Logical nullish assignment
    let p: number | null = null;
    p ??= 5;
    console.log("nullishAssign=" + p);

    p = 10;
    p ??= 5;
    console.log("nullishAssign2=" + p);

    // Chaining assignment
    let x, y, z;
    x = y = z = 5;
    console.log("chain=" + x + "," + y + "," + z);

    // Destructuring assignment
    const [first, second] = [1, 2];
    console.log("destruct=" + first + "," + second);

    const { name, age } = { name: "John", age: 30 };
    console.log("objDestruct=" + name + "," + age);

    console.log("=== Assignment Operators Complete ===");
}

runTests();