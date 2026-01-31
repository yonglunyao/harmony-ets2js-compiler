import { describe, it, expect, TestType, Level, Size, beforeAll, afterEach } from '@ohos/hypium';
import { abilityDelegatorRegistry, Driver, ON } from '@kit.TestKit';
import { Want } from '@kit.AbilityKit';
import { TestHelper } from './TestHelper';

const TAG = "MainPageUITest";
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;
function MainPageUITest() {
  describe("MainPageUITest", () => {
    const driver;
    beforeAll(/* arrow function */);
    afterEach(/* arrow function */);
    it("homeTest_verifyTitle", TestType.FUNCTION, /* arrow function */);
    it("homeTest_verifyFeatureCards", TestType.FUNCTION, /* arrow function */);
    it("homeTest_switchToImagePage", TestType.FUNCTION, /* arrow function */);
    it("homeTest_switchToCryptoPage", TestType.FUNCTION, /* arrow function */);
    it("homeTest_switchToFilePage", TestType.FUNCTION, /* arrow function */);
    it("homeTest_switchToVideoPage", TestType.FUNCTION, /* arrow function */);
    it("homeTest_switchToNfcPage", TestType.FUNCTION, /* arrow function */);
    it("homeTest_verifyTips", TestType.FUNCTION, /* arrow function */);
    it("homeTest_cycleAllTabs", TestType.FUNCTION, /* arrow function */);
  });}


//# sourceMappingURL=MainPageUITest.test.js.map