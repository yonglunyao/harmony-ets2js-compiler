// 055-do-while-loop.ts
// Test do-while loops
function runTests() {
    console.log("=== Do-While Loops ===");

    // Basic do-while
    console.log("basic:");
    let i = 0;
    do {
        console.log("  i=" + i);
        i++;
    } while (i < 5);

    // Do-while always executes once
    console.log("once:");
    let x = 10;
    do {
        console.log("  x=" + x);
    } while (x < 5);

    // Do-while for input validation simulation
    console.log("validate:");
    let password = "";
    let attempts = 0;
    do {
        attempts++;
        password = attempts >= 3 ? "valid" : "invalid";
        console.log("  attempt=" + attempts + " pass=" + password);
    } while (password !== "valid" && attempts < 5);

    // Do-while with sum
    console.log("sum:");
    let sum = 0;
    let num = 0;
    do {
        num++;
        sum += num;
    } while (sum < 20);
    console.log("  sum=" + sum);

    // Do-while with countdown
    console.log("countdown:");
    let count = 5;
    do {
        console.log("  count=" + count);
        count--;
    } while (count > 0);

    // Do-while with menu simulation
    console.log("menu:");
    let choice = 1;
    do {
        console.log("  choice=" + choice);
        choice++;
    } while (choice <= 3);

    // Do-while for factorial
    console.log("factorial:");
    let n = 5;
    let factorial = 1;
    let temp = n;
    do {
        factorial *= temp;
        temp--;
    } while (temp > 1);
    console.log("  fact=" + factorial);

    // Do-while with array
    console.log("array:");
    const arr = [10, 20, 30];
    let idx = 0;
    do {
        console.log("  arr[" + idx + "]=" + arr[idx]);
        idx++;
    } while (idx < arr.length);

    // Do-while with break
    console.log("break:");
    let j = 0;
    do {
        if (j === 3) {
            break;
        }
        console.log("  j=" + j);
        j++;
    } while (j < 10);

    console.log("=== Do-While Loops Complete ===");
}

runTests();