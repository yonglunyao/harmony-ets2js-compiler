// 025-object-getters-setters.ts
// Test object getters and setters
function runTests() {
    console.log("=== Object Getters and Setters ===");

    // Basic getter/setter
    const person = {
        _firstName: "John",
        _lastName: "Doe",

        get fullName() {
            return this._firstName + " " + this._lastName;
        },

        set fullName(name) {
            const parts = name.split(" ");
            this._firstName = parts[0];
            this._lastName = parts[1] || "";
        }
    };

    console.log("fullName=" + person.fullName);
    person.fullName = "Jane Smith";
    console.log("afterSet=" + person.fullName);

    // Computed property names with getter
    const methodName = "getName";
    const obj = {
        _name: "test",
        get [methodName]() {
            return this._name;
        }
    };
    console.log("computedGet=" + obj.getName);

    // Getter only (read-only)
    const config = {
        _value: 42,
        get value() {
            return this._value;
        }
    };
    console.log("readonly=" + config.value);

    // Validation in setter
    const temperature = {
        _celsius: 0,
        get celsius() {
            return this._celsius;
        },
        set celsius(value) {
            if (value < -273.15) {
                console.log("invalid");
            } else {
                this._celsius = value;
            }
        },
        get fahrenheit() {
            return this._celsius * 9 / 5 + 32;
        }
    };

    temperature.celsius = 25;
    console.log("celsius=" + temperature.celsius);
    console.log("fahrenheit=" + temperature.fahrenheit);

    // Static-like getter
    const Constants = {
        get PI() {
            return 3.14159;
        },
        get E() {
            return 2.71828;
        }
    };
    console.log("PI=" + Constants.PI);
    console.log("E=" + Constants.E);

    console.log("=== Object Getters/Setters Complete ===");
}

runTests();
