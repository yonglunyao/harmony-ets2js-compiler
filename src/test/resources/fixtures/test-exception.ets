export class ExceptionTest {
    static testTryCatch(): string {
        try {
            return "success";
        } catch (e) {
            return "error: " + e;
        }
    }

    static testTryFinally(): string {
        try {
            return "try";
        } finally {
            // cleanup
        }
    }

    static testTryCatchFinally(): string {
        try {
            return "try";
        } catch (e) {
            return "catch";
        } finally {
            return "finally";
        }
    }
}
