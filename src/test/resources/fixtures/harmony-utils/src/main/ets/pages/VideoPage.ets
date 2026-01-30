import { VideoProcessor, VideoInfo } from '../services/VideoProcessor';
import { Logger } from '../utils/Logger';
import { photoAccessHelper } from '@kit.MediaLibraryKit';

@Component
export struct VideoPage {
  @State selectedVideoUri: string = '';
  @State videoInfo: VideoInfo = {
    width: 0,
    height: 0,
    duration: 0,
    durationFormatted: '',
    mimeType: ''
  };
  @State captureTime: number = 1000;
  @State capturedFrame: string = '';

  build() {
    Scroll() {
      Column({ space: 16 }) {
        Text('视频处理工具')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)

        Text('注意：此功能需要设备支持视频编解码')
          .fontSize(12)
          .fontColor('#666666')

        Divider()

        Button('选择视频')
          .width('100%')
          .onClick(async () => {
            try {
              const photoSelectOptions = new photoAccessHelper.PhotoSelectOptions();
              photoSelectOptions.MIMEType = photoAccessHelper.PhotoViewMIMETypes.VIDEO_TYPE;
              photoSelectOptions.maxSelectNumber = 1;

              const photoPicker = new photoAccessHelper.PhotoViewPicker();
              const result = await photoPicker.select(photoSelectOptions);

              if (result && result.photoUris && result.photoUris.length > 0) {
                this.selectedVideoUri = result.photoUris[0];
                const info = await VideoProcessor.getVideoInfo(this.selectedVideoUri);
                this.videoInfo = info;
                this.capturedFrame = '';
                Logger.success('视频选择成功');
              }
            } catch (error) {
              Logger.error('视频选择失败', error as Error);
            }
          })

        if (this.selectedVideoUri) {
          Divider()

          Text('视频信息')
            .fontSize(16)
            .fontColor('#333333')

          if (this.videoInfo.width > 0) {
            Text(`分辨率: ${this.videoInfo.width}x${this.videoInfo.height}`)
              .fontSize(14)
              .fontColor('#666666')
          }

          if (this.videoInfo.duration > 0) {
            Text(`时长: ${this.videoInfo.durationFormatted}`)
              .fontSize(14)
              .fontColor('#6666')
          }

          if (this.videoInfo.mimeType) {
            Text(`格式: ${this.videoInfo.mimeType}`)
              .fontSize(14)
              .fontColor('#6666')
          }
        }

        if (this.selectedVideoUri) {
          Divider()

          Text('视频时长显示')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)

          Row({ space: 16 }) {
            Button('获取时长')
              .onClick(async () => {
                try {
                  const duration = await VideoProcessor.getDuration(this.selectedVideoUri);
                  this.videoInfo.duration = duration;
                  this.videoInfo.durationFormatted = VideoProcessor.formatDuration(duration);
                  Logger.success(`获取时长成功：${this.videoInfo.durationFormatted}`);
                } catch (error) {
                  Logger.error('获取时长失败', error as Error);
                }
              })
          }

          Divider()

          Text('视频截图')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)

          Row({ space: 16 }) {
            TextInput({ placeholder: '截图时间点(毫秒)', text: `${this.captureTime}` })
              .type(InputType.Number)
              .width(120)
              .onChange((value) => {
                this.captureTime = parseInt(value) || 1000;
              })

            Button('截图')
              .onClick(async () => {
                try {
                  const frameUri = await VideoProcessor.captureFrame(this.selectedVideoUri, this.captureTime);
                  this.capturedFrame = frameUri;
                  Logger.success('截图成功');
                } catch (error) {
                  Logger.error('截图失败', error as Error);
                }
              })
          }

          if (this.capturedFrame) {
            Divider()
            Image(this.capturedFrame)
              .width(200)
              .height(200)
              .objectFit(ImageFit.Contain)
              .borderRadius(8)
              .margin({ top: 8 })
          }
        }
      }
      .width('100%')
      .padding(16)
      .justifyContent(FlexAlign.Start)
    }
  }
}
