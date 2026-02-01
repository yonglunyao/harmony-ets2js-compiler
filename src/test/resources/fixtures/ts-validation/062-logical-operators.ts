// 062-logical-operators.ts
// Test logical operators
function runTests() {
    console.log("=== Logical Operators ===");

    // Logical AND
    const and1 = true && true;
    const and2 = true && false;
    console.log("and=" + and1 + "," + and2);

    // Logical OR
    const or1 = true || false;
    const or2 = false || false;
    console.log("or=" + or1 + "," + or2);

    // Logical NOT
    const not1 = !true;
    const not2 = !false;
    console.log("not=" + not1 + "," + not2);

    // Short-circuit AND
    const shortAnd = false && console.log("unreachable");
    console.log("shortAnd=" + shortAnd);

    // Short-circuit OR
    const shortOr = true || console.log("unreachable");
    console.log("shortOr=" + shortOr);

    // AND with truthy/falsy
    const truthy1 = 5 && 10;
    const truthy2 = "hello" && "world";
    const falsy = 0 && "unreached";
    console.log("truthy=" + truthy1 + "," + truthy2);
    console.log("falsy=" + falsy);

    // OR with default
    const def1 = 0 || 5;
    const def2 = "" || "default";
    const def3 = "value" || "default";
    console.log("default=" + def1 + "," + def2 + "," + def3);

    // Double negation
    const bool1 = !!5;
    const bool2 = !!0;
    const bool3 = !!"hello";
    console.log("doubleNot=" + bool1 + "," + bool2 + "," + bool3);

    // Complex logical expressions
    const complex1 = (5 > 3) && (10 < 20);
    const complex2 = (5 < 3) || (10 > 20);
    const complex3 = !(5 === 5);
    console.log("complex=" + complex1 + "," + complex2 + "," + complex3);

    // AND for conditional execution
    let executed = false;
    true && (executed = true);
    console.log("executed=" + executed);

    // OR for fallback
    const config: any = { setting: null };
    const value = config.setting || config.default || "fallback";
    console.log("fallback=" + value);

    // Nullish coalescing (similar to OR but only for null/undefined)
    const nullish1 = null ?? "default";
    const nullish2 = undefined ?? "default";
    const nullish3 = 0 ?? "default";
    const nullish4 = "" ?? "default";
    console.log("nullish=" + nullish1 + "," + nullish2 + "," + nullish3 + "," + nullish4);

    console.log("=== Logical Operators Complete ===");
}

runTests();