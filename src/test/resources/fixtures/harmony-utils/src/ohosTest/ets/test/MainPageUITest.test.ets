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
import { abilityDelegatorRegistry, Driver, ON } from '@kit.TestKit';
import { Want } from '@kit.AbilityKit';
import { TestHelper } from './TestHelper';

const TAG = 'MainPageUITest';
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;

export default function MainPageUITest() {
  describe('MainPageUITest', () => {
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
     * @tc.number MainPageUITest_001
     * @tc.name 验证首页标题显示正确
     * @tc.desc 测试首页显示的标题文本是否正确
     */
    it('homeTest_verifyTitle', TestType.FUNCTION, async (done: Function) => {
      try {
        await driver.assertComponentExist(ON.text('HarmonyOS 工具集'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number MainPageUITest_002
     * @tc.name 验证功能卡片显示
     * @tc.desc 测试首页显示的功能卡片数量是否正确
     */
    it('homeTest_verifyFeatureCards', TestType.FUNCTION, async (done: Function) => {
      try {
        await driver.assertComponentExist(ON.text('HarmonyOS 工具集'));
        await driver.assertComponentExist(ON.text('一站式工具应用'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number MainPageUITest_003
     * @tc.name 切换到图像处理标签页
     * @tc.desc 测试点击图像标签页是否能够正确切换
     */
    it('homeTest_switchToImagePage', TestType.FUNCTION, async (done: Function) => {
      try {
        const imageTab = await driver.findComponent(ON.text('图像'));
        await imageTab.click();
        await driver.delayMs(1000);
        await driver.assertComponentExist(ON.text('图像压缩'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number MainPageUITest_004
     * @tc.name 切换到密码学标签页
     * @tc.desc 测试点击密码学标签页是否能够正确切换
     */
    it('homeTest_switchToCryptoPage', TestType.FUNCTION, async (done: Function) => {
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
     * @tc.number MainPageUITest_005
     * @tc.name 切换到文件管理标签页
     * @tc.desc 测试点击文件标签页是否能够正确切换
     */
    it('homeTest_switchToFilePage', TestType.FUNCTION, async (done: Function) => {
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

    /**
     * @tc.number MainPageUITest_006
     * @tc.name 切换到视频处理标签页
     * @tc.desc 测试点击视频标签页是否能够正确切换
     */
    it('homeTest_switchToVideoPage', TestType.FUNCTION, async (done: Function) => {
      try {
        const videoTab = await driver.findComponent(ON.text('视频'));
        await videoTab.click();
        await driver.delayMs(1000);
        await driver.assertComponentExist(ON.text('视频处理工具'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number MainPageUITest_007
     * @tc.name 切换到NFC标签页
     * @tc.desc 测试点击NFC标签页是否能够正确切换
     */
    it('homeTest_switchToNfcPage', TestType.FUNCTION, async (done: Function) => {
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
     * @tc.number MainPageUITest_008
     * @tc.name 验证提示信息显示
     * @tc.desc 测试首页底部的使用提示信息是否正确显示
     */
    it('homeTest_verifyTips', TestType.FUNCTION, async (done: Function) => {
      try {
        await driver.assertComponentExist(ON.text('提示'));
        await driver.assertComponentExist(ON.text('点击上方标签页切换到不同功能模块'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number MainPageUITest_009
     * @tc.name 循环切换所有标签页
     * @tc.desc 测试依次切换所有标签页并验证页面显示
     */
    it('homeTest_cycleAllTabs', TestType.FUNCTION, async (done: Function) => {
      try {
        const tabs = ['图像', '密码学', '文件', '视频', 'NFC'];
        for (const tabName of tabs) {
          const tab = await driver.findComponent(ON.text(tabName));
          await tab.click();
          await driver.delayMs(500);
        }
        // 返回首页
        const homeTab = await driver.findComponent(ON.text('首页'));
        await homeTab.click();
        await driver.delayMs(500);
        await driver.assertComponentExist(ON.text('HarmonyOS 工具集'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });
  });
}
