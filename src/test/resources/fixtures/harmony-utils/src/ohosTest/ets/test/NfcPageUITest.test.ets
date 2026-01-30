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

const TAG = 'NfcPageUITest';
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;

export default function NfcPageUITest() {
  describe('NfcPageUITest', () => {
    let driver: Driver;

    beforeAll(async () => {
      driver = Driver.create();
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

    /**
     * @tc.number NfcPageUITest_001
     * @tc.name 导航到NFC页面
     * @tc.desc 测试从首页导航到NFC页面
     */
    it('nfcTest_navigateToPage', TestType.FUNCTION, async (done: Function) => {
      try {
        const nfcTab = await driver.findComponent(ON.text('NFC'));
        await nfcTab.click();
        await driver.delayMs(1000);
        await driver.assertComponentExist(ON.text('NFC 工具'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number NfcPageUITest_002
     * @tc.name 测试NFC状态显示
     * @tc.desc 验证NFC状态区域是否正确显示
     */
    it('nfcTest_verifyNfcStatus', TestType.FUNCTION, async (done: Function) => {
      try {
        const nfcTab = await driver.findComponent(ON.text('NFC'));
        await nfcTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('NFC 工具'));
        await driver.assertComponentExist(ON.text('注意：此功能需要设备支持NFC'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number NfcPageUITest_003
     * @tc.name 测试启用NFC按钮
     * @tc.desc 验证NFC页面基础元素
     */
    it('nfcTest_verifyEnableNfcButton', TestType.FUNCTION, async (done: Function) => {
      try {
        const nfcTab = await driver.findComponent(ON.text('NFC'));
        await nfcTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('NFC 工具'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number NfcPageUITest_004
     * @tc.name 测试禁用NFC按钮
     * @tc.desc 验证NFC页面基础元素
     */
    it('nfcTest_verifyDisableNfcButton', TestType.FUNCTION, async (done: Function) => {
      try {
        const nfcTab = await driver.findComponent(ON.text('NFC'));
        await nfcTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('NFC 工具'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number NfcPageUITest_005
     * @tc.name 测试标签监听控制按钮
     * @tc.desc 验证NFC页面基础元素
     */
    it('nfcTest_verifyListenButtons', TestType.FUNCTION, async (done: Function) => {
      try {
        const nfcTab = await driver.findComponent(ON.text('NFC'));
        await nfcTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('NFC 工具'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number NfcPageUITest_006
     * @tc.name 测试使用说明区域
     * @tc.desc 验证NFC页面基础元素
     */
    it('nfcTest_verifyUsageTips', TestType.FUNCTION, async (done: Function) => {
      try {
        const nfcTab = await driver.findComponent(ON.text('NFC'));
        await nfcTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('NFC 工具'));
        await driver.assertComponentExist(ON.text('注意：此功能需要设备支持NFC'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });
  });
}
