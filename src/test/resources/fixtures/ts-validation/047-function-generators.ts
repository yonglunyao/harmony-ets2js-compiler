// 047-function-generators.ts
// Test generator functions
function runTests() {
    console.log("=== Generator Functions ===");

    // Basic generator
    function* generateNumbers() {
        yield 1;
        yield 2;
        yield 3;
    }

    const gen1 = generateNumbers();
    console.log("gen1=" + gen1.next().value);
    console.log("gen2=" + gen1.next().value);
    console.log("gen3=" + gen1.next().value);
    console.log("gen4=" + gen1.next().done);

    // Generator with loop
    function* countTo(max: number) {
        for (let i = 1; i <= max; i++) {
            yield i;
        }
    }

    const counter = countTo(5);
    for (const num of counter) {
        console.log("count=" + num);
    }

    // Infinite generator
    function* fibonacci() {
        let a = 0, b = 1;
        while (true) {
            yield a;
            [a, b] = [b, a + b];
        }
    }

    const fib = fibonacci();
    console.log("fib1=" + fib.next().value);
    console.log("fib2=" + fib.next().value);
    console.log("fib3=" + fib.next().value);
    console.log("fib4=" + fib.next().value);
    console.log("fib5=" + fib.next().value);

    // Generator with return value
    function* generateWithReturn() {
        yield 1;
        yield 2;
        return "done";
    }

    const gen2 = generateWithReturn();
    console.log("ret1=" + gen2.next().value);
    console.log("ret2=" + gen2.next().value);
    console.log("ret3=" + gen2.next().value);

    // Generator delegation
    function* generateA() {
        yield "a1";
        yield "a2";
    }

    function* generateB() {
        yield "b1";
        yield* generateA();
        yield "b2";
    }

    const gen3 = generateB();
    for (const val of gen3) {
        console.log("delegate=" + val);
    }

    // Generator as iterator
    function* range(start: number, end: number) {
        for (let i = start; i <= end; i++) {
            yield i;
        }
    }

    const rangeGen = range(1, 3);
    console.log("range=" + [...rangeGen].join(","));

    // Generator with array
    function* fromArray<T>(arr: T[]): Generator<T> {
        for (const item of arr) {
            yield item;
        }
    }

    const arrGen = fromArray([1, 2, 3]);
    for (const item of arrGen) {
        console.log("arrGen=" + item);
    }

    console.log("=== Generator Functions Complete ===");
}

runTests();