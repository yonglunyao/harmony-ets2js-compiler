// 071-template-literals.ts
// Test template literals
function runTests() {
    console.log("=== Template Literals ===");

    // Basic template literal
    const name = "John";
    const greeting = `Hello, ${name}!`;
    console.log("greeting=" + greeting);

    // Multi-line string
    const multiline = `Line 1
Line 2
Line 3`;
    console.log("multiline=" + multiline);

    // Expression interpolation
    const a = 10, b = 20;
    const sum = `Sum: ${a + b}`;
    console.log("sum=" + sum);

    // Function call in template
    const upper = `name: ${name.toUpperCase()}`;
    console.log("upper=" + upper);

    // Object property
    const person = { name: "Jane", age: 30 };
    const info = `Person: ${person.name}, ${person.age}`;
    console.log("info=" + info);

    // Conditional in template
    const age = 20;
    const status = `Status: ${age >= 18 ? "adult" : "minor"}`;
    console.log("status=" + status);

    // Nested template
    const nested = `Outer ${`inner ${value}`}`;
    const value = "test";
    console.log("nested=" + nested);

    // Tagged template
    function tag(strings: TemplateStringsArray, ...values: any[]): string {
        console.log("strings=" + strings.join(","));
        console.log("values=" + values.join(","));
        return "tagged";
    }
    const tagged = tag`Hello ${name}, you are ${age}`;
    console.log("tagged=" + tagged);

    // Raw strings
    function raw(strings: TemplateStringsArray): string {
        return strings.raw[0];
    }
    const rawStr = raw`Line 1\nLine 2`;
    console.log("raw=" + rawStr);

    // Template with HTML
    const title = "Page";
    const html = `<h1>${title}</h1><p>Content</p>`;
    console.log("html=" + html);

    // Escaping in templates
    const escaped = `Use \`backticks\` and \${dollar}`;
    console.log("escaped=" + escaped);

    // Expression with complex logic
    const items = ["apple", "banana", "cherry"];
    const list = `Items: ${items.map(i => i.toUpperCase()).join(", ")}`;
    console.log("list=" + list);

    // Number formatting
    const price = 19.99;
    const formatted = `Price: $${price.toFixed(2)}`;
    console.log("formatted=" + formatted);

    // Date in template
    const now = new Date();
    const dateStr = `Now: ${now.toISOString()}`;
    console.log("date=" + dateStr);

    console.log("=== Template Literals Complete ===");
}

runTests();