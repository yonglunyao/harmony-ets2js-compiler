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

import { Driver } from '@kit.TestKit';

/**
 * TestHelper provides a shared singleton Driver instance for all test classes
 * to avoid concurrent Driver.create() calls which cause UITest API errors.
 */
export class TestHelper {
  private static driver: Driver | null = null;

  /**
   * Get or create the shared Driver instance
   * @returns The shared Driver instance
   */
  static getDriver(): Driver {
    if (!TestHelper.driver) {
      TestHelper.driver = Driver.create();
    }
    return TestHelper.driver;
  }

  /**
   * Reset the Driver instance (call this only when completely done with all tests)
   */
  static resetDriver(): void {
    TestHelper.driver = null;
  }
}
