import { describe, it, expect, TestType, Level, Size, beforeAll, afterEach } from '@ohos/hypium';
import { abilityDelegatorRegistry, Driver, ON, Component } from '@kit.TestKit';
import { Want } from '@kit.AbilityKit';
import { TestHelper } from './TestHelper';

const TAG = "FileSystemPageUITest";
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;
function FileSystemPageUITest() {
  describe("FileSystemPageUITest", () => {
    const driver;
    beforeAll(/* arrow function */);
    afterEach(/* arrow function */);
    it("fileTest_navigateToPage", TestType.FUNCTION, /* arrow function */);
    it("fileTest_verifyBrowseTab", TestType.FUNCTION, /* arrow function */);
    it("fileTest_verifyOperationTab", TestType.FUNCTION, /* arrow function */);
    it("fileTest_verifyInfoTab", TestType.FUNCTION, /* arrow function */);
    it("fileTest_cycleAllSubTabs", TestType.FUNCTION, /* arrow function */);
    it("fileTest_verifyRefreshButton", TestType.FUNCTION, /* arrow function */);
    it("fileTest_verifyCreateFolderButton", TestType.FUNCTION, /* arrow function */);
  });}


//# sourceMappingURL=FileSystemPageUITest.test.js.map