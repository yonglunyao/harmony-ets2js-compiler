import { describe, it, expect, TestType, Level, Size, beforeAll, afterEach } from '@ohos/hypium';
import { abilityDelegatorRegistry, Driver, ON, Component } from '@kit.TestKit';
import { Want } from '@kit.AbilityKit';
import { TestHelper } from './TestHelper';

const TAG = "Cryptography Page UI Test";
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;
function CryptographyPageUITest() {
  describe("CryptographyPageUITest", () => {
    const driver;
    beforeAll(/* arrow function */);
    afterEach(/* arrow function */);
    it("cryptoTest_navigateToPage", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_verifyAESInputs", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_verifyAESButtons", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_switchToHashTab", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_switchToBase64Tab", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_cycleAllSubTabs", TestType.FUNCTION, /* arrow function */);
    it("cryptoTest_verifyResultArea", TestType.FUNCTION, /* arrow function */);
  });}


//# sourceMappingURL=CryptographyPageUITest.test.js.map