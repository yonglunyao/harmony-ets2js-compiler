// 020-array-multidimensional.ts
// Test multidimensional arrays
function runTests() {
    console.log("=== Multidimensional Arrays ===");

    // 2D array
    const matrix: number[][] = [
        [1, 2, 3],
        [4, 5, 6],
        [7, 8, 9]
    ];
    console.log("matrix=" + JSON.stringify(matrix));

    // Access 2D
    console.log("matrix[1][2]=" + matrix[1][2]);

    // 3D array
    const cube: number[][][] = [
        [[1, 2], [3, 4]],
        [[5, 6], [7, 8]]
    ];
    console.log("cube=" + JSON.stringify(cube));

    // Access 3D
    console.log("cube[1][0][1]=" + cube[1][0][1]);

    // Iterate 2D
    console.log("2D iteration:");
    for (let i: number = 0; i < matrix.length; i++) {
        for (let j: number = 0; j < matrix[i].length; j++) {
            console.log("  [" + i + "][" + j + "]=" + matrix[i][j]);
        }
    }

    // Flat 2D array
    const flat: number[] = matrix.flat();
    console.log("flat=" + flat.join(","));

    // Create 2D from 1D
    const oneD: number[] = [1, 2, 3, 4, 5, 6];
    const twoD: number[][] = [];
    let row: number[] = [];
    for (let i: number = 0; i < oneD.length; i++) {
        row.push(oneD[i]);
        if (row.length === 2) {
            twoD.push(row);
            row = [];
        }
    }
    console.log("twoD=" + JSON.stringify(twoD));

    // Jagged array
    const jagged: number[][] = [
        [1],
        [2, 3],
        [4, 5, 6]
    ];
    console.log("jagged=" + JSON.stringify(jagged));

    console.log("=== Multidimensional Arrays Complete ===");
}

runTests();