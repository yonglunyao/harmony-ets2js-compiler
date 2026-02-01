// 040-class-instanceof.ts
// Test instanceof and type guards
function runTests() {
    console.log("=== instanceof and Type Guards ===");

    // Basic instanceof
    class Animal {
        name: string;
        constructor(name: string) {
            this.name = name;
        }
    }

    class Dog extends Animal {
        bark(): void {
            console.log(this.name + " barks");
        }
    }

    const animal = new Animal("Generic");
    const dog = new Dog("Buddy");

    console.log("animal is Animal=" + (animal instanceof Animal));
    console.log("dog is Dog=" + (dog instanceof Dog));
    console.log("dog is Animal=" + (dog instanceof Animal));

    // instanceof in conditionals
    function makeSound(pet: Animal): void {
        if (pet instanceof Dog) {
            pet.bark();
        } else {
            console.log(pet.name + " makes a sound");
        }
    }

    makeSound(animal);
    makeSound(dog);

    // Type guard with union types
    class Car {
        drive(): void {
            console.log("Driving car");
        }
    }

    class Boat {
        sail(): void {
            console.log("Sailing boat");
        }
    }

    function operate(vehicle: Car | Boat): void {
        if (vehicle instanceof Car) {
            vehicle.drive();
        } else {
            vehicle.sail();
        }
    }

    operate(new Car());
    operate(new Boat());

    // Custom type guard
    interface IPet {
        name: string;
    }

    interface IDog extends IPet {
        bark(): void;
    }

    interface ICat extends IPet {
        meow(): void;
    }

    function isDog(pet: IPet): pet is IDog {
        return (pet as IDog).bark !== undefined;
    }

    function isCat(pet: IPet): pet is ICat {
        return (pet as ICat).meow !== undefined;
    }

    function petSound(pet: IPet): void {
        if (isDog(pet)) {
            pet.bark();
        } else if (isCat(pet)) {
            pet.meow();
        } else {
            console.log(pet.name + " makes sound");
        }
    }

    const myDog: IDog = { name: "Rex", bark: () => console.log("Woof") };
    const myCat: ICat = { name: "Whiskers", meow: () => console.log("Meow") };

    petSound(myDog);
    petSound(myCat);

    // Type guard for primitives
    function processValue(value: string | number): string {
        if (typeof value === "string") {
            return "string: " + value.toUpperCase();
        } else {
            return "number: " + (value * 2);
        }
    }

    console.log(processValue("hello"));
    console.log(processValue(21));

    // Type guard with in operator
    interface Bird {
        fly(): void;
    }

    interface Fish {
        swim(): void;
    }

    function move(creature: Bird | Fish): void {
        if ("fly" in creature) {
            creature.fly();
        } else {
            creature.swim();
        }
    }

    move({ fly: () => console.log("Flying") });
    move({ swim: () => console.log("Swimming") });

    console.log("=== instanceof Complete ===");
}

runTests();