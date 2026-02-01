// 063-bitwise-operators.ts
// Test bitwise operators
function runTests() {
    console.log("=== Bitwise Operators ===");

    // Bitwise AND
    const and = 5 & 3;  // 0101 & 0011 = 0001
    console.log("and=" + and);

    // Bitwise OR
    const or = 5 | 3;  // 0101 | 0011 = 0111
    console.log("or=" + or);

    // Bitwise XOR
    const xor = 5 ^ 3;  // 0101 ^ 0011 = 0110
    console.log("xor=" + xor);

    // Bitwise NOT
    const not = ~5;  // ~0101 = 1010 (two's complement)
    console.log("not=" + not);

    // Left shift
    const left = 5 << 1;  // 0101 << 1 = 1010
    console.log("left=" + left);

    // Right shift (sign-propagating)
    const right = 10 >> 1;  // 1010 >> 1 = 0101
    console.log("right=" + right);

    // Unsigned right shift
    const unsigned = -10 >>> 1;
    console.log("unsigned=" + unsigned);

    // Bit operations demonstration
    const num = 13;  // 1101
    console.log("binary:");
    console.log("  num=" + num);
    console.log("  &10=" + (num & 10));
    console.log("  |10=" + (num | 10));
    console.log("  ^10=" + (num ^ 10));

    // Shift operations
    console.log("shifts:");
    console.log("  <<1=" + (8 << 1));
    console.log("  <<2=" + (8 << 2));
    console.log("  >>1=" + (8 >> 1));
    console.log("  >>2=" + (8 >> 2));

    // Bitwise for flags
    const READ = 1;    // 0001
    const WRITE = 2;   // 0010
    const EXECUTE = 4; // 0100

    const perms = READ | WRITE;
    const canRead = (perms & READ) !== 0;
    const canWrite = (perms & WRITE) !== 0;
    const canExecute = (perms & EXECUTE) !== 0;
    console.log("flags:");
    console.log("  read=" + canRead + " write=" + canWrite + " exec=" + canExecute);

    // Toggle bit
    let value = 5;  // 0101
    value ^= 1;     // Toggle bit 0
    console.log("toggle=" + value);

    // Set bit
    let bits = 8;   // 1000
    bits |= 4;      // Set bit 2: 1100
    console.log("set=" + bits);

    // Clear bit
    let clear = 12; // 1100
    clear &= ~4;    // Clear bit 2: 1000
    console.log("clear=" + clear);

    console.log("=== Bitwise Operators Complete ===");
}

runTests();