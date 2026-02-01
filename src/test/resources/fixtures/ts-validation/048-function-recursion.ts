// 048-function-recursion.ts
// Test recursive functions
function runTests() {
    console.log("=== Recursive Functions ===");

    // Basic recursion - factorial
    function factorial(n: number): number {
        if (n <= 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }
    console.log("factorial=" + factorial(5));

    // Recursion - fibonacci
    function fibonacci(n: number): number {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    console.log("fib=" + fibonacci(7));

    // Recursion - sum array
    function sumArray(arr: number[], index: number = 0): number {
        if (index >= arr.length) {
            return 0;
        }
        return arr[index] + sumArray(arr, index + 1);
    }
    console.log("sum=" + sumArray([1, 2, 3, 4, 5]));

    // Recursion - reverse string
    function reverseString(str: string): string {
        if (str.length <= 1) {
            return str;
        }
        return reverseString(str.slice(1)) + str[0];
    }
    console.log("reverse=" + reverseString("hello"));

    // Recursion - power
    function power(base: number, exp: number): number {
        if (exp === 0) {
            return 1;
        }
        return base * power(base, exp - 1);
    }
    console.log("power=" + power(2, 5));

    // Recursion - binary search
    function binarySearch(arr: number[], target: number, left: number = 0, right: number = arr.length - 1): number {
        if (left > right) {
            return -1;
        }
        const mid = Math.floor((left + right) / 2);
        if (arr[mid] === target) {
            return mid;
        } else if (arr[mid] < target) {
            return binarySearch(arr, target, mid + 1, right);
        } else {
            return binarySearch(arr, target, left, mid - 1);
        }
    }
    const sorted = [1, 3, 5, 7, 9, 11, 13];
    console.log("search=" + binarySearch(sorted, 7));

    // Recursion - GCD
    function gcd(a: number, b: number): number {
        if (b === 0) {
            return a;
        }
        return gcd(b, a % b);
    }
    console.log("gcd=" + gcd(48, 18));

    // Recursion - tree depth simulation
    interface INode {
        value: number;
        children?: INode[];
    }

    function treeDepth(node: INode): number {
        if (!node.children || node.children.length === 0) {
            return 1;
        }
        return 1 + Math.max(...node.children.map(treeDepth));
    }

    const tree: INode = {
        value: 1,
        children: [
            { value: 2, children: [{ value: 4 }, { value: 5 }] },
            { value: 3 }
        ]
    };
    console.log("depth=" + treeDepth(tree));

    // Tail recursion - sum
    function tailSum(n: number, acc: number = 0): number {
        if (n === 0) {
            return acc;
        }
        return tailSum(n - 1, acc + n);
    }
    console.log("tailSum=" + tailSum(5));

    // Mutual recursion
    function isEven(n: number): boolean {
        if (n === 0) {
            return true;
        }
        return isOdd(n - 1);
    }

    function isOdd(n: number): boolean {
        if (n === 0) {
            return false;
        }
        return isEven(n - 1);
    }
    console.log("even=" + isEven(4));
    console.log("odd=" + isOdd(5));

    console.log("=== Recursive Functions Complete ===");
}

runTests();