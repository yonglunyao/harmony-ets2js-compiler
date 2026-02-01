// 033-class-modifiers.ts
// Test class modifiers
function runTests() {
    console.log("=== Class Modifiers ===");

    // Public (default)
    class PublicExample {
        public publicProp: string;
        constructor(publicProp: string) {
            this.publicProp = publicProp;
        }
        public publicMethod(): string {
            return "public";
        }
    }

    const pub = new PublicExample("test");
    console.log("public=" + pub.publicProp);
    console.log("publicMethod=" + pub.publicMethod());

    // Private (runtime closure simulation)
    class PrivateExample {
        private _privateProp: string;
        constructor(value: string) {
            this._privateProp = value;
        }
        public getPrivateValue(): string {
            return this._privateProp;
        }
        private privateMethod(): string {
            return "private";
        }
        public callPrivate(): string {
            return this.privateMethod();
        }
    }

    const priv = new PrivateExample("secret");
    console.log("viaAccessor=" + priv.getPrivateValue());
    console.log("viaPublic=" + priv.callPrivate());

    // Protected
    class BaseClass {
        protected protectedProp: string = "protected";
        protected protectedMethod(): string {
            return "protected method";
        }
    }

    class DerivedClass extends BaseClass {
        public accessProtected(): string {
            return this.protectedProp + " " + this.protectedMethod();
        }
    }

    const derived = new DerivedClass();
    console.log("protectedAccess=" + derived.accessProtected());

    // Readonly
    class ReadonlyExample {
        readonly readonlyProp: string;
        readonly readonlyNum: number;

        constructor() {
            this.readonlyProp = "cannot change";
            this.readonlyNum = 42;
        }

        getReadonly(): string {
            return this.readonlyProp + " " + this.readonlyNum;
        }
    }

    const ro = new ReadonlyExample();
    console.log("readonly=" + ro.getReadonly());

    // Static
    class StaticExample {
        static staticProp: string = "static";
        private static count: number = 0;

        constructor() {
            StaticExample.count++;
        }

        public static getStatic(): string {
            return this.staticProp;
        }

        public static getCount(): number {
            return this.count;
        }
    }

    console.log("staticProp=" + StaticExample.staticProp);
    console.log("staticMethod=" + StaticExample.getStatic());
    new StaticExample();
    new StaticExample();
    console.log("count=" + StaticExample.getCount());

    console.log("=== Class Modifiers Complete ===");
}

runTests();