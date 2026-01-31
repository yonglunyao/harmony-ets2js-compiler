import { describe, it, expect, TestType, Level, Size } from '@ohos/hypium';
import { CryptographyUtil } from '../../../main/ets/services/CryptographyUtil';
import { Logger } from '../../../main/ets/utils/Logger';

const TAG = "UtilsTest";
function UtilsTest() {
  describe("CryptographyUtil Test", () => {
    it("cryptoTest_generateAESKey", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_base64Encode", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_base64Decode", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_sha256", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_md5", TestType.FUNCTION, /* arrow function */);
  });  describe("Logger Test", () => {
    it("loggerTest_info", TestType.FUNCTION, /* arrow function */);
    it("loggerTest_error", TestType.FUNCTION, /* arrow function */);
    it("loggerTest_success", TestType.FUNCTION, /* arrow function */);
    it("loggerTest_warn", TestType.FUNCTION, /* arrow function */);
    it("loggerTest_debug", TestType.FUNCTION, /* arrow function */);
  });}


//# sourceMappingURL=UtilsTest.test.js.map