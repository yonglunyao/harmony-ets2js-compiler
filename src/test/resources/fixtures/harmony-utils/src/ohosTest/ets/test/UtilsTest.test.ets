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

import { describe, it, expect, TestType, Level, Size } from '@ohos/hypium';
import { CryptographyUtil } from '../../../main/ets/services/CryptographyUtil';
import { Logger } from '../../../main/ets/utils/Logger';

const TAG = 'UtilsTest';

export default function UtilsTest() {
  describe('CryptographyUtil Test', () => {
    it('cryptoTest_generateAESKey', TestType.FUNCTION, async (done: Function) => {
      const key = await CryptographyUtil.generateAESKey();
      expect(key.length > 0).assertTrue();
      done();
    });

    it('cryptoTest_base64Encode', TestType.FUNCTION, async (done: Function) => {
      const input = 'Hello HarmonyOS';
      const result = CryptographyUtil.encodeBase64(input);
      expect(result.length > 0).assertTrue();
      done();
    });

    it('cryptoTest_base64Decode', TestType.FUNCTION, async (done: Function) => {
      const input = 'SGVsbG8gSGFybW9ueU9T';
      const result = CryptographyUtil.decodeBase64(input);
      expect(result === 'Hello HarmonyOS').assertTrue();
      done();
    });

    it('cryptoTest_sha256', TestType.FUNCTION, async (done: Function) => {
      const input = 'test';
      const result = await CryptographyUtil.sha256(input);
      expect(result.length > 0).assertTrue();
      done();
    });

    it('cryptoTest_md5', TestType.FUNCTION, async (done: Function) => {
      const input = 'test';
      const result = await CryptographyUtil.md5(input);
      expect(result.length > 0).assertTrue();
      done();
    });
  });

  describe('Logger Test', () => {
    it('loggerTest_info', TestType.FUNCTION, (done: Function) => {
      Logger.info('Test info message');
      expect(true).assertTrue();
      done();
    });

    it('loggerTest_error', TestType.FUNCTION, (done: Function) => {
      Logger.error('Test error message');
      expect(true).assertTrue();
      done();
    });

    it('loggerTest_success', TestType.FUNCTION, (done: Function) => {
      Logger.success('Test success message');
      expect(true).assertTrue();
      done();
    });

    it('loggerTest_warn', TestType.FUNCTION, (done: Function) => {
      Logger.warn('Test warn message');
      expect(true).assertTrue();
      done();
    });

    it('loggerTest_debug', TestType.FUNCTION, (done: Function) => {
      Logger.debug('Test debug message');
      expect(true).assertTrue();
      done();
    });
  });
}
