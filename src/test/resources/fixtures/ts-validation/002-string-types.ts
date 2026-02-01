// 002-string-types.ts
// Test string types and operations
function runTests() {
    console.log("=== String Types ===");

    // Single quotes
    const str1: string = 'Hello';
    console.log("single=" + str1);

    // Double quotes
    const str2: string = "World";
    console.log("double=" + str2);

    // Template literal
    const name: string = "TypeScript";
    const template: string = `Hello, ${name}!`;
    console.log("template=" + template);

    // Multiline string
    const multiline: string = `Line 1
Line 2
Line 3`;
    console.log("multiline=" + multiline);

    // String length
    const text: string = "JavaScript";
    console.log("length=" + text.length);

    // String concatenation
    const first: string = "Hello";
    const second: string = " ";
    const third: string = "World";
    const combined: string = first + second + third;
    console.log("combined=" + combined);

    // Character access
    const str: string = "ABC";
    console.log("char0=" + str[0]);
    console.log("char1=" + str[1]);

    // toUpperCase
    const lower: string = "hello";
    console.log("uppercase=" + lower.toUpperCase());

    // toLowerCase
    const upper: string = "WORLD";
    console.log("lowercase=" + upper.toLowerCase());

    // trim
    const spaced: string = "  text  ";
    console.log("trim=" + spaced.trim());

    // substring
    const full: string = "JavaScript";
    console.log("substring=" + full.substring(0, 4));

    // indexOf
    const sentence: string = "Hello World";
    console.log("indexOf=" + sentence.indexOf("World"));

    // replace
    const original: string = "Hello World";
    console.log("replace=" + original.replace("World", "TypeScript"));
}

runTests();
