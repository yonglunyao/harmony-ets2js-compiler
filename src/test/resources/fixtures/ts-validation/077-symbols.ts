// 077-symbols.ts
// Test Symbol type
function runTests() {
    console.log("=== Symbols ===");

    // Create symbol
    const sym1 = Symbol("description");
    const sym2 = Symbol("description");
    console.log("unique=" + (sym1 === sym2));

    // Symbol for object properties
    const idSym = Symbol("id");
    const user = {
        name: "John",
        [idSym]: 123
    };
    console.log("name=" + user.name);
    console.log("idSym=" + user[idSym]);

    // Symbols are not iterated by for-in
    console.log("keys=" + Object.keys(user).join(","));
    console.log("keysLength=" + Object.keys(user).length);

    // GetOwnPropertySymbols
    const symbols = Object.getOwnPropertySymbols(user);
    console.log("symProps=" + symbols.length);

    // Symbol.for() - global registry
    const globalSym1 = Symbol.for("app");
    const globalSym2 = Symbol.for("app");
    console.log("globalSame=" + (globalSym1 === globalSym2));

    // Symbol.keyFor()
    const key = Symbol.keyFor(globalSym1);
    console.log("keyFor=" + key);

    // Well-known symbols
    console.log("toStringTag=" + Symbol.toStringTag);
    console.log("iterator=" + Symbol.iterator);
    console.log("asyncIterator=" + Symbol.asyncIterator);

    // Symbol.iterator for custom iterable
    const iterable = {
        data: [1, 2, 3],
        [Symbol.iterator]() {
            let index = 0;
            const data = this.data;
            return {
                next(): { value: number; done: boolean } {
                    if (index < data.length) {
                        return { value: data[index++], done: false };
                    }
                    return { value: 0, done: true };
                }
            };
        }
    };

    for (const value of iterable) {
        console.log("iter=" + value);
    }

    // Symbol.hasInstance
    class MyArray {
        static [Symbol.hasInstance](instance: any): boolean {
            return Array.isArray(instance);
        }
    }
    console.log("isArray=" + ([] instanceof MyArray));

    // Symbol.toPrimitive
    const primitiveObj = {
        [Symbol.toPrimitive](hint: string): number {
            if (hint === "number") {
                return 42;
            }
            return 0;
        }
    };
    console.log("toPrimitive=" + (+primitiveObj));

    // Symbol.toStringTag
    class CustomClass {
        get [Symbol.toStringTag](): string {
            return "CustomClass";
        }
    }
    console.log("tag=" + Object.prototype.toString.call(new CustomClass()));

    console.log("=== Symbols Complete ===");
}

runTests();