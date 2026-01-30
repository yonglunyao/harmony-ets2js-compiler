export class SwitchTest {
  static testSwitch(value /* number */) {
    switch (value) {
  case 1:
    return "one";
    break;
  case 2:
    return "two";
    break;
  default:
    return "other";
}
  }

  static testSwitchWithString(value /* string */) {
    switch (value) {
  case "one":
    return 1;
    break;
  case "two":
    return 2;
    break;
  default:
    return 0;
}
  }

}


//# sourceMappingURL=output-switch-test.js.map