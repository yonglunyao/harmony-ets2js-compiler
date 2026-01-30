import { Driver } from '@kit.TestKit';

export class TestHelper {
  driver = null;

  static getDriver() {
    if (!TestHelper.driver) {
      TestHelper.driver = Driver.create();
    }
    return TestHelper.driver;
  }

  static resetDriver() {
    TestHelper.driver = null;
  }

}


//# sourceMappingURL=TestHelper.js.map