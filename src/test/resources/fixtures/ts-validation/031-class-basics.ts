// 031-class-basics.ts
// Test class basics
function runTests() {
    console.log("=== Class Basics ===");

    // Basic class
    class Person {
        constructor(name, age) {
            this.name = name;
            this.age = age;
        }

        greet() {
            console.log("Hi, I am " + this.name);
        }

        getBirthYear() {
            const currentYear = 2024;
            return currentYear - this.age;
        }
    }

    const person1 = new Person("John", 30);
    console.log("name=" + person1.name);
    console.log("age=" + person1.age);
    person1.greet();
    console.log("birthYear=" + person1.getBirthYear());

    // Property initialization
    class Point {
        constructor() {
            this.x = 0;
            this.y = 0;
        }

        move(dx, dy) {
            this.x += dx;
            this.y += dy;
        }

        toString() {
            return "(" + this.x + "," + this.y + ")";
        }
    }

    const point = new Point();
    point.move(5, 3);
    console.log("point=" + point.toString());

    // Class with readonly (simulated with convention)
    class Circle {
        constructor(radius) {
            this.radius = radius;
        }

        getArea() {
            return Math.PI * this.radius * this.radius;
        }
    }

    const circle = new Circle(5);
    console.log("area=" + circle.getArea());

    // Class expression
    const MyClass = class {
        constructor() {
            this.value = "anonymous";
        }
    };
    const anon = new MyClass();
    console.log("anon=" + anon.value);

    console.log("=== Class Basics Complete ===");
}

runTests();
