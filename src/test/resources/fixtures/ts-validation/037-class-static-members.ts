// 037-class-static-members.ts
// Test class static members
function runTests() {
    console.log("=== Class Static Members ===");

    // Static properties
    class Counter {
        static getCount() {
            return this.count;
        }

        static reset() {
            this.count = 0;
        }
    }
    Counter.count = 0;

    console.log("initial=" + Counter.getCount());
    new Counter();
    new Counter();
    new Counter();
    Counter.count += 3;
    console.log("after3=" + Counter.getCount());
    Counter.reset();
    console.log("reset=" + Counter.getCount());

    // Static initialization
    class Constants {
        static circleArea(radius) {
            return this.PI * radius * radius;
        }
    }
    Constants.PI = 3.14159;
    Constants.E = 2.71828;
    Constants.GOLDEN_RATIO = 1.618;

    console.log("PI=" + Constants.PI);
    console.log("E=" + Constants.E);
    console.log("area=" + Constants.circleArea(5));

    // Static class for utilities
    class StringUtils {
        static capitalize(str) {
            return str.charAt(0).toUpperCase() + str.slice(1);
        }

        static reverse(str) {
            return str.split("").reverse().join("");
        }

        static truncate(str, maxLength) {
            return str.length > maxLength ? str.slice(0, maxLength) + "..." : str;
        }

        static isEmpty(str) {
            return str.length === 0;
        }
    }

    console.log("capitalize=" + StringUtils.capitalize("hello"));
    console.log("reverse=" + StringUtils.reverse("world"));
    console.log("truncate=" + StringUtils.truncate("This is a long string", 10));
    console.log("empty=" + StringUtils.isEmpty(""));

    // Static with inheritance
    class BaseStatic {
        static staticMethod() {
            return "base method";
        }
    }
    BaseStatic.staticProp = "base";

    class DerivedStatic extends BaseStatic {
        static staticMethod() {
            return "derived overridden";
        }
    }
    DerivedStatic.staticProp = "derived";

    console.log("baseStatic=" + BaseStatic.staticProp);
    console.log("derivedStatic=" + DerivedStatic.staticProp);
    console.log("derivedMethod=" + DerivedStatic.staticMethod());

    // Static factory methods
    class User {
        constructor(name, email) {
            this.name = name;
            this.email = email;
        }

        static fromJSON(json) {
            const data = JSON.parse(json);
            return new User(data.name, data.email);
        }

        static createGuest() {
            return new User("Guest", "guest@example.com");
        }

        toJSON() {
            return JSON.stringify({ name: this.name, email: this.email });
        }
    }

    const user1 = User.createGuest();
    console.log("guest=" + user1.name);

    const userJson = '{"name":"John","email":"john@example.com"}';
    const user2 = User.fromJSON(userJson);
    console.log("fromJSON=" + user2.name);

    console.log("=== Class Static Members Complete ===");
}

runTests();
