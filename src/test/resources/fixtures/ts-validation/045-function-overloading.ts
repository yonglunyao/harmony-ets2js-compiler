// 045-function-overloading.ts
// Test function overloading
function runTests() {
    console.log("=== Function Overloading ===");

    // Basic overloading
    function process(value: string): string;
    function process(value: number): number;
    function process(value: string | number): string | number {
        if (typeof value === "string") {
            return value.toUpperCase();
        } else {
            return value * 2;
        }
    }

    console.log("str=" + process("hello"));
    console.log("num=" + process(21));

    // Multiple overloads
    function combine(a: string, b: string): string;
    function combine(a: number, b: number): number;
    function combine(a: string, b: number): string;
    function combine(a: number, b: string): string;
    function combine(a: string | number, b: string | number): string | number {
        if (typeof a === "string" && typeof b === "string") {
            return a + b;
        } else if (typeof a === "number" && typeof b === "number") {
            return a + b;
        } else {
            return String(a) + String(b);
        }
    }

    console.log("combine1=" + combine("a", "b"));
    console.log("combine2=" + combine(1, 2));
    console.log("combine3=" + combine("a", 1));
    console.log("combine4=" + combine(1, "b"));

    // Overloading with different parameter counts
    function buildName(firstName: string): string;
    function buildName(firstName: string, lastName: string): string;
    function buildName(firstName: string, lastName?: string): string {
        return lastName ? firstName + " " + lastName : firstName;
    }

    console.log("name1=" + buildName("John"));
    console.log("name2=" + buildName("John", "Doe"));

    // Overloading with object types
    interface IOptions {
        path: string;
        verbose?: boolean;
    }

    function configure(options: IOptions): void;
    function configure(path: string, verbose?: boolean): void;
    function configure(pathOrOptions: string | IOptions, verbose?: boolean): void {
        if (typeof pathOrOptions === "string") {
            console.log("config=" + pathOrOptions + " v=" + verbose);
        } else {
            console.log("config=" + pathOrOptions.path + " v=" + pathOrOptions.verbose);
        }
    }

    configure("/api", true);
    configure({ path: "/api", verbose: false });

    // Method overloading in class
    class Calculator {
        add(a: number, b: number): number;
        add(a: string, b: string): string;
        add(a: any, b: any): any {
            return a + b;
        }
    }

    const calc = new Calculator();
    console.log("addNum=" + calc.add(5, 3));
    console.log("addStr=" + calc.add("Hello", " World"));

    console.log("=== Function Overloading Complete ===");
}

runTests();