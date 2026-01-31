/*
 * Copyright (C) 2024 桃花镇童长老 @pura/harmony-utils
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

import { cryptoFramework } from '@kit.CryptoArchitectureKit';
import { CryptoUtil } from './CryptoUtil';


/**
 * TODO ECDSA工具类
 * author: 桃花镇童长老ᥫ᭡
 * since: 2024/07/01
 */
export class ECDSA {


  /**
   * 对数据进行签名，异步
   * @param dataBlob 待签名数据
   * @param priKey 私钥
   * @param algName 指定签名算法（ECC256|SHA256、ECC256|SHA512、ECC384|SHA256、等）。
   * @returns
   */
  static async sign(dataBlob: cryptoFramework.DataBlob, priKey: cryptoFramework.PriKey,
    algName = 'ECC256|SHA256'): Promise<cryptoFramework.DataBlob> {
    return await CryptoUtil.sign(dataBlob, priKey, algName);
  }

  /**
   * 对数据进行签名，同步
   * @param dataBlob 待签名数据
   * @param priKey 私钥
   * @param algName 指定签名算法（ECC256|SHA256、ECC256|SHA512、ECC384|SHA256、等）。
   * @returns
   */
  static signSync(dataBlob: cryptoFramework.DataBlob, priKey: cryptoFramework.PriKey,
    algName = 'ECC256|SHA256'): cryptoFramework.DataBlob {
    return CryptoUtil.signSync(dataBlob, priKey, algName);
  }


  /**
   * 对数据进行验签，异步
   * @param dataBlob 待验签数据
   * @param signDataBlob 签名数据
   * @param pubKey 公钥
   * @param algName 指定签名算法（ECC256|SHA256、ECC256|SHA512、ECC384|SHA256、等）。
   * @returns
   */
  static async verify(dataBlob: cryptoFramework.DataBlob, signDataBlob: cryptoFramework.DataBlob,
    pubKey: cryptoFramework.PubKey, algName = 'ECC256|SHA256'): Promise<boolean> {
    return await CryptoUtil.verify(dataBlob, signDataBlob, pubKey, algName);
  }

  /**
   * 对数据进行验签，同步
   * @param dataBlob 待验签数据
   * @param signDataBlob 签名数据
   * @param pubKey 公钥
   * @param algName 指定签名算法（ECC256|SHA256、ECC256|SHA512、ECC384|SHA256、等）。
   * @returns
   */
  static verifySync(dataBlob: cryptoFramework.DataBlob, signDataBlob: cryptoFramework.DataBlob,
    pubKey: cryptoFramework.PubKey, algName = 'ECC256|SHA256'): boolean {
    return CryptoUtil.verifySync(dataBlob, signDataBlob, pubKey, algName);
  }


  /**
   * 对数据进行分段签名，异步
   * @param data 待签名数据
   * @param priKey 私钥
   * @param algName 指定签名算法（ECC256|SHA256、ECC256|SHA512、ECC384|SHA256、等）。
   * @param len 自定义的数据拆分长度，此处取64
   * @returns
   */
  static async signSegment(data: Uint8Array, priKey: cryptoFramework.PriKey, algName = 'ECC256|SHA256',
    len: number = 64): Promise<cryptoFramework.DataBlob> {
    return await CryptoUtil.signSegment(data, priKey, algName, len);
  }

  /**
   * 对数据进行分段签名，同步
   * @param data 待签名数据
   * @param priKey 私钥
   * @param algName 指定签名算法（ECC256|SHA256、ECC256|SHA512、ECC384|SHA256、等）。
   * @param len 自定义的数据拆分长度，此处取64
   * @returns
   */
  static signSegmentSync(data: Uint8Array, priKey: cryptoFramework.PriKey, algName = 'ECC256|SHA256',
    len: number = 64): cryptoFramework.DataBlob {
    return CryptoUtil.signSegmentSync(data, priKey, algName, len);
  }


  /**
   * 对数据进行分段验签，异步
   * @param data 待验签数据
   * @param signDataBlob 签名数据
   * @param pubKey 公钥
   * @param algName 指定签名算法（ECC256|SHA256、ECC256|SHA512、ECC384|SHA256、等）。
   * @param len 自定义的数据拆分长度，此处取64
   * @returns
   */
  static async verifySegment(data: Uint8Array, signDataBlob: cryptoFramework.DataBlob, pubKey: cryptoFramework.PubKey,
    algName = 'ECC256|SHA256', len: number = 64): Promise<boolean> {
    return CryptoUtil.verifySegment(data, signDataBlob, pubKey, algName, len);
  }

  /**
   * 对数据进行分段验签，同步
   * @param data 待验签数据
   * @param signDataBlob 签名数据
   * @param pubKey 公钥
   * @param algName 指定签名算法（ECC256|SHA256、ECC256|SHA512、ECC384|SHA256、等）。
   * @param len 自定义的数据拆分长度，此处取64
   * @returns
   */
  static verifySegmentSync(data: Uint8Array, signDataBlob: cryptoFramework.DataBlob, pubKey: cryptoFramework.PubKey,
    algName = 'ECC256|SHA256', len: number = 64): boolean {
    return CryptoUtil.verifySegmentSync(data, signDataBlob, pubKey, algName, len);
  }


}