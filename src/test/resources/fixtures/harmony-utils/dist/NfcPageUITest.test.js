import { describe, it, expect, TestType, Level, Size, beforeAll, afterEach } from '@ohos/hypium';
import { abilityDelegatorRegistry, Driver, ON, Component } from '@kit.TestKit';
import { Want } from '@kit.AbilityKit';
import { TestHelper } from './TestHelper';

const TAG = "NfcPageUITest";
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;
function NfcPageUITest() {
  describe("NfcPageUITest", () => {
    const driver;
    beforeAll(/* arrow function */);
    afterEach(/* arrow function */);
    it("nfcTest_navigateToPage", TestType.FUNCTION, /* arrow function */);
    it("nfcTest_verifyNfcStatus", TestType.FUNCTION, /* arrow function */);
    it("nfcTest_verifyEnableNfcButton", TestType.FUNCTION, /* arrow function */);
    it("nfcTest_verifyDisableNfcButton", TestType.FUNCTION, /* arrow function */);
    it("nfcTest_verifyListenButtons", TestType.FUNCTION, /* arrow function */);
    it("nfcTest_verifyUsageTips", TestType.FUNCTION, /* arrow function */);
  });}


//# sourceMappingURL=NfcPageUITest.test.js.map