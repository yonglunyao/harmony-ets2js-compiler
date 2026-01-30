import { ImageProcessor } from '../services/ImageProcessor';
import { Logger } from '../utils/Logger';
import { photoAccessHelper } from '@kit.MediaLibraryKit';

@Entry
@Component
export struct ImagePage {
  @State selectedImageUri: string = '';
  @State processedImageUri: string = '';
  @State imageInfo: string = '';
  @State quality: number = 80;
  @State rotationAngle: number = 90;
  @State cropX: string = '0';
  @State cropY: string = '0';
  @State cropWidth: string = '200';
  @State cropHeight: string = '200';

  async selectImage(): Promise<void> {
    try {
      const photoSelectOptions = new photoAccessHelper.PhotoSelectOptions();
      photoSelectOptions.MIMEType = photoAccessHelper.PhotoViewMIMETypes.IMAGE_TYPE;
      photoSelectOptions.maxSelectNumber = 1;

      const photoPicker = new photoAccessHelper.PhotoViewPicker();
      const result = await photoPicker.select(photoSelectOptions);

      if (result && result.photoUris && result.photoUris.length > 0) {
        this.selectedImageUri = result.photoUris[0];
        const info = await ImageProcessor.getImageInfo(this.selectedImageUri);
        this.imageInfo = `尺寸: ${info.width} x ${info.height}\n格式: ${info.format}`;
        Logger.success('图片选择成功');
      }
    } catch (error) {
      Logger.error('图片选择失败', error);
    }
  }

  build() {
    Tabs({ barPosition: BarPosition.Start }) {
      TabContent() {
        Column({ space: 16 }) {
          Text('图像压缩')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)

          Button('选择图片')
            .width('100%')
            .onClick(() => {
              this.selectImage();
            })

          if (this.selectedImageUri) {
            Image(this.selectedImageUri)
              .width('80%')
              .height(200)
              .objectFit(ImageFit.Contain)

            Text(this.imageInfo)
              .fontSize(14)

            Row({ space: 16 }) {
              Text('质量:')
              Slider({
                value: this.quality,
                min: 10,
                max: 100,
                step: 10
              })
                .width('60%')
                .onChange((value) => {
                  this.quality = value;
                })
              Text(`${this.quality}%`)
            }

            Button('压缩图片')
              .width('100%')
              .onClick(async () => {
                if (this.selectedImageUri) {
                  this.processedImageUri = await ImageProcessor.compressImage(this.selectedImageUri, this.quality);
                }
              })
          }

          if (this.processedImageUri) {
            Text('压缩后图片:')
              .fontSize(14)

            Image(this.processedImageUri)
              .width('80%')
              .height(200)
              .objectFit(ImageFit.Contain)
          }
        }
        .width('100%')
        .height('100%')
        .padding(16)
      }
      .tabBar('压缩')

      TabContent() {
        Column({ space: 16 }) {
          Text('图像裁剪')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)

          Button('选择图片')
            .width('100%')
            .onClick(() => {
              this.selectImage();
            })

          if (this.selectedImageUri) {
            Image(this.selectedImageUri)
              .width('80%')
              .height(200)
              .objectFit(ImageFit.Contain)

            Row({ space: 8 }) {
              TextInput({ placeholder: 'X' })
                .width(80)
                .type(InputType.Number)
                .onChange((value) => {
                  this.cropX = value;
                })

              TextInput({ placeholder: 'Y' })
                .width(80)
                .type(InputType.Number)
                .onChange((value) => {
                  this.cropY = value;
                })
            }

            Row({ space: 8 }) {
              TextInput({ placeholder: '宽度' })
                .width(80)
                .type(InputType.Number)
                .onChange((value) => {
                  this.cropWidth = value;
                })

              TextInput({ placeholder: '高度' })
                .width(80)
                .type(InputType.Number)
                .onChange((value) => {
                  this.cropHeight = value;
                })
            }

            Button('裁剪图片')
              .width('100%')
              .onClick(async () => {
                if (this.selectedImageUri) {
                  const x = parseInt(this.cropX) || 0;
                  const y = parseInt(this.cropY) || 0;
                  const w = parseInt(this.cropWidth) || 200;
                  const h = parseInt(this.cropHeight) || 200;
                  this.processedImageUri = await ImageProcessor.cropImage(this.selectedImageUri, x, y, w, h);
                }
              })
          }

          if (this.processedImageUri) {
            Text('裁剪后图片:')
              .fontSize(14)

            Image(this.processedImageUri)
              .width('80%')
              .height(200)
              .objectFit(ImageFit.Contain)
          }
        }
        .width('100%')
        .height('100%')
        .padding(16)
      }
      .tabBar('裁剪')

      TabContent() {
        Column({ space: 16 }) {
          Text('图像旋转')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)

          Button('选择图片')
            .width('100%')
            .onClick(() => {
              this.selectImage();
            })

          if (this.selectedImageUri) {
            Image(this.selectedImageUri)
              .width('80%')
              .height(200)
              .objectFit(ImageFit.Contain)

            Row({ space: 16 }) {
              Text('角度:')
              Slider({
                value: this.rotationAngle,
                min: 0,
                max: 360,
                step: 90
              })
                .width('60%')
                .onChange((value) => {
                  this.rotationAngle = value;
                })
              Text(`${this.rotationAngle}°`)
            }

            Button('旋转图片')
              .width('100%')
              .onClick(async () => {
                if (this.selectedImageUri) {
                  this.processedImageUri = await ImageProcessor.rotateImage(this.selectedImageUri, this.rotationAngle);
                }
              })
          }

          if (this.processedImageUri) {
            Text('旋转后图片:')
              .fontSize(14)

            Image(this.processedImageUri)
              .width('80%')
              .height(200)
              .objectFit(ImageFit.Contain)
          }
        }
        .width('100%')
        .height('100%')
        .padding(16)
      }
      .tabBar('旋转')

      TabContent() {
        Column({ space: 16 }) {
          Text('图像滤镜')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)

          Button('选择图片')
            .width('100%')
            .onClick(() => {
              this.selectImage();
            })

          if (this.selectedImageUri) {
            Image(this.selectedImageUri)
              .width('80%')
              .height(200)
              .objectFit(ImageFit.Contain)

            Row({ space: 16 }) {
              Button('灰度')
                .onClick(async () => {
                  this.processedImageUri = await ImageProcessor.applyGrayscaleFilter(this.selectedImageUri);
                })

              Button('黑白')
                .onClick(async () => {
                  this.processedImageUri = await ImageProcessor.applyBlackWhiteFilter(this.selectedImageUri);
                })

              Button('高对比度')
                .onClick(async () => {
                  this.processedImageUri = await ImageProcessor.applyHighContrastFilter(this.selectedImageUri);
                })
            }
          }

          if (this.processedImageUri) {
            Text('滤镜后图片:')
              .fontSize(14)

            Image(this.processedImageUri)
              .width('80%')
              .height(200)
              .objectFit(ImageFit.Contain)
          }
        }
        .width('100%')
        .height('100%')
        .padding(16)
      }
      .tabBar('滤镜')
    }
    .barWidth(100)
  }
}
