// 058-break-continue.ts
// Test break and continue statements
function runTests() {
    console.log("=== Break and Continue ===");

    // Break in for loop
    console.log("breakFor:");
    for (let i = 0; i < 10; i++) {
        if (i === 5) {
            break;
        }
        console.log("  i=" + i);
    }

    // Continue in for loop
    console.log("continueFor:");
    for (let i = 0; i < 10; i++) {
        if (i % 2 === 0) {
            continue;
        }
        console.log("  odd=" + i);
    }

    // Break in while loop
    console.log("breakWhile:");
    let j = 0;
    while (j < 10) {
        if (j === 7) {
            break;
        }
        console.log("  j=" + j);
        j++;
    }

    // Continue in while loop
    console.log("continueWhile:");
    let k = 0;
    while (k < 10) {
        k++;
        if (k % 3 === 0) {
            continue;
        }
        console.log("  k=" + k);
    }

    // Break in nested loops
    console.log("breakNested:");
    outer1: for (let i = 0; i < 3; i++) {
        for (let j = 0; j < 3; j++) {
            if (i === 1 && j === 1) {
                break outer1;
            }
            console.log("  [" + i + "][" + j + "]");
        }
    }

    // Continue in nested loops
    console.log("continueNested:");
    outer2: for (let i = 0; i < 3; i++) {
        for (let j = 0; j < 3; j++) {
            if (i === j) {
                continue outer2;
            }
            console.log("  [" + i + "][" + j + "]");
        }
    }

    // Labeled break
    console.log("labeledBreak:");
    outer: for (let i = 0; i < 3; i++) {
        for (let j = 0; j < 3; j++) {
            if (i === 1 && j === 2) {
                break outer;
            }
            console.log("  " + i + "," + j);
        }
    }

    // Labeled continue
    console.log("labeledContinue:");
    outer: for (let i = 0; i < 3; i++) {
        for (let j = 0; j < 3; j++) {
            if (j === 1) {
                continue outer;
            }
            console.log("  " + i + "," + j);
        }
    }

    // Break in switch
    console.log("breakSwitch:");
    for (let i = 0; i < 3; i++) {
        switch (i) {
            case 1:
                console.log("  one");
                break;
            case 2:
                console.log("  two");
                break;
            default:
                console.log("  other");
        }
    }

    // Break with condition
    console.log("conditionalBreak:");
    for (let i = 0; i < 100; i++) {
        const square = i * i;
        console.log("  " + i + "^2=" + square);
        if (square > 50) {
            break;
        }
    }

    console.log("=== Break and Continue Complete ===");
}

runTests();