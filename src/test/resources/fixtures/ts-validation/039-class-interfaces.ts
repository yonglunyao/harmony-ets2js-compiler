// 039-class-interfaces.ts
// Test class interfaces (runtime behavior without interface enforcement)
function runTests() {
    console.log("=== Class Interfaces ===");

    // Implement single interface (runtime duck typing)
    class File {
        constructor(content) {
            this.content = content;
        }

        read() {
            return this.content;
        }
    }

    const file = new File("Hello World");
    console.log("file=" + file.read());

    // Implement multiple interfaces (runtime duck typing)
    class Document {
        constructor() {
            this.data = "";
        }

        read() {
            return this.data;
        }

        write(data) {
            this.data = data;
        }
    }

    const doc = new Document();
    doc.write("Important content");
    console.log("doc=" + doc.read());

    // Interface with properties (runtime duck typing)
    class Product {
        constructor(name, id) {
            this.name = name;
            this._id = id;
        }

        getId() {
            return this._id;
        }
    }

    const product = new Product("Widget", 123);
    console.log("product=" + product.name + "," + product.getId());

    // Interface inheritance (runtime duck typing)
    class ColoredCircle {
        constructor(color, radius) {
            this.color = color;
            this.radius = radius;
        }

        getArea() {
            return Math.PI * this.radius * this.radius;
        }

        describe() {
            return this.color + " circle with area " + this.getArea();
        }
    }

    const coloredCircle = new ColoredCircle("red", 5);
    console.log("colored=" + coloredCircle.describe());

    // Class as interface (runtime duck typing)
    class Point {
        constructor(x, y) {
            this.x = x;
            this.y = y;
        }
    }

    const point3d = { x: 1, y: 2, z: 3 };
    console.log("point3d=" + point3d.x + "," + point3d.y + "," + point3d.z);

    console.log("=== Class Interfaces Complete ===");
}

runTests();
