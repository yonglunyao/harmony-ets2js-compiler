// 036-class-getters-setters.ts
// Test class getters and setters
function runTests() {
    console.log("=== Class Getters and Setters ===");

    // Basic getter/setter
    class Temperature {
        constructor() {
            this._celsius = 0;
        }

        get celsius() {
            return this._celsius;
        }

        set celsius(value) {
            this._celsius = value;
        }

        get fahrenheit() {
            return this._celsius * 9 / 5 + 32;
        }

        set fahrenheit(value) {
            this._celsius = (value - 32) * 5 / 9;
        }
    }

    const temp = new Temperature();
    temp.celsius = 25;
    console.log("celsius=" + temp.celsius);
    console.log("fahrenheit=" + temp.fahrenheit);

    temp.fahrenheit = 100;
    console.log("fromF=" + temp.celsius);

    // Validation in setter
    class BankAccount {
        constructor() {
            this._balance = 0;
        }

        get balance() {
            return this._balance;
        }

        set balance(value) {
            if (value < 0) {
                console.log("invalid balance");
            } else {
                this._balance = value;
            }
        }

        deposit(amount) {
            if (amount > 0) {
                this._balance += amount;
            }
        }

        withdraw(amount) {
            if (amount > 0 && this._balance >= amount) {
                this._balance -= amount;
            }
        }
    }

    const account = new BankAccount();
    account.balance = 1000;
    console.log("balance=" + account.balance);
    account.deposit(500);
    console.log("afterDeposit=" + account.balance);
    account.withdraw(300);
    console.log("afterWithdraw=" + account.balance);

    // Computed properties
    class Rectangle {
        constructor(_width, _height) {
            this._width = _width;
            this._height = _height;
        }

        get width() {
            return this._width;
        }

        set width(value) {
            this._width = value;
        }

        get height() {
            return this._height;
        }

        set height(value) {
            this._height = value;
        }

        get area() {
            return this._width * this._height;
        }

        get perimeter() {
            return 2 * (this._width + this._height);
        }

        get isSquare() {
            return this._width === this._height;
        }
    }

    const rect = new Rectangle(5, 10);
    console.log("area=" + rect.area);
    console.log("perimeter=" + rect.perimeter);
    console.log("isSquare=" + rect.isSquare);

    // Read-only getter
    class Config {
        constructor() {
            this._settings = {};
        }

        get settings() {
            return { ...this._settings };
        }

        set(key, value) {
            this._settings[key] = value;
        }
    }

    const config = new Config();
    config.set("host", "localhost");
    config.set("port", "8080");
    console.log("settings=" + JSON.stringify(config.settings));

    console.log("=== Class Getters/Setters Complete ===");
}

runTests();
