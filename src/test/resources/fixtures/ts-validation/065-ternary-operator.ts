// 065-ternary-operator.ts
// Test ternary operator
function runTests() {
    console.log("=== Ternary Operator ===");

    // Basic ternary
    const age = 20;
    const status = age >= 18 ? "adult" : "minor";
    console.log("status=" + status);

    // Ternary with numbers
    const max = 10 > 5 ? 10 : 5;
    console.log("max=" + max);

    // Nested ternary
    const num = 15;
    const result = num > 10 ? (num > 20 ? "very large" : "large") : "small";
    console.log("nested=" + result);

    // Ternary with expressions
    const a = 5, b = 10;
    const abs = a > b ? a - b : b - a;
    console.log("abs=" + abs);

    // Ternary for default values
    const name: string | null = null;
    const displayName = name ? name : "Anonymous";
    console.log("name=" + displayName);

    // Ternary in expressions
    const value = 5;
    const doubled = value > 0 ? value * 2 : 0;
    console.log("doubled=" + doubled);

    // Multiple ternaries
    const score = 85;
    const grade = score >= 90 ? "A" : score >= 80 ? "B" : score >= 70 ? "C" : "F";
    console.log("grade=" + grade);

    // Ternary with function calls
    const getMessage = (success: boolean): string => {
        return success ? "Success!" : "Failed!";
    };
    console.log("msg=" + getMessage(true));

    // Ternary in template literal
    const isLoggedIn = true;
    const welcome = `Welcome ${isLoggedIn ? "back" : "guest"}`;
    console.log("welcome=" + welcome);

    // Ternary for assignment
    let x: number | string;
    const condition = true;
    x = condition ? 42 : "fallback";
    console.log("x=" + x);

    // Ternary with null check
    const data: { value?: string } = {};
    const output = data.value ? data.value : "default";
    console.log("output=" + output);

    // Ternary in array
    const items = [1, 2, 3];
    const message = items.length > 0 ? "has items" : "empty";
    console.log("arrMsg=" + message);

    console.log("=== Ternary Operator Complete ===");
}

runTests();