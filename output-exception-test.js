export class ExceptionTest {
  static testTryCatch() {
    try {
  return "success";
} catch (e) {
  return "error: " + e;
}
  }

  static testTryFinally() {
    try {
  return "try";
} finally {

}
  }

  static testTryCatchFinally() {
    try {
  return "try";
} catch (e) {
  return "catch";
} finally {
  return "finally";
}
  }

}


//# sourceMappingURL=output-exception-test.js.map