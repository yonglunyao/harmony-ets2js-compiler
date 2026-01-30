import image from '@ohos.multimedia.image';
import fs from '@ohos.file.fs';
import { Logger } from '../utils/Logger';

export interface ImageInfo {
  width: number;
  height: number;
  format: string;
}

/**
 * 图像处理工具类
 * 提供图像压缩、裁剪、旋转、滤镜效果、格式转换等功能
 */
export class ImageProcessor {
  private static readonly TAG = 'ImageProcessor';

  // 压缩图片
  static async compressImage(sourceUri: string, quality: number): Promise<string> {
    try {
      const file = fs.openSync(sourceUri, 0o1);
      const imageSource = image.createImageSource(file.fd);
      const pixelMap = await imageSource.createPixelMap();
      const imagePacker = image.createImagePacker();
      const packingOpts: image.PackingOption = { format: 'image/jpeg', quality: quality };
      const packedData = await imagePacker.packToData(pixelMap, packingOpts);
      const result = `data:image/jpeg;base64,${packedData}`;
      fs.closeSync(file);
      Logger.success(`图片压缩成功，质量: ${quality}%`);
      return result;
    } catch (error) {
      Logger.error('图片压缩失败', error as Error);
      return '';
    }
  }

  // 旋转图片
  static async rotateImage(sourceUri: string, degrees: number): Promise<string> {
    try {
      const file = fs.openSync(sourceUri, 0o1);
      const imageSource = image.createImageSource(file.fd);
      const pixelMap = await imageSource.createPixelMap();
      await pixelMap.rotate(degrees);
      const imagePacker = image.createImagePacker();
      const packingOpts: image.PackingOption = { format: 'image/jpeg', quality: 100 };
      const packedData = await imagePacker.packToData(pixelMap, packingOpts);
      const result = `data:image/jpeg;base64,${packedData}`;
      fs.closeSync(file);
      Logger.success(`图片旋转成功，角度: ${degrees}度`);
      return result;
    } catch (error) {
      Logger.error('图片旋转失败', error as Error);
      return '';
    }
  }

  // 裁剪图片
  static async cropImage(sourceUri: string, x: number, y: number, width: number, height: number): Promise<string> {
    try {
      const file = fs.openSync(sourceUri, 0o1);
      const imageSource = image.createImageSource(file.fd);
      const pixelMap = await imageSource.createPixelMap();
      await pixelMap.crop({ x: x, y: y, size: { width: width, height: height } });
      const imagePacker = image.createImagePacker();
      const packingOpts: image.PackingOption = { format: 'image/jpeg', quality: 100 };
      const packedData = await imagePacker.packToData(pixelMap, packingOpts);
      const result = `data:image/jpeg;base64,${packedData}`;
      fs.closeSync(file);
      Logger.success(`图片裁剪成功，区域: ${x},${y} ${width}x${height}`);
      return result;
    } catch (error) {
      Logger.error('图片裁剪失败', error as Error);
      return '';
    }
  }

  // 灰度滤镜 - 简化版
  static async applyGrayscaleFilter(sourceUri: string): Promise<string> {
    try {
      const file = fs.openSync(sourceUri, 0o1);
      const imageSource = image.createImageSource(file.fd);
      const pixelMap = await imageSource.createPixelMap();
      const imageInfo = await pixelMap.getImageInfo();
      const bufferSize = imageInfo.size.width * imageInfo.size.height * 4;
      const bufferData = new ArrayBuffer(bufferSize);
      await pixelMap.readPixelsToBuffer(bufferData);
      const uint8Array = new Uint8Array(bufferData);

      // 应用灰度滤镜
      for (let i = 0; i < uint8Array.length; i += 4) {
        const gray = uint8Array[i] * 0.299 + uint8Array[i + 1] * 0.587 + uint8Array[i + 2] * 0.114;
        uint8Array[i] = gray;
        uint8Array[i + 1] = gray;
        uint8Array[i + 2] = gray;
      }

      await pixelMap.writeBufferToPixels(bufferData);
      const imagePacker = image.createImagePacker();
      const packingOpts: image.PackingOption = { format: 'image/jpeg', quality: 100 };
      const packedData = await imagePacker.packToData(pixelMap, packingOpts);
      const result = `data:image/jpeg;base64,${packedData}`;
      fs.closeSync(file);
      Logger.success('灰度滤镜应用成功');
      return result;
    } catch (error) {
      Logger.error('灰度滤镜应用失败', error as Error);
      return '';
    }
  }

  // 黑白滤镜
  static async applyBlackWhiteFilter(sourceUri: string, threshold: number = 128): Promise<string> {
    try {
      const file = fs.openSync(sourceUri, 0o1);
      const imageSource = image.createImageSource(file.fd);
      const pixelMap = await imageSource.createPixelMap();
      const imageInfo = await pixelMap.getImageInfo();
      const bufferSize = imageInfo.size.width * imageInfo.size.height * 4;
      const bufferData = new ArrayBuffer(bufferSize);
      await pixelMap.readPixelsToBuffer(bufferData);
      const uint8Array = new Uint8Array(bufferData);

      // 应用黑白滤镜
      for (let i = 0; i < uint8Array.length; i += 4) {
        const gray = uint8Array[i] * 0.299 + uint8Array[i + 1] * 0.587 + uint8Array[i + 2] * 0.114;
        const value = gray > threshold ? 255 : 0;
        uint8Array[i] = value;
        uint8Array[i + 1] = value;
        uint8Array[i + 2] = value;
      }

      await pixelMap.writeBufferToPixels(bufferData);
      const imagePacker = image.createImagePacker();
      const packingOpts: image.PackingOption = { format: 'image/jpeg', quality: 100 };
      const packedData = await imagePacker.packToData(pixelMap, packingOpts);
      const result = `data:image/jpeg;base64,${packedData}`;
      fs.closeSync(file);
      Logger.success('黑白滤镜应用成功');
      return result;
    } catch (error) {
      Logger.error('黑白滤镜应用失败', error as Error);
      return '';
    }
  }

  // 高对比度滤镜
  static async applyHighContrastFilter(sourceUri: string, contrast: number = 1.5): Promise<string> {
    try {
      const file = fs.openSync(sourceUri, 0o1);
      const imageSource = image.createImageSource(file.fd);
      const pixelMap = await imageSource.createPixelMap();
      const imageInfo = await pixelMap.getImageInfo();
      const bufferSize = imageInfo.size.width * imageInfo.size.height * 4;
      const bufferData = new ArrayBuffer(bufferSize);
      await pixelMap.readPixelsToBuffer(bufferData);
      const uint8Array = new Uint8Array(bufferData);

      // 应用高对比度滤镜
      const factor = (259 * (contrast * 255 + 255)) / (255 * (259 - contrast * 255));

      for (let i = 0; i < uint8Array.length; i += 4) {
        const value = Math.min(255, Math.max(0, factor * (uint8Array[i] - 128) + 128));
        uint8Array[i] = value;
        uint8Array[i + 1] = value;
        uint8Array[i + 2] = value;
      }

      await pixelMap.writeBufferToPixels(bufferData);
      const imagePacker = image.createImagePacker();
      const packingOpts: image.PackingOption = { format: 'image/jpeg', quality: 100 };
      const packedData = await imagePacker.packToData(pixelMap, packingOpts);
      const result = `data:image/jpeg;base64,${packedData}`;
      fs.closeSync(file);
      Logger.success('高对比度滤镜应用成功');
      return result;
    } catch (error) {
      Logger.error('高对比度滤镜应用失败', error as Error);
      return '';
    }
  }

  // 格式转换
  static async convertFormat(sourceUri: string, targetFormat: 'image/jpeg' | 'image/png' | 'image/webp'): Promise<string> {
    try {
      const file = fs.openSync(sourceUri, 0o1);
      const imageSource = image.createImageSource(file.fd);
      const pixelMap = await imageSource.createPixelMap();
      const imagePacker = image.createImagePacker();
      const packingOpts: image.PackingOption = { format: targetFormat, quality: 100 };
      const packedData = await imagePacker.packToData(pixelMap, packingOpts);
      const result = `data:${targetFormat};base64,${packedData}`;
      fs.closeSync(file);
      Logger.success(`格式转换成功: ${targetFormat}`);
      return result;
    } catch (error) {
      Logger.error('格式转换失败', error as Error);
      return '';
    }
  }

  // 获取图片信息
  static async getImageInfo(sourceUri: string): Promise<ImageInfo> {
    try {
      const file = fs.openSync(sourceUri, 0o1);
      const imageSource = image.createImageSource(file.fd);
      const pixelMap = await imageSource.createPixelMap();
      const imageInfo = await pixelMap.getImageInfo();
      const result: ImageInfo = {
        width: imageInfo.size.width,
        height: imageInfo.size.height,
        format: 'image/jpeg'
      };
      fs.closeSync(file);
      Logger.success('获取图片信息成功');
      return result;
    } catch (error) {
      Logger.error('获取图片信息失败', error as Error);
      const emptyResult: ImageInfo = { width: 0, height: 0, format: '' };
      return emptyResult;
    }
  }
}
