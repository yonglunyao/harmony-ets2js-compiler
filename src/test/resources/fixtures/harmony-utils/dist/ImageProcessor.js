import image from '@ohos.multimedia.image';
import fs from '@ohos.file.fs';
import { Logger } from '../utils/Logger';

export class ImageProcessor {
  TAG = ImageProcessor;

  static async compressImage(sourceUri /* string */, quality /* number */) {
    try {
  const file = fs.openSync(sourceUri, 1);
  const imageSource = image.createImageSource(file.fd);
  const pixelMap = await imageSource.createPixelMap();
  const imagePacker = image.createImagePacker();
  const packingOpts = {format: , quality: };
  const packedData = await imagePacker.packToData(pixelMap, packingOpts);
  const result = {"kind":229,"kindName":"TemplateExpression"};
  fs.closeSync(file);
  Logger.success(`图片压缩成功，质量: ${quality}%`);
  return result;
} catch (error) {
  Logger.error("图片压缩失败", error);
  return "";
}
  }

  static async rotateImage(sourceUri /* string */, degrees /* number */) {
    try {
  const file = fs.openSync(sourceUri, 1);
  const imageSource = image.createImageSource(file.fd);
  const pixelMap = await imageSource.createPixelMap();
  await pixelMap.rotate(degrees);
  const imagePacker = image.createImagePacker();
  const packingOpts = {format: , quality: };
  const packedData = await imagePacker.packToData(pixelMap, packingOpts);
  const result = {"kind":229,"kindName":"TemplateExpression"};
  fs.closeSync(file);
  Logger.success(`图片旋转成功，角度: ${degrees}度`);
  return result;
} catch (error) {
  Logger.error("图片旋转失败", error);
  return "";
}
  }

  static async cropImage(sourceUri /* string */, x /* number */, y /* number */, width /* number */, height /* number */) {
    try {
  const file = fs.openSync(sourceUri, 1);
  const imageSource = image.createImageSource(file.fd);
  const pixelMap = await imageSource.createPixelMap();
  await pixelMap.crop("{x: x, y: y, size: {width: , height: }}");
  const imagePacker = image.createImagePacker();
  const packingOpts = {format: , quality: };
  const packedData = await imagePacker.packToData(pixelMap, packingOpts);
  const result = {"kind":229,"kindName":"TemplateExpression"};
  fs.closeSync(file);
  Logger.success(`图片裁剪成功，区域: ${x},${y} ${width}x${height}`);
  return result;
} catch (error) {
  Logger.error("图片裁剪失败", error);
  return "";
}
  }

  static async applyGrayscaleFilter(sourceUri /* string */) {
    try {
  const file = fs.openSync(sourceUri, 1);
  const imageSource = image.createImageSource(file.fd);
  const pixelMap = await imageSource.createPixelMap();
  const imageInfo = await pixelMap.getImageInfo();
  const bufferSize = imageInfo.size.width * imageInfo.size.height * 4;
  const bufferData = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"ArrayBuffer","text":"ArrayBuffer"},"arguments":[{"kind":80,"kindName":"Identifier","name":"bufferSize","text":"bufferSize"}]};
  await pixelMap.readPixelsToBuffer(bufferData);
  const uint8Array = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Uint8Array","text":"Uint8Array"},"arguments":[{"kind":80,"kindName":"Identifier","name":"bufferData","text":"bufferData"}]};
  for (let i = 0; i < uint8Array.length; i += 4) {
  const gray = uint8Array[i] * 0.299 + uint8Array[i + 1] * 0.587 + uint8Array[i + 2] * 0.114;
  uint8Array[i] = gray;
  uint8Array[i + 1] = gray;
  uint8Array[i + 2] = gray;
}
  await pixelMap.writeBufferToPixels(bufferData);
  const imagePacker = image.createImagePacker();
  const packingOpts = {format: , quality: };
  const packedData = await imagePacker.packToData(pixelMap, packingOpts);
  const result = {"kind":229,"kindName":"TemplateExpression"};
  fs.closeSync(file);
  Logger.success("灰度滤镜应用成功");
  return result;
} catch (error) {
  Logger.error("灰度滤镜应用失败", error);
  return "";
}
  }

  static async applyBlackWhiteFilter(sourceUri /* string */, threshold /* number */) {
    try {
  const file = fs.openSync(sourceUri, 1);
  const imageSource = image.createImageSource(file.fd);
  const pixelMap = await imageSource.createPixelMap();
  const imageInfo = await pixelMap.getImageInfo();
  const bufferSize = imageInfo.size.width * imageInfo.size.height * 4;
  const bufferData = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"ArrayBuffer","text":"ArrayBuffer"},"arguments":[{"kind":80,"kindName":"Identifier","name":"bufferSize","text":"bufferSize"}]};
  await pixelMap.readPixelsToBuffer(bufferData);
  const uint8Array = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Uint8Array","text":"Uint8Array"},"arguments":[{"kind":80,"kindName":"Identifier","name":"bufferData","text":"bufferData"}]};
  for (let i = 0; i < uint8Array.length; i += 4) {
  const gray = uint8Array[i] * 0.299 + uint8Array[i + 1] * 0.587 + uint8Array[i + 2] * 0.114;
  const value = gray > threshold ? 255 : 0;
  uint8Array[i] = value;
  uint8Array[i + 1] = value;
  uint8Array[i + 2] = value;
}
  await pixelMap.writeBufferToPixels(bufferData);
  const imagePacker = image.createImagePacker();
  const packingOpts = {format: , quality: };
  const packedData = await imagePacker.packToData(pixelMap, packingOpts);
  const result = {"kind":229,"kindName":"TemplateExpression"};
  fs.closeSync(file);
  Logger.success("黑白滤镜应用成功");
  return result;
} catch (error) {
  Logger.error("黑白滤镜应用失败", error);
  return "";
}
  }

  static async applyHighContrastFilter(sourceUri /* string */, contrast /* number */) {
    try {
  const file = fs.openSync(sourceUri, 1);
  const imageSource = image.createImageSource(file.fd);
  const pixelMap = await imageSource.createPixelMap();
  const imageInfo = await pixelMap.getImageInfo();
  const bufferSize = imageInfo.size.width * imageInfo.size.height * 4;
  const bufferData = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"ArrayBuffer","text":"ArrayBuffer"},"arguments":[{"kind":80,"kindName":"Identifier","name":"bufferSize","text":"bufferSize"}]};
  await pixelMap.readPixelsToBuffer(bufferData);
  const uint8Array = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Uint8Array","text":"Uint8Array"},"arguments":[{"kind":80,"kindName":"Identifier","name":"bufferData","text":"bufferData"}]};
  const factor = (259 * (contrast * 255 + 255)) / (255 * (259 - contrast * 255));
  for (let i = 0; i < uint8Array.length; i += 4) {
  const value = Math.min(255, Math.max(0, factor * (uint8Array[i] - 128) + 128));
  uint8Array[i] = value;
  uint8Array[i + 1] = value;
  uint8Array[i + 2] = value;
}
  await pixelMap.writeBufferToPixels(bufferData);
  const imagePacker = image.createImagePacker();
  const packingOpts = {format: , quality: };
  const packedData = await imagePacker.packToData(pixelMap, packingOpts);
  const result = {"kind":229,"kindName":"TemplateExpression"};
  fs.closeSync(file);
  Logger.success("高对比度滤镜应用成功");
  return result;
} catch (error) {
  Logger.error("高对比度滤镜应用失败", error);
  return "";
}
  }

  static async convertFormat(sourceUri /* string */, targetFormat /* 'image/jpeg' | 'image/png' | 'image/webp' */) {
    try {
  const file = fs.openSync(sourceUri, 1);
  const imageSource = image.createImageSource(file.fd);
  const pixelMap = await imageSource.createPixelMap();
  const imagePacker = image.createImagePacker();
  const packingOpts = {format: , quality: };
  const packedData = await imagePacker.packToData(pixelMap, packingOpts);
  const result = {"kind":229,"kindName":"TemplateExpression"};
  fs.closeSync(file);
  Logger.success(`格式转换成功: ${targetFormat}`);
  return result;
} catch (error) {
  Logger.error("格式转换失败", error);
  return "";
}
  }

  static async getImageInfo(sourceUri /* string */) {
    try {
  const file = fs.openSync(sourceUri, 1);
  const imageSource = image.createImageSource(file.fd);
  const pixelMap = await imageSource.createPixelMap();
  const imageInfo = await pixelMap.getImageInfo();
  const result = {width: , height: , format: };
  fs.closeSync(file);
  Logger.success("获取图片信息成功");
  return result;
} catch (error) {
  Logger.error("获取图片信息失败", error);
  const emptyResult = {width: , height: , format: };
  return emptyResult;
}
  }

}


//# sourceMappingURL=ImageProcessor.js.map