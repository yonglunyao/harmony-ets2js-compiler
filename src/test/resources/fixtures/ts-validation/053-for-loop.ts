// 053-for-loop.ts
// Test for loops
function runTests() {
    console.log("=== For Loops ===");

    // Basic for loop
    console.log("basic:");
    for (let i = 0; i < 5; i++) {
        console.log("  i=" + i);
    }

    // For loop with step
    console.log("step:");
    for (let i = 0; i < 10; i += 2) {
        console.log("  i=" + i);
    }

    // For loop counting down
    console.log("countdown:");
    for (let i = 5; i >= 0; i--) {
        console.log("  i=" + i);
    }

    // For loop with multiple variables
    console.log("multi:");
    for (let i = 0, j = 10; i < j; i++, j--) {
        console.log("  i=" + i + " j=" + j);
    }

    // For loop iterating array
    const arr = [10, 20, 30, 40, 50];
    console.log("array:");
    for (let i = 0; i < arr.length; i++) {
        console.log("  arr[" + i + "]=" + arr[i]);
    }

    // For loop with break
    console.log("break:");
    for (let i = 0; i < 10; i++) {
        if (i === 5) {
            break;
        }
        console.log("  i=" + i);
    }

    // For loop with continue
    console.log("continue:");
    for (let i = 0; i < 10; i++) {
        if (i % 2 === 0) {
            continue;
        }
        console.log("  odd=" + i);
    }

    // Nested for loop
    console.log("nested:");
    for (let i = 0; i < 3; i++) {
        for (let j = 0; j < 3; j++) {
            console.log("  [" + i + "][" + j + "]");
        }
    }

    // For loop with string
    const str = "Hello";
    console.log("string:");
    for (let i = 0; i < str.length; i++) {
        console.log("  char=" + str[i]);
    }

    // For loop with condition
    console.log("condition:");
    let found = false;
    for (let i = 0; i < 10 && !found; i++) {
        if (i === 7) {
            found = true;
            console.log("  found=" + i);
        }
    }

    console.log("=== For Loops Complete ===");
}

runTests();