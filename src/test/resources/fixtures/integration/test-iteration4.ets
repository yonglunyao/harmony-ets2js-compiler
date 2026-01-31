/**
 * Test file for Iteration 4 features
 * Tests: object literals, new expressions, template strings, dynamic import, interface, etc.
 */

// P0: Object literal expression
const testObjectLiteral1 = { data: "test", value: 123 };
convertKey({ data: "keyData" });

// P0: NewExpression
const encoder = new util.TextEncoder();
const buffer = new Uint8Array(16);

// P0: Template strings
const message = "Hello";
const error = new Error("test");
const result = `${message}: ${typeof error === 'string' ? error : error.message}`;
const simple = `simple template`;

// P1: Type annotations (should be removed in output)
const files: FileInfo[] = [];
const name: string = "test";
let count: number = 0;

// P1: Octal literals
const flags = 0o1;

// P2: Dynamic import
const nfcModule = await import('@ohos.nfc');

// P2: Interface declaration (should not generate runtime code)
interface FileInfo {
  name: string;
  size: number;
}

// P2: Type assertion (as)
const businessError: Error = new Error("test");
Logger.error('operation failed', businessError as BusinessError);

// Helper functions
function convertKey(obj: Object): string {
  return JSON.stringify(obj);
}

// P1: @Entry decorator
@Entry
@Component
struct TestPage {
  build() {
    Column() {
      Text("Entry Test");
    }
  }
}

@Component
struct Column {
  build() {
  }
}

@Component
struct Text {
  constructor(text: string) {
    this.text = text;
  }
  private text: string;
}

class Logger {
  static error(message: string, error: Error) {
    console.error(message, error);
  }
}

class BusinessError extends Error {
  code: number;
}

class util {
  static TextEncoder() {
    return new TextEncoder();
  }
}

class TextEncoder {
}

class Uint8Array {
  constructor(size: number) {
  }
}
