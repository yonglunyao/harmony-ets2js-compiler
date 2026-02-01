// 096-performance-optimization.ts
// Test performance optimization patterns
function runTests() {
    console.log("=== Performance Optimization ===");

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

    const expensive = memoize((n) => {
        console.log("computing=" + n);
        return n * 2;
    });

    console.log("memo1=" + expensive(5));
    console.log("memo2=" + expensive(5));

    // Lazy evaluation
    class Lazy {
        constructor(evaluator) {
            this.evaluator = evaluator;
            this.evaluated = false;
        }
        get() {
            if (!this.evaluated) {
                this.value = this.evaluator();
                this.evaluated = true;
            }
            return this.value;
        }
    }

    const lazy = new Lazy(() => {
        console.log("evaluating");
        return 42;
    });
    console.log("lazy1=" + lazy.get());
    console.log("lazy2=" + lazy.get());

    // Debounce simulation
    function debounce(fn, delay) {
        let timeout;
        return (...args) => {
            clearTimeout(timeout);
            timeout = setTimeout(() => fn(...args), delay);
        };
    }

    const debounced = debounce((msg) => {
        console.log("debounced=" + msg);
    }, 100);

    debounced("call1");
    debounced("call2");
    debounced("call3");
    console.log("debounce queued");

    // Throttle simulation
    function throttle(fn, limit) {
        let inThrottle = false;
        return (...args) => {
            if (!inThrottle) {
                fn(...args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    }

    const throttled = throttle((msg) => {
        console.log("throttled=" + msg);
    }, 100);

    throttled("call1");
    throttled("call2");
    throttled("call3");

    // Object pooling
    class Pool {
        constructor(factory, reset) {
            this.factory = factory;
            this.reset = reset;
            this.items = [];
        }
        acquire() {
            return this.items.pop() ?? this.factory();
        }
        release(item) {
            this.reset(item);
            this.items.push(item);
        }
    }

    const bufferPool = new Pool(
        () => new Array(1000),
        () => {}
    );

    const buf1 = bufferPool.acquire();
    const buf2 = bufferPool.acquire();
    console.log("pool=" + buf1.length + "," + buf2.length);

    bufferPool.release(buf1);
    bufferPool.release(buf2);

    // Lazy initialization
    class Singleton {
        static getInstance() {
            if (!Singleton.instance) {
                Singleton.instance = new Singleton();
                console.log("creating singleton");
            }
            return Singleton.instance;
        }
    }

    const s1 = Singleton.getInstance();
    const s2 = Singleton.getInstance();
    console.log("singleton=" + (s1 === s2));

    console.log("=== Performance Optimization Complete ===");
}

runTests();
