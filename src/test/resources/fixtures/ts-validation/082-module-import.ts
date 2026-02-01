// 082-module-import.ts
// Test module imports
function runTests() {
    console.log("=== Module Imports ===");

    // Note: These are example imports - actual modules would need to exist
    // This file demonstrates the syntax patterns

    // Default import
    // import myFunction from "./myModule";

    // Named imports
    // import { add, subtract } from "./math";

    // Import with rename
    // import { add as sum } from "./math";

    // Import all as namespace
    // import * as math from "./math";

    // Import side effect only
    // import "./polyfills";

    // Import both default and named
    // import myDefault, { named1, named2 } from "./myModule";

    // Import type
    // import type { IUser } from "./types";

    // Import with type assertion
    // import { IUser } from "./types";

    // Dynamic import
    async function dynamicImport(): Promise<void> {
        // const module = await import("./dynamicModule");
        // module.default();
    }
    dynamicImport();

    // Re-export patterns
    // export { add } from "./math";
    // export { default } from "./defaultExport";
    // export * as utils from "./utils";

    // CommonJS compatibility
    // import = require("commonjs-module");

    // Import assertions (TypeScript 4.5+)
    // import data from "./data.json" assert { type: "json" };

    // Import for types only
    // import type { MyInterface } from "./interfaces";

    console.log("importTypes=demo");
    console.log("namedImports=demo");
    console.log("defaultImport=demo");
    console.log("namespaceImport=demo");
    console.log("dynamicImport=demo");
    console.log("typeImport=demo");

    console.log("=== Module Imports Complete ===");
}

runTests();