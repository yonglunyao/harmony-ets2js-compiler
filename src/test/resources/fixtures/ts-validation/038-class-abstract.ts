// 038-class-abstract.ts
// Test abstract classes (runtime simulation)
function runTests() {
    console.log("=== Abstract Classes ===");

    // Abstract class simulation (using regular class)
    class Shape {
        describe() {
            return "Shape with area " + this.getArea();
        }

        isLargerThan(other) {
            return this.getArea() > other.getArea();
        }
    }

    // Concrete implementation
    class Rectangle extends Shape {
        constructor(width, height) {
            super();
            this.width = width;
            this.height = height;
        }

        getArea() {
            return this.width * this.height;
        }

        getPerimeter() {
            return 2 * (this.width + this.height);
        }
    }

    class Circle extends Shape {
        constructor(radius) {
            super();
            this.radius = radius;
        }

        getArea() {
            return Math.PI * this.radius * this.radius;
        }

        getPerimeter() {
            return 2 * Math.PI * this.radius;
        }
    }

    const rect = new Rectangle(5, 10);
    const circle = new Circle(7);

    console.log("rectArea=" + rect.getArea());
    console.log("rectPerim=" + rect.getPerimeter());
    console.log("rectDesc=" + rect.describe());

    console.log("circleArea=" + circle.getArea());
    console.log("circlePerim=" + circle.getPerimeter());
    console.log("circleDesc=" + circle.describe());

    console.log("isLarger=" + rect.isLargerThan(circle));

    // Abstract with partial implementation
    class Animal {
        constructor(name) {
            this.name = name;
        }

        eat() {
            console.log(this.name + " is eating");
        }

        sleep() {
            console.log(this.name + " is sleeping");
        }
    }

    class Dog extends Animal {
        makeSound() {
            console.log(this.name + " barks");
        }
    }

    class Cat extends Animal {
        makeSound() {
            console.log(this.name + " meows");
        }
    }

    const dog = new Dog("Buddy");
    const cat = new Cat("Whiskers");

    dog.eat();
    dog.makeSound();
    dog.sleep();

    cat.eat();
    cat.makeSound();
    cat.sleep();

    console.log("=== Abstract Classes Complete ===");
}

runTests();
