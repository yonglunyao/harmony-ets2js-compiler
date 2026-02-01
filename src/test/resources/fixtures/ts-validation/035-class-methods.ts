// 035-class-methods.ts
// Test class methods
function runTests() {
    console.log("=== Class Methods ===");

    // Instance methods
    class Calculator {
        add(a: number, b: number): number {
            return a + b;
        }

        subtract(a: number, b: number): number {
            return a - b;
        }

        // Method with default parameter
        multiply(a: number, b: number = 1): number {
            return a * b;
        }

        // Method with rest parameter
        sum(...numbers: number[]): number {
            return numbers.reduce((acc, n) => acc + n, 0);
        }
    }

    const calc = new Calculator();
    console.log("add=" + calc.add(5, 3));
    console.log("sub=" + calc.subtract(10, 4));
    console.log("mul=" + calc.multiply(7));
    console.log("sum=" + calc.sum(1, 2, 3, 4, 5));

    // Static methods
    class MathUtils {
        static square(x: number): number {
            return x * x;
        }

        static cube(x: number): number {
            return x * x * x;
        }

        static abs(x: number): number {
            return x < 0 ? -x : x;
        }
    }

    console.log("square=" + MathUtils.square(5));
    console.log("cube=" + MathUtils.cube(3));
    console.log("abs=" + MathUtils.abs(-10));

    // Method chaining
    class QueryBuilder {
        private parts: string[] = [];

        select(columns: string): this {
            this.parts.push("SELECT " + columns);
            return this;
        }

        from(table: string): this {
            this.parts.push("FROM " + table);
            return this;
        }

        where(condition: string): this {
            this.parts.push("WHERE " + condition);
            return this;
        }

        build(): string {
            return this.parts.join(" ");
        }
    }

    const query = new QueryBuilder()
        .select("*")
        .from("users")
        .where("active = true")
        .build();
    console.log("query=" + query);

    // Callback method
    class Timer {
        delay(ms: number, callback: () => void): void {
            console.log("waiting " + ms + "ms");
            callback();
        }
    }

    const timer = new Timer();
    timer.delay(1000, () => console.log("done"));

    // Methods returning functions
    class Multiplier {
        create(factor: number): (x: number) => number {
            return (x: number) => x * factor;
        }
    }

    const mult = new Multiplier();
    const double = mult.create(2);
    console.log("double=" + double(5));

    console.log("=== Class Methods Complete ===");
}

runTests();