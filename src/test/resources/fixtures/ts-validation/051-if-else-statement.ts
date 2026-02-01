// 051-if-else-statement.ts
// Test if/else statements
function runTests() {
    console.log("=== If/Else Statements ===");

    // Basic if
    const value1 = 10;
    if (value1 > 5) {
        console.log("greater");
    }

    // If/else
    const value2 = 3;
    if (value2 > 5) {
        console.log("greater");
    } else {
        console.log("lessEqual");
    }

    // If/else if/else
    const value3 = 7;
    if (value3 < 5) {
        console.log("less");
    } else if (value3 < 10) {
        console.log("between");
    } else {
        console.log("greaterEqual");
    }

    // Multiple conditions
    const age = 25;
    if (age >= 18 && age < 65) {
        console.log("workingAge");
    }

    // Ternary operator
    const num = 10;
    const result = num > 5 ? "big" : "small";
    console.log("ternary=" + result);

    // Nested if
    const score = 85;
    let grade = "";
    if (score >= 90) {
        grade = "A";
    } else {
        if (score >= 80) {
            grade = "B";
        } else if (score >= 70) {
            grade = "C";
        } else {
            grade = "F";
        }
    }
    console.log("grade=" + grade);

    // If with complex condition
    const x = 5, y = 10;
    if (x > 0 && y > 0) {
        console.log("bothPositive");
    }

    if (x > 0 || y > 0) {
        console.log("atLeastOne");
    }

    // If with truthy/falsy
    const name = "John";
    if (name) {
        console.log("hasName");
    }

    const empty = "";
    if (!empty) {
        console.log("emptyString");
    }

    // If with null/undefined
    let maybe: string | null = null;
    if (maybe !== null) {
        console.log("notNull");
    } else {
        console.log("isNull");
    }

    maybe = "value";
    if (maybe !== null) {
        console.log("nowNotNull");
    }

    console.log("=== If/Else Statements Complete ===");
}

runTests();