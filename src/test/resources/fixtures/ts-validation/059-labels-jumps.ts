// 059-labels-jumps.ts
// Test labeled statements and jumps
function runTests() {
    console.log("=== Labels and Jumps ===");

    // Labeled for loop
    console.log("labeledFor:");
    outer: for (let i = 0; i < 3; i++) {
        console.log("  i=" + i);
        for (let j = 0; j < 3; j++) {
            if (j === 2) {
                break outer;
            }
            console.log("    j=" + j);
        }
    }

    // Labeled while loop
    console.log("labeledWhile:");
    outer: let i = 0;
    while (i < 5) {
        let j = 0;
        while (j < 5) {
            if (i === 2 && j === 2) {
                break outer;
            }
            console.log("  " + i + "," + j);
            j++;
        }
        i++;
    }

    // Labeled continue
    console.log("labeledContinue:");
    outer: for (let i = 0; i < 5; i++) {
        for (let j = 0; j < 5; j++) {
            if (i === j) {
                continue outer;
            }
            console.log("  " + i + "," + j);
        }
    }

    // Multiple labels
    console.log("multipleLabels:");
    first: for (let i = 0; i < 3; i++) {
        second: for (let j = 0; j < 3; j++) {
            if (i === 1 && j === 1) {
                break first;
            }
            console.log("  [" + i + "][" + j + "]");
        }
    }

    // Label with block
    console.log("labeledBlock:");
    myBlock: {
        const x = 10;
        const y = 20;
        console.log("  x=" + x + " y=" + y);
        if (x + y > 25) {
            break myBlock;
        }
        console.log("  unreachable");
    }

    // Nested labeled loops
    console.log("deepNest:");
    level1: for (let i = 0; i < 2; i++) {
        level2: for (let j = 0; j < 2; j++) {
            level3: for (let k = 0; k < 2; k++) {
                if (i === 1 && j === 1) {
                    break level1;
                }
                console.log("  " + i + j + k);
            }
        }
    }

    // Label for searching
    console.log("search:");
    const matrix = [
        [1, 2, 3],
        [4, 5, 6],
        [7, 8, 9]
    ];
    search: for (let i = 0; i < matrix.length; i++) {
        for (let j = 0; j < matrix[i].length; j++) {
            if (matrix[i][j] === 5) {
                console.log("  found at " + i + "," + j);
                break search;
            }
        }
    }

    // Label with continue in nested
    console.log("continueNested:");
    outer: for (let i = 0; i < 3; i++) {
        console.log("  i=" + i);
        for (let j = 0; j < 3; j++) {
            if (j === 1) {
                continue outer;
            }
            console.log("    j=" + j);
        }
    }

    console.log("=== Labels and Jumps Complete ===");
}

runTests();
