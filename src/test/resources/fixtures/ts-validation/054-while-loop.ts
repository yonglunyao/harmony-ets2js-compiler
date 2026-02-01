// 054-while-loop.ts
// Test while loops
function runTests() {
    console.log("=== While Loops ===");

    // Basic while loop
    console.log("basic:");
    let i = 0;
    while (i < 5) {
        console.log("  i=" + i);
        i++;
    }

    // While loop with countdown
    console.log("countdown:");
    let count = 5;
    while (count > 0) {
        console.log("  count=" + count);
        count--;
    }

    // While loop for sum
    console.log("sum:");
    let sum = 0;
    let num = 1;
    while (num <= 10) {
        sum += num;
        num++;
    }
    console.log("  total=" + sum);

    // While loop with break
    console.log("break:");
    let j = 0;
    while (j < 10) {
        if (j === 5) {
            break;
        }
        console.log("  j=" + j);
        j++;
    }

    // While loop with continue
    console.log("continue:");
    let k = 0;
    while (k < 10) {
        k++;
        if (k % 2 === 0) {
            continue;
        }
        console.log("  odd=" + k);
    }

    // While loop with condition on variable
    console.log("condition:");
    let value = 100;
    while (value > 10) {
        value = Math.floor(value / 2);
        console.log("  val=" + value);
    }

    // While loop for array iteration
    console.log("array:");
    const arr = [1, 2, 3, 4, 5];
    let idx = 0;
    while (idx < arr.length) {
        console.log("  arr[" + idx + "]=" + arr[idx]);
        idx++;
    }

    // While loop with boolean flag
    console.log("flag:");
    let running = true;
    let counter = 0;
    while (running) {
        counter++;
        if (counter >= 5) {
            running = false;
        }
        console.log("  run=" + counter);
    }

    // While loop until condition
    console.log("until:");
    let total = 0;
    let adder = 1;
    while (total < 100) {
        total += adder;
        adder++;
    }
    console.log("  total=" + total);

    console.log("=== While Loops Complete ===");
}

runTests();