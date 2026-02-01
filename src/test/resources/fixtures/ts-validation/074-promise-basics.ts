// 074-promise-basics.ts
// Test Promise basics
function runTests() {
    console.log("=== Promise Basics ===");

    // Create resolved promise
    const resolved = Promise.resolve("success");
    resolved.then((value) => console.log("resolved=" + value));

    // Create rejected promise
    const rejected = Promise.reject("error");
    rejected.catch((reason) => console.log("rejected=" + reason));

    // Promise constructor
    const prom = new Promise((resolve, reject) => {
        setTimeout(() => {
            resolve("completed");
        }, 100);
    });

    // Promise chain
    Promise.resolve(5)
        .then((x) => x * 2)
        .then((x) => x + 10)
        .then((x) => console.log("chain=" + x));

    // Catch in chain
    Promise.resolve(10)
        .then((x) => {
            if (x > 5) {
                throw new Error("too big");
            }
            return x;
        })
        .catch((e) => console.log("caught=" + e.message));

    // Finally
    Promise.resolve("data")
        .then((x) => x.toUpperCase())
        .finally(() => console.log("finally"))
        .then((x) => console.log("afterFinally=" + x));

    // Promise all
    const p1 = Promise.resolve(1);
    const p2 = Promise.resolve(2);
    const p3 = Promise.resolve(3);

    Promise.all([p1, p2, p3]).then((values) => {
        console.log("all=" + values.join(","));
    });

    // Promise race
    const slow = new Promise((r) => setTimeout(() => r("slow"), 200));
    const fast = new Promise((r) => setTimeout(() => r("fast"), 100));

    Promise.race([slow, fast]).then((value) => {
        console.log("race=" + value);
    });

    // Promise allSettled
    const success = Promise.resolve("ok");
    const failure = Promise.reject("fail");

    Promise.allSettled([success, failure]).then((results) => {
        results.forEach((r) => {
            console.log("settled=" + r.status);
        });
    });

    // Promise any
    const first = Promise.reject("first");
    const second = Promise.resolve("second");
    const third = Promise.reject("third");

    Promise.any([first, second, third]).then((value) => {
        console.log("any=" + value);
    });

    console.log("=== Promise Basics Complete ===");
}

runTests();
