import { ImageProcessor } from '../services/ImageProcessor';
import { Logger } from '../utils/Logger';
import { photoAccessHelper } from '@kit.MediaLibraryKit';

export class ImagePage extends View {
  constructor() {
    super();
    this.selectedImageUri__ = this.createState('selectedImageUri', () => this.selectedImageUri);
    this.processedImageUri__ = this.createState('processedImageUri', () => this.processedImageUri);
    this.imageInfo__ = this.createState('imageInfo', () => this.imageInfo);
    this.quality__ = this.createState('quality', () => this.quality);
    this.rotationAngle__ = this.createState('rotationAngle', () => this.rotationAngle);
    this.cropX__ = this.createState('cropX', () => this.cropX);
    this.cropY__ = this.createState('cropY', () => this.cropY);
    this.cropWidth__ = this.createState('cropWidth', () => this.cropWidth);
    this.cropHeight__ = this.createState('cropHeight', () => this.cropHeight);
  }

  async selectImage() {
    try {
  const photoSelectOptions = {"kind":215,"kindName":"NewExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"photoAccessHelper","text":"photoAccessHelper"},"name":"PhotoSelectOptions"},"arguments":[]};
  photoSelectOptions.MIMEType = photoAccessHelper.PhotoViewMIMETypes.IMAGE_TYPE;
  photoSelectOptions.maxSelectNumber = 1;
  const photoPicker = {"kind":215,"kindName":"NewExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"photoAccessHelper","text":"photoAccessHelper"},"name":"PhotoViewPicker"},"arguments":[]};
  const result = await photoPicker.select(photoSelectOptions);
  if (result && result.photoUris && result.photoUris.length > 0) {
  {"kind":110,"kindName":"ThisKeyword"}.selectedImageUri = result.photoUris[0];
  const info = await ImageProcessor.getImageInfo(this.selectedImageUri);
  {"kind":110,"kindName":"ThisKeyword"}.imageInfo = {"kind":229,"kindName":"TemplateExpression"};
  Logger.success("图片选择成功");
}
} catch (error) {
  Logger.error("图片选择失败", error);
}
  }

  initialRender() {
        Tabs.create({barPosition: BarPosition.Start});
            TabContent.create();
                Column.create({space: 16});
                    Text.create("图像压缩");
          Text.fontSize(20)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

          Button("选择图片").width("100%").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.selectImage();
  });
          If.create();
          if (this.selectedImageUri) {
            If.branchId(0);
            Image(this.selectedImageUri).width("80%").height(200).objectFit(ImageFit.Contain);
            Text(this.imageInfo).fontSize(14);
                        Row.create({space: 16});
                            Text.create("质量:");
              Text.pop();

              Slider({value: {"kind":110,"kindName":"ThisKeyword"}.quality, min: 10, max: 100, step: 10}).width("60%").onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.quality = value;
  });
              Text(`${this.quality}%`);
            Row.pop();

            Button("压缩图片").width("100%").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.selectedImageUri) {
    {"kind":110,"kindName":"ThisKeyword"}.processedImageUri = await ImageProcessor.compressImage(this.selectedImageUri, this.quality);
    }
  });
          }
          If.pop();
          If.create();
          if (this.processedImageUri) {
            If.branchId(0);
                        Text.create("压缩后图片:");
            Text.fontSize(14)
            Text.pop();

            Image(this.processedImageUri).width("80%").height(200).objectFit(ImageFit.Contain);
          }
          If.pop();
        Column.pop();

        width("100%").height("100%").padding(16);
      TabContent.pop();

      tabBar("压缩");
            TabContent.create();
                Column.create({space: 16});
                    Text.create("图像裁剪");
          Text.fontSize(20)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

          Button("选择图片").width("100%").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.selectImage();
  });
          If.create();
          if (this.selectedImageUri) {
            If.branchId(0);
            Image(this.selectedImageUri).width("80%").height(200).objectFit(ImageFit.Contain);
                        Row.create({space: 8});
              TextInput({placeholder: "X"}).width(80).type(InputType.Number).onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.cropX = value;
  });
              TextInput({placeholder: "Y"}).width(80).type(InputType.Number).onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.cropY = value;
  });
            Row.pop();

                        Row.create({space: 8});
              TextInput({placeholder: "宽度"}).width(80).type(InputType.Number).onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.cropWidth = value;
  });
              TextInput({placeholder: "高度"}).width(80).type(InputType.Number).onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.cropHeight = value;
  });
            Row.pop();

            Button("裁剪图片").width("100%").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.selectedImageUri) {
    const x = parseInt(this.cropX) || 0;
    const y = parseInt(this.cropY) || 0;
    const w = parseInt(this.cropWidth) || 200;
    const h = parseInt(this.cropHeight) || 200;
    {"kind":110,"kindName":"ThisKeyword"}.processedImageUri = await ImageProcessor.cropImage(this.selectedImageUri, x, y, w, h);
    }
  });
          }
          If.pop();
          If.create();
          if (this.processedImageUri) {
            If.branchId(0);
                        Text.create("裁剪后图片:");
            Text.fontSize(14)
            Text.pop();

            Image(this.processedImageUri).width("80%").height(200).objectFit(ImageFit.Contain);
          }
          If.pop();
        Column.pop();

        width("100%").height("100%").padding(16);
      TabContent.pop();

      tabBar("裁剪");
            TabContent.create();
                Column.create({space: 16});
                    Text.create("图像旋转");
          Text.fontSize(20)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

          Button("选择图片").width("100%").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.selectImage();
  });
          If.create();
          if (this.selectedImageUri) {
            If.branchId(0);
            Image(this.selectedImageUri).width("80%").height(200).objectFit(ImageFit.Contain);
                        Row.create({space: 16});
                            Text.create("角度:");
              Text.pop();

              Slider({value: {"kind":110,"kindName":"ThisKeyword"}.rotationAngle, min: 0, max: 360, step: 90}).width("60%").onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.rotationAngle = value;
  });
              Text(`${this.rotationAngle}°`);
            Row.pop();

            Button("旋转图片").width("100%").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.selectedImageUri) {
    {"kind":110,"kindName":"ThisKeyword"}.processedImageUri = await ImageProcessor.rotateImage(this.selectedImageUri, this.rotationAngle);
    }
  });
          }
          If.pop();
          If.create();
          if (this.processedImageUri) {
            If.branchId(0);
                        Text.create("旋转后图片:");
            Text.fontSize(14)
            Text.pop();

            Image(this.processedImageUri).width("80%").height(200).objectFit(ImageFit.Contain);
          }
          If.pop();
        Column.pop();

        width("100%").height("100%").padding(16);
      TabContent.pop();

      tabBar("旋转");
            TabContent.create();
                Column.create({space: 16});
                    Text.create("图像滤镜");
          Text.fontSize(20)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

          Button("选择图片").width("100%").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.selectImage();
  });
          If.create();
          if (this.selectedImageUri) {
            If.branchId(0);
            Image(this.selectedImageUri).width("80%").height(200).objectFit(ImageFit.Contain);
                        Row.create({space: 16});
              Button("灰度").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.processedImageUri = await ImageProcessor.applyGrayscaleFilter(this.selectedImageUri);
  });
              Button("黑白").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.processedImageUri = await ImageProcessor.applyBlackWhiteFilter(this.selectedImageUri);
  });
              Button("高对比度").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.processedImageUri = await ImageProcessor.applyHighContrastFilter(this.selectedImageUri);
  });
            Row.pop();

          }
          If.pop();
          If.create();
          if (this.processedImageUri) {
            If.branchId(0);
                        Text.create("滤镜后图片:");
            Text.fontSize(14)
            Text.pop();

            Image(this.processedImageUri).width("80%").height(200).objectFit(ImageFit.Contain);
          }
          If.pop();
        Column.pop();

        width("100%").height("100%").padding(16);
      TabContent.pop();

      tabBar("滤镜");
    Tabs.pop();

    barWidth(100);
  }

  private selectedImageUri__ = '';

  get selectedImageUri() {
    return this.selectedImageUri__.get();
  }

  set selectedImageUri(newValue) {
    this.selectedImageUri__.set(newValue);
  }

  private processedImageUri__ = '';

  get processedImageUri() {
    return this.processedImageUri__.get();
  }

  set processedImageUri(newValue) {
    this.processedImageUri__.set(newValue);
  }

  private imageInfo__ = '';

  get imageInfo() {
    return this.imageInfo__.get();
  }

  set imageInfo(newValue) {
    this.imageInfo__.set(newValue);
  }

  private quality__ = 80;

  get quality() {
    return this.quality__.get();
  }

  set quality(newValue) {
    this.quality__.set(newValue);
  }

  private rotationAngle__ = 90;

  get rotationAngle() {
    return this.rotationAngle__.get();
  }

  set rotationAngle(newValue) {
    this.rotationAngle__.set(newValue);
  }

  private cropX__ = 0;

  get cropX() {
    return this.cropX__.get();
  }

  set cropX(newValue) {
    this.cropX__.set(newValue);
  }

  private cropY__ = 0;

  get cropY() {
    return this.cropY__.get();
  }

  set cropY(newValue) {
    this.cropY__.set(newValue);
  }

  private cropWidth__ = 200;

  get cropWidth() {
    return this.cropWidth__.get();
  }

  set cropWidth(newValue) {
    this.cropWidth__.set(newValue);
  }

  private cropHeight__ = 200;

  get cropHeight() {
    return this.cropHeight__.get();
  }

  set cropHeight(newValue) {
    this.cropHeight__.set(newValue);
  }

}


//# sourceMappingURL=ImagePage.js.map