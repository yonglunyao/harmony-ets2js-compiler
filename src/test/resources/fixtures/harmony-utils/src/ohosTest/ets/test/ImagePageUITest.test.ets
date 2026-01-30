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

const TAG = 'ImagePageUITest';
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;

export default function ImagePageUITest() {
  describe('ImagePageUITest', () => {
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

    it('imageTest_navigateToPage', TestType.FUNCTION, async (done: Function) => {
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

    it('imageTest_verifyCompressTab', TestType.FUNCTION, async (done: Function) => {
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

    it('imageTest_verifyCropTab', TestType.FUNCTION, async (done: Function) => {
      try {
        const imageTab = await driver.findComponent(ON.text('图像'));
        await imageTab.click();
        await driver.delayMs(1000);
        const cropTab = await driver.findComponent(ON.text('裁剪'));
        await cropTab.click();
        await driver.delayMs(500);
        await driver.assertComponentExist(ON.text('图像裁剪'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    it('imageTest_verifyRotateTab', TestType.FUNCTION, async (done: Function) => {
      try {
        const imageTab = await driver.findComponent(ON.text('图像'));
        await imageTab.click();
        await driver.delayMs(1000);
        const rotateTab = await driver.findComponent(ON.text('旋转'));
        await rotateTab.click();
        await driver.delayMs(500);
        await driver.assertComponentExist(ON.text('图像旋转'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    it('imageTest_verifyFilterTab', TestType.FUNCTION, async (done: Function) => {
      try {
        const imageTab = await driver.findComponent(ON.text('图像'));
        await imageTab.click();
        await driver.delayMs(1000);
        const filterTab = await driver.findComponent(ON.text('滤镜'));
        await filterTab.click();
        await driver.delayMs(500);
        await driver.assertComponentExist(ON.text('图像滤镜'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    it('imageTest_cycleAllSubTabs', TestType.FUNCTION, async (done: Function) => {
      try {
        const imageTab = await driver.findComponent(ON.text('图像'));
        await imageTab.click();
        await driver.delayMs(1000);
        const subTabs = ['裁剪', '旋转', '滤镜'];
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

    it('imageTest_verifySelectImageButton', TestType.FUNCTION, async (done: Function) => {
      try {
        const imageTab = await driver.findComponent(ON.text('图像'));
        await imageTab.click();
        await driver.delayMs(1000);
        await driver.assertComponentExist(ON.text('选择图片'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });
  });
}
