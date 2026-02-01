// 032-class-inheritance.ts
// Test class inheritance
function runTests() {
    console.log("=== Class Inheritance ===");

    // Basic inheritance
    class Animal {
        name: string;

        constructor(name: string) {
            this.name = name;
        }

        speak(): void {
            console.log(this.name + " makes a sound");
        }

        move(): void {
            console.log(this.name + " moves");
        }
    }

    class Dog extends Animal {
        breed: string;

        constructor(name: string, breed: string) {
            super(name);
            this.breed = breed;
        }

        speak(): void {
            console.log(this.name + " barks");
        }

        fetch(): void {
            console.log(this.name + " fetches");
        }
    }

    const dog = new Dog("Buddy", "Golden Retriever");
    console.log("name=" + dog.name);
    console.log("breed=" + dog.breed);
    dog.speak();
    dog.move();
    dog.fetch();

    // Multilevel inheritance
    class Vehicle {
        brand: string;
        constructor(brand: string) {
            this.brand = brand;
        }
        drive(): void {
            console.log("Driving " + this.brand);
        }
    }

    class Car extends Vehicle {
        model: string;
        constructor(brand: string, model: string) {
            super(brand);
            this.model = model;
        }
        honk(): void {
            console.log("Beep beep");
        }
    }

    class ElectricCar extends Car {
        battery: number;
        constructor(brand: string, model: string, battery: number) {
            super(brand, model);
            this.battery = battery;
        }
        charge(): void {
            console.log("Charging to " + this.battery + "%");
        }
    }

    const tesla = new ElectricCar("Tesla", "Model 3", 85);
    tesla.drive();
    tesla.honk();
    tesla.charge();

    // Method override with super
    class Base {
        getValue(): string {
            return "base";
        }
    }

    class Derived extends Base {
        getValue(): string {
            return super.getValue() + " derived";
        }
    }

    const derived = new Derived();
    console.log("override=" + derived.getValue());

    console.log("=== Class Inheritance Complete ===");
}

runTests();