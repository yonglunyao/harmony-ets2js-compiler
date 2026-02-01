// 078-proxies.ts
// Test Proxy
function runTests() {
    console.log("=== Proxies ===");

    // Basic proxy
    const target = {
        name: "John",
        age: 30
    };

    const proxy = new Proxy(target, {
        get(trapTarget, prop) {
            console.log("get=" + String(prop));
            return trapTarget[prop];
        },
        set(trapTarget, prop, value) {
            console.log("set=" + String(prop) + "=" + value);
            trapTarget[prop] = value;
            return true;
        }
    });

    console.log("name=" + proxy.name);
    proxy.age = 31;

    // Proxy for validation
    const validator = new Proxy(
        { value: 0 },
        {
            set(trapTarget, prop, value) {
                if (prop === "value" && typeof value !== "number") {
                    console.log("invalid");
                    return false;
                }
                trapTarget[prop] = value;
                return true;
            }
        }
    );
    validator.value = 10;
    console.log("valid=" + validator.value);

    // Proxy with has trap
    const hasProxy = new Proxy(
        { a: 1, b: 2 },
        {
            has(trapTarget, prop) {
                console.log("has=" + String(prop));
                return prop in trapTarget;
            }
        }
    );
    console.log("hasA=" + ("a" in hasProxy));

    // Proxy with delete trap
    const deleteProxy = new Proxy(
        { x: 1, y: 2 },
        {
            deleteProperty(trapTarget, prop) {
                console.log("delete=" + String(prop));
                delete trapTarget[prop];
                return true;
            }
        }
    );
    delete deleteProxy.x;

    // Proxy with ownKeys trap
    const keysProxy = new Proxy(
        { a: 1, b: 2, c: 3 },
        {
            ownKeys(trapTarget) {
                console.log("ownKeys");
                return Object.keys(trapTarget);
            }
        }
    );
    console.log("keys=" + Object.keys(keysProxy).join(","));

    // Proxy with getOwnPropertyDescriptor
    const descProxy = new Proxy(
        { name: "test" },
        {
            getOwnPropertyDescriptor(trapTarget, prop) {
                console.log("desc=" + String(prop));
                return Object.getOwnPropertyDescriptor(trapTarget, prop);
            }
        }
    );
    const desc = Object.getOwnPropertyDescriptor(descProxy, "name");
    console.log("descValue=" + desc?.value);

    // Revocable proxy
    const revocable = Proxy.revocable({ data: "value" }, {});
    const revProxy = revocable.proxy;
    console.log("beforeRevoke=" + revProxy.data);
    revocable.revoke();
    console.log("revoked=" + (revProxy.data === undefined));

    console.log("=== Proxies Complete ===");
}

runTests();
