// 090-decorators.ts
// Test decorators (runtime simulation)
function runTests() {
    console.log("=== Decorators ===");

    // Class decorator (runtime simulation)
    function sealed(constructor) {
        Object.seal(constructor);
        Object.seal(constructor.prototype);
        console.log("sealed=" + constructor.name);
    }

    class SealedClass {
        constructor() {
            console.log("sealed instance");
        }
    }
    sealed(SealedClass);

    new SealedClass();

    // Method decorator (runtime simulation)
    function log(target, propertyKey, descriptor) {
        const originalMethod = descriptor.value;
        descriptor.value = function(...args) {
            console.log("method=" + propertyKey);
            return originalMethod.apply(this, args);
        };
        return descriptor;
    }

    class Logger {
        doSomething() {
            console.log("doing something");
        }
    }
    Object.defineProperty(Logger.prototype, "doSomething",
        log(Logger.prototype, "doSomething",
            Object.getOwnPropertyDescriptor(Logger.prototype, "doSomething")));

    const logger = new Logger();
    logger.doSomething();

    // Property decorator (runtime simulation)
    function format(formatString) {
        return function(target, propertyKey) {
            console.log("prop=" + propertyKey + " format=" + formatString);
        };
    }

    class Person {
        constructor() {
            this.name = "John";
        }
    }
    format("uppercase")(Person.prototype, "name");

    const person = new Person();
    console.log("person=" + person.name);

    // Parameter decorator (runtime simulation)
    function required(target, propertyKey, parameterIndex) {
        console.log("param=" + propertyKey + " index=" + parameterIndex);
    }

    class Calculator {
        add(a, b) {
            return a + b;
        }
    }
    required(Calculator.prototype, "add", 0);

    const calc = new Calculator();
    console.log("calc=" + calc.add(5, 3));

    // Accessor decorator (runtime simulation)
    function validate(target, propertyKey, descriptor) {
        const setter = descriptor.set;
        descriptor.set = function(value) {
            console.log("validate=" + propertyKey + "=" + value);
            setter?.call(this, value);
        };
    }

    class Validated {
        constructor() {
            this._value = 0;
        }

        get value() {
            return this._value;
        }

        set value(v) {
            this._value = v;
        }
    }
    validate(Validated.prototype, "value",
        Object.getOwnPropertyDescriptor(Validated.prototype, "value"));

    const v = new Validated();
    v.value = 42;
    console.log("validated=" + v.value);

    console.log("=== Decorators Complete ===");
}

runTests();
