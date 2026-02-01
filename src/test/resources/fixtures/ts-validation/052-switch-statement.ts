// 052-switch-statement.ts
// Test switch statements
function runTests() {
    console.log("=== Switch Statements ===");

    // Basic switch
    const day = 3;
    let dayName = "";
    switch (day) {
        case 1:
            dayName = "Monday";
            break;
        case 2:
            dayName = "Tuesday";
            break;
        case 3:
            dayName = "Wednesday";
            break;
        case 4:
            dayName = "Thursday";
            break;
        case 5:
            dayName = "Friday";
            break;
        default:
            dayName = "Weekend";
    }
    console.log("day=" + dayName);

    // Fall-through
    const grade = "B";
    let message = "";
    switch (grade) {
        case "A":
        case "B":
            message = "Good";
            break;
        case "C":
            message = "Average";
            break;
        default:
            message = "Poor";
    }
    console.log("grade=" + message);

    // Switch with string
    const color = "red";
    let hex = "";
    switch (color) {
        case "red":
            hex = "#FF0000";
            break;
        case "green":
            hex = "#00FF00";
            break;
        case "blue":
            hex = "#0000FF";
            break;
        default:
            hex = "#000000";
    }
    console.log("hex=" + hex);

    // Switch with multiple statements
    const value = 2;
    switch (value) {
        case 1:
            console.log("one");
            break;
        case 2:
            console.log("two");
            console.log("isTwo");
            break;
        case 3:
            console.log("three");
            break;
    }

    // Switch without break (intentional)
    const num = 2;
    let result = 0;
    switch (num) {
        case 1:
            result += 1;
        case 2:
            result += 2;
        case 3:
            result += 3;
            break;
        default:
            result = 0;
    }
    console.log("fall=" + result);

    // Switch with expressions
    const x = 10, y = 5;
    const operation = "+";
    let calcResult = 0;
    switch (operation) {
        case "+":
            calcResult = x + y;
            break;
        case "-":
            calcResult = x - y;
            break;
        case "*":
            calcResult = x * y;
            break;
        case "/":
            calcResult = x / y;
            break;
    }
    console.log("calc=" + calcResult);

    // Nested switch
    const category = "fruit";
    const item = "apple";
    let description = "";
    switch (category) {
        case "fruit":
            switch (item) {
                case "apple":
                    description = "red fruit";
                    break;
                case "banana":
                    description = "yellow fruit";
                    break;
            }
            break;
        case "vegetable":
            description = "vegetable";
            break;
    }
    console.log("nested=" + description);

    console.log("=== Switch Statements Complete ===");
}

runTests();