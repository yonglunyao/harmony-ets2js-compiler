import { describe, it, expect, TestType, Level, Size, beforeAll, afterEach } from '@ohos/hypium';
import { abilityDelegatorRegistry, Driver, ON, Component } from '@kit.TestKit';
import { Want } from '@kit.AbilityKit';
import { TestHelper } from './TestHelper';

const TAG = "ImagePageUITest";
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;
function ImagePageUITest() {
  describe("ImagePageUITest", () => {
    const driver;
    beforeAll(/* arrow function */);
    afterEach(/* arrow function */);
    it("imageTest_navigateToPage", TestType.FUNCTION, /* arrow function */);
    it("imageTest_verifyCompressTab", TestType.FUNCTION, /* arrow function */);
    it("imageTest_verifyCropTab", TestType.FUNCTION, /* arrow function */);
    it("imageTest_verifyRotateTab", TestType.FUNCTION, /* arrow function */);
    it("imageTest_verifyFilterTab", TestType.FUNCTION, /* arrow function */);
    it("imageTest_cycleAllSubTabs", TestType.FUNCTION, /* arrow function */);
    it("imageTest_verifySelectImageButton", TestType.FUNCTION, /* arrow function */);
  });}


//# sourceMappingURL=ImagePageUITest.test.js.map