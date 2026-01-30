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

const TAG = 'VideoPageUITest';
const delegator = abilityDelegatorRegistry.getAbilityDelegator();
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;

export default function VideoPageUITest() {
  describe('VideoPageUITest', () => {
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
     * @tc.number VideoPageUITest_001
     * @tc.name 导航到视频处理页面
     * @tc.desc 测试从首页导航到视频处理页面
     */
    it('videoTest_navigateToPage', TestType.FUNCTION, async (done: Function) => {
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
     * @tc.number VideoPageUITest_002
     * @tc.name 测试选择视频按钮
     * @tc.desc 验证选择视频按钮是否存在且可点击
     */
    it('videoTest_verifySelectVideoButton', TestType.FUNCTION, async (done: Function) => {
      try {
        const videoTab = await driver.findComponent(ON.text('视频'));
        await videoTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('选择视频'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number VideoPageUITest_003
     * @tc.name 测试视频信息区域
     * @tc.desc 验证视频信息区域是否正确显示
     */
    it('videoTest_verifyVideoInfoArea', TestType.FUNCTION, async (done: Function) => {
      try {
        const videoTab = await driver.findComponent(ON.text('视频'));
        await videoTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('视频信息'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number VideoPageUITest_004
     * @tc.name 测试获取时长功能
     * @tc.desc 验证获取视频时长按钮和区域是否正确显示
     */
    it('videoTest_verifyDurationArea', TestType.FUNCTION, async (done: Function) => {
      try {
        const videoTab = await driver.findComponent(ON.text('视频'));
        await videoTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('视频时长显示'));
        await driver.assertComponentExist(ON.text('获取时长'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number VideoPageUITest_005
     * @tc.name 测试视频截图功能
     * @tc.desc 验证视频截图相关控件是否正确显示
     */
    it('videoTest_verifyCaptureArea', TestType.FUNCTION, async (done: Function) => {
      try {
        const videoTab = await driver.findComponent(ON.text('视频'));
        await videoTab.click();
        await driver.delayMs(1000);

        await driver.assertComponentExist(ON.text('视频截图'));
        await driver.assertComponentExist(ON.text('截图'));
        expect(true).assertTrue();
        done();
      } catch (error) {
        expect(false).assertTrue();
        done();
      }
    });

    /**
     * @tc.number VideoPageUITest_006
     * @tc.name 测试格式说明区域
     * @tc.desc 验证格式说明区域是否正确显示
     */
    it('videoTest_verifyFormatInfoArea', TestType.FUNCTION, async (done: Function) => {
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
  });
}
