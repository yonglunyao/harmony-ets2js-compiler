// 099-functional-programming.ts
// Test functional programming patterns
function runTests() {
    console.log("=== Functional Programming ===");

    // Pure function
    function pureAdd(a, b) {
        return a + b;
    }
    console.log("pure=" + pureAdd(5, 3));

    // Higher-order function
    function withMultiplier(fn, multiplier) {
        return (x) => fn(x) * multiplier;
    }

    const double = (x) => x * 2;
    const quadruple = withMultiplier(double, 2);
    console.log("hof=" + quadruple(5));

    // Currying
    function curry(fn) {
        return (a) => (b) => fn(a, b);
    }

    const add = (a, b) => a + b;
    const curriedAdd = curry(add);
    const addFive = curriedAdd(5);
    console.log("curry=" + addFive(3));

    // Function composition
    function compose(f, g) {
        return (x) => f(g(x));
    }

    const toString = (x) => x.toString();
    const toUpperCase = (x) => x.toUpperCase();
    const numberToUpper = compose(toUpperCase, toString);
    console.log("compose=" + numberToUpper(42));

    // Pipe (reverse composition)
    function pipe(g, f) {
        return (x) => f(g(x));
    }

    const addOne = (x) => x + 1;
    const doubleIt = (x) => x * 2;
    const addOneThenDouble = pipe(addOne, doubleIt);
    console.log("pipe=" + addOneThenDouble(5));

    // Memoization
    function memoize(fn) {
        const cache = new Map();
        return (...args) => {
            const key = JSON.stringify(args);
            if (cache.has(key)) {
                return cache.get(key);
            }
            const result = fn(...args);
            cache.set(key, result);
            return result;
        };
    }

    const fibonacci = memoize((n) => {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    });
    console.log("fib=" + fibonacci(10));

    // Functor (map)
    class Maybe {
        constructor(value) {
            this.value = value;
        }
        map(fn) {
            return this.value === null ? new Maybe(null) : new Maybe(fn(this.value));
        }
        getOrElse(defaultValue) {
            return this.value === null ? defaultValue : this.value;
        }
    }

    const maybeValue = new Maybe(5);
    const result = maybeValue
        .map((x) => x * 2)
        .map((x) => x + 10)
        .getOrElse(0);
    console.log("maybe=" + result);

    // Monad (bind/chain)
    class Monad {
        constructor(value) {
            this.value = value;
        }
        map(fn) {
            return new Monad(fn(this.value));
        }
        flatMap(fn) {
            return fn(this.value);
        }
        get() {
            return this.value;
        }
    }

    const monad = new Monad(5);
    const flatMapped = monad
        .flatMap((x) => new Monad(x * 2))
        .flatMap((x) => new Monad(x + 10));
    console.log("monad=" + flatMapped.get());

    // Reduce (fold)
    const numbers = [1, 2, 3, 4, 5];
    const sum = numbers.reduce((acc, x) => acc + x, 0);
    const product = numbers.reduce((acc, x) => acc * x, 1);
    console.log("sum=" + sum);
    console.log("product=" + product);

    // Filter and map chain
    const evenDoubled = numbers
        .filter((x) => x % 2 === 0)
        .map((x) => x * 2);
    console.log("chain=" + evenDoubled.join(","));

    console.log("=== Functional Programming Complete ===");
}

runTests();
