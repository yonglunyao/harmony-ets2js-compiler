// 050-function-closures.ts
// Test function closures
function runTests() {
    console.log("=== Function Closures ===");

    // Basic closure
    function createGreeter(greeting) {
        return function(name) {
            return greeting + ", " + name + "!";
        };
    }

    const sayHello = createGreeter("Hello");
    const sayHi = createGreeter("Hi");

    console.log("hello=" + sayHello("John"));
    console.log("hi=" + sayHi("Jane"));

    // Closure with counter
    function createCounter() {
        let count = 0;
        return {
            increment() {
                count++;
                return count;
            },
            decrement() {
                count--;
                return count;
            },
            getCount() {
                return count;
            }
        };
    }

    const counter = createCounter();
    console.log("inc1=" + counter.increment());
    console.log("inc2=" + counter.increment());
    console.log("dec=" + counter.decrement());
    console.log("count=" + counter.getCount());

    // Closure for data hiding
    function createBankAccount(initialBalance) {
        let balance = initialBalance;

        return {
            deposit(amount) {
                balance += amount;
            },
            withdraw(amount) {
                if (amount <= balance) {
                    balance -= amount;
                }
            },
            getBalance() {
                return balance;
            }
        };
    }

    const account = createBankAccount(1000);
    account.deposit(500);
    account.withdraw(200);
    console.log("balance=" + account.getBalance());

    // Closure with loop (common gotcha)
    function createFunctions() {
        const functions = [];
        for (let i = 0; i < 3; i++) {
            functions.push(() => i);
        }
        return functions;
    }

    const funcs = createFunctions();
    console.log("func0=" + funcs[0]());
    console.log("func1=" + funcs[1]());
    console.log("func2=" + funcs[2]());

    // Closure with let (fixes loop issue)
    function createFunctionsFixed() {
        const functions = [];
        for (let i = 0; i < 3; i++) {
            functions.push(() => i);
        }
        return functions;
    }

    const fixedFuncs = createFunctionsFixed();
    console.log("fixed0=" + fixedFuncs[0]());
    console.log("fixed1=" + fixedFuncs[1]());
    console.log("fixed2=" + fixedFuncs[2]());

    // Closure factory
    function createMultiplier(factor) {
        return function(value) {
            return value * factor;
        };
    }

    const double = createMultiplier(2);
    const triple = createMultiplier(3);
    console.log("double=" + double(5));
    console.log("triple=" + triple(5));

    // Closure with private methods
    function createPerson(name) {
        let age = 0;

        return {
            getName() {
                return name;
            },
            setAge(newAge) {
                if (newAge >= 0) {
                    age = newAge;
                }
            },
            getAge() {
                return age;
            }
        };
    }

    const person = createPerson("John");
    person.setAge(30);
    console.log("person=" + person.getName() + "," + person.getAge());

    console.log("=== Function Closures Complete ===");
}

runTests();
