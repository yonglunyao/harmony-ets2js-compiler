import { VideoProcessor, VideoInfo } from '../services/VideoProcessor';
import { Logger } from '../utils/Logger';
import { photoAccessHelper } from '@kit.MediaLibraryKit';

export class VideoPage extends View {
  constructor() {
    super();
    this.selectedVideoUri__ = this.createState('selectedVideoUri', () => this.selectedVideoUri);
    this.videoInfo__ = this.createState('videoInfo', () => this.videoInfo);
    this.captureTime__ = this.createState('captureTime', () => this.captureTime);
    this.capturedFrame__ = this.createState('capturedFrame', () => this.capturedFrame);
  }

  initialRender() {
        Scroll.create();
            Column.create({space: 16});
                Text.create("视频处理工具");
        Text.fontSize(20)
        Text.fontWeight(FontWeight.Bold)
        Text.pop();

                Text.create("注意：此功能需要设备支持视频编解码");
        Text.fontSize(12)
        Text.fontColor("#666666")
        Text.pop();

                Divider.create();
        Divider.pop();

        Button("选择视频").width("100%").onClick(() => {
    try {
    const photoSelectOptions = {"kind":215,"kindName":"NewExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"photoAccessHelper","text":"photoAccessHelper"},"name":"PhotoSelectOptions"},"arguments":[]};
    photoSelectOptions.MIMEType = photoAccessHelper.PhotoViewMIMETypes.VIDEO_TYPE;
    photoSelectOptions.maxSelectNumber = 1;
    const photoPicker = {"kind":215,"kindName":"NewExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"photoAccessHelper","text":"photoAccessHelper"},"name":"PhotoViewPicker"},"arguments":[]};
    const result = await photoPicker.select(photoSelectOptions);
    if (result && result.photoUris && result.photoUris.length > 0) {
    {"kind":110,"kindName":"ThisKeyword"}.selectedVideoUri = result.photoUris[0];
    const info = await VideoProcessor.getVideoInfo(this.selectedVideoUri);
    {"kind":110,"kindName":"ThisKeyword"}.videoInfo = info;
    {"kind":110,"kindName":"ThisKeyword"}.capturedFrame = "";
    Logger.success("视频选择成功");
    }
    } catch (error) {
    Logger.error("视频选择失败", error);
    }
  });
        If.create();
        if (this.selectedVideoUri) {
          If.branchId(0);
                    Divider.create();
          Divider.pop();

                    Text.create("视频信息");
          Text.fontSize(16)
          Text.fontColor("#333333")
          Text.pop();

          If.create();
          if (this.videoInfo.width > 0) {
            If.branchId(0);
            Text(`分辨率: ${this.videoInfo.width}x${this.videoInfo.height}`).fontSize(14).fontColor("#666666");
          }
          If.pop();
          If.create();
          if (this.videoInfo.duration > 0) {
            If.branchId(0);
            Text(`时长: ${this.videoInfo.durationFormatted}`).fontSize(14).fontColor("#6666");
          }
          If.pop();
          If.create();
          if (this.videoInfo.mimeType) {
            If.branchId(0);
            Text(`格式: ${this.videoInfo.mimeType}`).fontSize(14).fontColor("#6666");
          }
          If.pop();
        }
        If.pop();
        If.create();
        if (this.selectedVideoUri) {
          If.branchId(0);
                    Divider.create();
          Divider.pop();

                    Text.create("视频时长显示");
          Text.fontSize(18)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

                    Row.create({space: 16});
            Button("获取时长").onClick(() => {
    try {
    const duration = await VideoProcessor.getDuration(this.selectedVideoUri);
    {"kind":110,"kindName":"ThisKeyword"}.videoInfo.duration = duration;
    {"kind":110,"kindName":"ThisKeyword"}.videoInfo.durationFormatted = VideoProcessor.formatDuration(duration);
    Logger.success(`获取时长成功：${this.videoInfo.durationFormatted}`);
    } catch (error) {
    Logger.error("获取时长失败", error);
    }
  });
          Row.pop();

                    Divider.create();
          Divider.pop();

                    Text.create("视频截图");
          Text.fontSize(18)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

                    Row.create({space: 16});
            TextInput({placeholder: "截图时间点(毫秒)", text: {"kind":229,"kindName":"TemplateExpression"}}).type(InputType.Number).width(120).onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.captureTime = parseInt(value) || 1000;
  });
            Button("截图").onClick(() => {
    try {
    const frameUri = await VideoProcessor.captureFrame(this.selectedVideoUri, this.captureTime);
    {"kind":110,"kindName":"ThisKeyword"}.capturedFrame = frameUri;
    Logger.success("截图成功");
    } catch (error) {
    Logger.error("截图失败", error);
    }
  });
          Row.pop();

          If.create();
          if (this.capturedFrame) {
            If.branchId(0);
                        Divider.create();
            Divider.pop();

            Image(this.capturedFrame).width(200).height(200).objectFit(ImageFit.Contain).borderRadius(8).margin({top: 8});
          }
          If.pop();
        }
        If.pop();
      Column.pop();

      width("100%").padding(16).justifyContent(FlexAlign.Start);
    Scroll.pop();

  }

  private selectedVideoUri__ = '';

  get selectedVideoUri() {
    return this.selectedVideoUri__.get();
  }

  set selectedVideoUri(newValue) {
    this.selectedVideoUri__.set(newValue);
  }

  private videoInfo__ = {width: 0, height: 0, duration: 0, durationFormatted: "", mimeType: ""};

  get videoInfo() {
    return this.videoInfo__.get();
  }

  set videoInfo(newValue) {
    this.videoInfo__.set(newValue);
  }

  private captureTime__ = 1000;

  get captureTime() {
    return this.captureTime__.get();
  }

  set captureTime(newValue) {
    this.captureTime__.set(newValue);
  }

  private capturedFrame__ = '';

  get capturedFrame() {
    return this.capturedFrame__.get();
  }

  set capturedFrame(newValue) {
    this.capturedFrame__.set(newValue);
  }

}


//# sourceMappingURL=VideoPage.js.map