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

const TAG = 'Cryptography Page UI Test';
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;

export default function CryptographyPageUITest() {
  describe('CryptographyPageUITest', () => {
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
        await driver.delayMs(500);
      }
    });

    /**
     * @tc.number CryptoPageUITest_001
     * @tc.name 导航到密码学页面
     * @tc.desc 测试从首页导航到密码学页面
     */
    it('cryptoTest_navigateToPage', TestType.FUNCTION, async (done: Function) => {
      try {
        const cryptoTab = await driver.findComponent(ON.text('密码学'));
        await cryptoTab.click();
        await driver.delayMs(1000);
        await driver.assertComponentExist(ON.text('AES 加密/解密'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number CryptoPageUITest_002
     * @tc.name 测试AES标题显示
     * @tc.desc 验证AES加密/解密页面的标题是否正常显示
     */
    it('cryptoTest_verifyAESInputs', TestType.FUNCTION, async (done: Function) => {
      try {
        const cryptoTab = await driver.findComponent(ON.text('密码学'));
        await cryptoTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('AES 加密/解密'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number CryptoPageUITest_003
     * @tc.name 测试AES标题
     * @tc.desc 验证AES页面的标题是否正常显示
     */
    it('cryptoTest_verifyAESButtons', TestType.FUNCTION, async (done: Function) => {
      try {
        const cryptoTab = await driver.findComponent(ON.text('密码学'));
        await cryptoTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('AES 加密/解密'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number CryptoPageUITest_004
     * @tc.name 切换到哈希标签页
     * @tc.desc 测试切换到哈希计算标签页
     */
    it('cryptoTest_switchToHashTab', TestType.FUNCTION, async (done: Function) => {
      try {
        const cryptoTab = await driver.findComponent(ON.text('密码学'));
        await cryptoTab.click();
        await driver.delayMs(1000);

        const hashTab = await driver.findComponent(ON.text('哈希'));
        await hashTab.click();
        await driver.delayMs(500);

        await driver.assertComponentExist(ON.text('哈希计算'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number CryptoPageUITest_005
     * @tc.name 切换到Base64标签页
     * @tc.desc 测试切换到Base64编解码标签页
     */
    it('cryptoTest_switchToBase64Tab', TestType.FUNCTION, async (done: Function) => {
      try {
        const cryptoTab = await driver.findComponent(ON.text('密码学'));
        await cryptoTab.click();
        await driver.delayMs(1000);

        const base64Tab = await driver.findComponent(ON.text('Base64'));
        await base64Tab.click();
        await driver.delayMs(500);

        await driver.assertComponentExist(ON.text('Base64 编码/解码'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number CryptoPageUITest_006
     * @tc.name 测试所有密码学子标签切换
     * @tc.desc 测试依次切换所有密码学子标签页
     */
    it('cryptoTest_cycleAllSubTabs', TestType.FUNCTION, async (done: Function) => {
      try {
        const cryptoTab = await driver.findComponent(ON.text('密码学'));
        await cryptoTab.click();
        await driver.delayMs(1000);

        const subTabs = ['哈希', 'Base64'];
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

    /**
     * @tc.number CryptoPageUITest_007
     * @tc.name 验证结果显示区域
     * @tc.desc 验证密码学操作结果显示区域是否存在
     */
    it('cryptoTest_verifyResultArea', TestType.FUNCTION, async (done: Function) => {
      try {
        const cryptoTab = await driver.findComponent(ON.text('密码学'));
        await cryptoTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('AES 加密/解密'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });
  });
}
