// 034-class-constructors.ts
// Test class constructors
function runTests() {
    console.log("=== Class Constructors ===");

    // Basic constructor
    class Person {
        constructor(name, age) {
            this.name = name;
            this.age = age;
        }
    }

    const person1 = new Person("John", 30);
    console.log("person=" + person1.name + "," + person1.age);

    // Parameter properties (simulated)
    class Employee {
        constructor(name, salary, department) {
            this.name = name;
            this._salary = salary;
            this.department = department;
        }

        getInfo() {
            return this.name + " in " + this.department;
        }

        getSalary() {
            return this._salary;
        }
    }

    const emp = new Employee("Jane", 50000, "Engineering");
    console.log("employee=" + emp.getInfo());
    console.log("salary=" + emp.getSalary());

    // Optional constructor parameters
    class Product {
        constructor(name, price, description) {
            this.name = name;
            this.price = price;
            this.description = description;
        }

        getDetails() {
            return this.name + " $" + this.price + (this.description ? " - " + this.description : "");
        }
    }

    const product1 = new Product("Widget", 9.99);
    const product2 = new Product("Gadget", 19.99, "Premium");
    console.log("prod1=" + product1.getDetails());
    console.log("prod2=" + product2.getDetails());

    // Default parameter values
    class Config {
        constructor(host, port, secure) {
            this.host = host !== undefined ? host : "localhost";
            this.port = port !== undefined ? port : 8080;
            this.secure = secure !== undefined ? secure : false;
        }

        toString() {
            return this.host + ":" + this.port + " (secure=" + this.secure + ")";
        }
    }

    const config1 = new Config();
    const config2 = new Config("example.com", 443, true);
    console.log("config1=" + config1.toString());
    console.log("config2=" + config2.toString());

    // Constructor overloading (simulation)
    class Overload {
        constructor(value) {
            this.value = value;
        }

        getValue() {
            return String(this.value);
        }
    }

    const ov1 = new Overload("text");
    const ov2 = new Overload(42);
    console.log("overload1=" + ov1.getValue());
    console.log("overload2=" + ov2.getValue());

    console.log("=== Class Constructors Complete ===");
}

runTests();
