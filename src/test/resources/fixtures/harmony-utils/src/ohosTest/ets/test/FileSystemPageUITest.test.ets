/*
 * Copyright (c) 2024 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { describe, it, expect, TestType, Level, Size, beforeAll, afterEach } from '@ohos/hypium';
import { abilityDelegatorRegistry, Driver, ON, Component } from '@kit.TestKit';
import { Want } from '@kit.AbilityKit';
import { TestHelper } from './TestHelper';

const TAG = 'FileSystemPageUITest';
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;

export default function FileSystemPageUITest() {
  describe('FileSystemPageUITest', () => {
    let driver: Driver;

    beforeAll(async () => {
      driver = TestHelper.getDriver();
      const want: Want = {
        bundleName: bundleName,
        abilityName: 'EntryAbility'
      };
      await delegator.startAbility(want);
      await driver.delayMs(2000);
    });

    afterEach(async () => {
      // Navigate back to home page by pressing home tab
      try {
        const homeTab = await driver.findComponent(ON.text('首页'));
        await homeTab.click();
        await driver.delayMs(500);
      } catch (e) {
        // If home tab not found, press back
        await driver.pressBack();
        await driver.pressBack();
        await driver.delayMs(500);
      }
    });

    it('fileTest_navigateToPage', TestType.FUNCTION, async (done: Function) => {
      try {
        const fileTab = await driver.findComponent(ON.text('文件'));
        await fileTab.click();
        await driver.delayMs(1000);
        await driver.assertComponentExist(ON.text('文件浏览器'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    it('fileTest_verifyBrowseTab', TestType.FUNCTION, async (done: Function) => {
      try {
        const fileTab = await driver.findComponent(ON.text('文件'));
        await fileTab.click();
        await driver.delayMs(1000);
        await driver.assertComponentExist(ON.text('文件浏览器'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    it('fileTest_verifyOperationTab', TestType.FUNCTION, async (done: Function) => {
      try {
        const fileTab = await driver.findComponent(ON.text('文件'));
        await fileTab.click();
        await driver.delayMs(1000);
        const operationTab = await driver.findComponent(ON.text('操作'));
        await operationTab.click();
        await driver.delayMs(500);
        await driver.assertComponentExist(ON.text('文件操作'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    it('fileTest_verifyInfoTab', TestType.FUNCTION, async (done: Function) => {
      try {
        const fileTab = await driver.findComponent(ON.text('文件'));
        await fileTab.click();
        await driver.delayMs(1000);
        const infoTab = await driver.findComponent(ON.text('信息'));
        await infoTab.click();
        await driver.delayMs(500);
        await driver.assertComponentExist(ON.text('文件信息'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    it('fileTest_cycleAllSubTabs', TestType.FUNCTION, async (done: Function) => {
      try {
        const fileTab = await driver.findComponent(ON.text('文件'));
        await fileTab.click();
        await driver.delayMs(1000);
        const subTabs = ['操作', '信息'];
        for (const tabName of subTabs) {
          const tab = await driver.findComponent(ON.text(tabName));
          await tab.click();
          await driver.delayMs(500);
        }
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    it('fileTest_verifyRefreshButton', TestType.FUNCTION, async (done: Function) => {
      try {
        const fileTab = await driver.findComponent(ON.text('文件'));
        await fileTab.click();
        await driver.delayMs(1000);
        await driver.assertComponentExist(ON.text('刷新'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    it('fileTest_verifyCreateFolderButton', TestType.FUNCTION, async (done: Function) => {
      try {
        const fileTab = await driver.findComponent(ON.text('文件'));
        await fileTab.click();
        await driver.delayMs(1000);
        await driver.assertComponentExist(ON.text('新建文件夹'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });
  });
}
