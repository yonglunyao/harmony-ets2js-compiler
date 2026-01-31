export class SpreadTest {
    static testArraySpread(): number[] {
        const arr1 = [1, 2, 3];
        const arr2 = [...arr1, 4, 5];
        return arr2;
    }

    static testObjectSpread(): object {
        const obj1 = { a: 1, b: 2 };
        const obj2 = { ...obj1, c: 3 };
        return obj2;
    }

    static testSpreadInCall(): number {
        const arr = [1, 2, 3];
        return Math.max(...arr);
    }
}
