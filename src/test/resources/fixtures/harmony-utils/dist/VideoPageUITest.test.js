import { describe, it, expect, TestType, Level, Size, beforeAll, afterEach } from '@ohos/hypium';
import { abilityDelegatorRegistry, Driver, ON, Component } from '@kit.TestKit';
import { Want } from '@kit.AbilityKit';

const TAG = "VideoPageUITest";
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;
function VideoPageUITest() {
  describe("VideoPageUITest", () => {
    const driver;
    beforeAll(/* arrow function */);
    afterEach(/* arrow function */);
    it("videoTest_navigateToPage", TestType.FUNCTION, /* arrow function */);
    it("videoTest_verifySelectVideoButton", TestType.FUNCTION, /* arrow function */);
    it("videoTest_verifyVideoInfoArea", TestType.FUNCTION, /* arrow function */);
    it("videoTest_verifyDurationArea", TestType.FUNCTION, /* arrow function */);
    it("videoTest_verifyCaptureArea", TestType.FUNCTION, /* arrow function */);
    it("videoTest_verifyFormatInfoArea", TestType.FUNCTION, /* arrow function */);
  });}


//# sourceMappingURL=VideoPageUITest.test.js.map