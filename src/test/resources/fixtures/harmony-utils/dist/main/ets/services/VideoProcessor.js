import media from '@ohos.multimedia.media';
import image from '@ohos.multimedia.image';
import { Logger } from '../utils/Logger';

export class VideoProcessor {
  TAG = VideoProcessor;

  static async getVideoInfo(videoUri /* string */) {
    try {
  const avPlayer = await media.createAVPlayer();
  avPlayer.url = videoUri;
  const infoPromise = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Promise","text":"Promise"},"arguments":[{"kind":220,"kindName":"ArrowFunction","parameters":[{"name":"resolve","type":""}],"body":{"kind":242,"kindName":"Block","statements":[{"kind":245,"kindName":"ExpressionStatement","expression":{"kind":214,"kindName":"CallExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"avPlayer","text":"avPlayer"},"name":"on","text":"avPlayer.on"},"arguments":["\"stateChange\"","(state) => {\n    if (state === \"initialized\") {\n    await avPlayer.prepare();\n    const duration = avPlayer.duration;\n    resolve(\"{width: 1920, height: 1080, duration: duration, durationFormatted: VideoProcessor.formatDuration(duration), mimeType: \\\"video/mp4\\\"}\");\n    await avPlayer.release();\n    }\n  }"]}}]},"text":"(resolve) => {\n    avPlayer.on(\"stateChange\", (state) => {\n    if (state === \"initialized\") {\n    await avPlayer.prepare();\n    const duration = avPlayer.duration;\n    resolve(\"{width: 1920, height: 1080, duration: duration, durationFormatted: VideoProcessor.formatDuration(duration), mimeType: \\\"video/mp4\\\"}\");\n    await avPlayer.release();\n    }\n    });\n  }"}]};
  avPlayer.prepare();
  const result = await infoPromise;
  Logger.success("获取视频信息成功");
  return result;
} catch (error) {
  Logger.error("获取视频信息失败", error);
  return {width: , height: , duration: , durationFormatted: , mimeType: };
}
  }

  static async captureFrame(videoUri /* string */, timeMs /* number */) {
    try {
  const avPlayer = await media.createAVPlayer();
  avPlayer.url = videoUri;
  const framePromise = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Promise","text":"Promise"},"arguments":[{"kind":220,"kindName":"ArrowFunction","parameters":[{"name":"resolve","type":""}],"body":{"kind":242,"kindName":"Block","statements":[{"kind":245,"kindName":"ExpressionStatement","expression":{"kind":214,"kindName":"CallExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"avPlayer","text":"avPlayer"},"name":"on","text":"avPlayer.on"},"arguments":["\"stateChange\"","(state) => {\n    if (state === \"initialized\") {\n    await avPlayer.prepare();\n    await avPlayer.seek(timeMs, media.SeekMode.SEEK_NEXT_SYNC);\n    avPlayer.setSpeed(media.PlaybackSpeed.SPEED_FORWARD_1_00_X);\n    Logger.success(`视频截图功能已调用，时间点: ${timeMs}ms`);\n    resolve(`data:image/jpeg;base64,screenshot_placeholder_${timeMs}`);\n    await avPlayer.release();\n    }\n  }"]}}]},"text":"(resolve) => {\n    avPlayer.on(\"stateChange\", (state) => {\n    if (state === \"initialized\") {\n    await avPlayer.prepare();\n    await avPlayer.seek(timeMs, media.SeekMode.SEEK_NEXT_SYNC);\n    avPlayer.setSpeed(media.PlaybackSpeed.SPEED_FORWARD_1_00_X);\n    Logger.success(`视频截图功能已调用，时间点: ${timeMs}ms`);\n    resolve(`data:image/jpeg;base64,screenshot_placeholder_${timeMs}`);\n    await avPlayer.release();\n    }\n    });\n  }"}]};
  avPlayer.prepare();
  const result = await framePromise;
  return result;
} catch (error) {
  Logger.error("视频截图失败", error);
  return "";
}
  }

  static async getDuration(videoUri /* string */) {
    try {
  const avPlayer = await media.createAVPlayer();
  avPlayer.url = videoUri;
  const durationPromise = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Promise","text":"Promise"},"arguments":[{"kind":220,"kindName":"ArrowFunction","parameters":[{"name":"resolve","type":""}],"body":{"kind":242,"kindName":"Block","statements":[{"kind":245,"kindName":"ExpressionStatement","expression":{"kind":214,"kindName":"CallExpression","expression":{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"avPlayer","text":"avPlayer"},"name":"on","text":"avPlayer.on"},"arguments":["\"stateChange\"","(state) => {\n    if (state === \"initialized\") {\n    await avPlayer.prepare();\n    const duration = avPlayer.duration;\n    resolve(duration);\n    await avPlayer.release();\n    }\n  }"]}}]},"text":"(resolve) => {\n    avPlayer.on(\"stateChange\", (state) => {\n    if (state === \"initialized\") {\n    await avPlayer.prepare();\n    const duration = avPlayer.duration;\n    resolve(duration);\n    await avPlayer.release();\n    }\n    });\n  }"}]};
  avPlayer.prepare();
  const result = await durationPromise;
  Logger.success(`获取视频时长成功: ${result}ms`);
  return result;
} catch (error) {
  Logger.error("获取视频时长失败", error);
  return 0;
}
  }

  static formatDuration(milliseconds /* number */) {
    const totalSeconds = Math.floor(milliseconds / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
    if (hours > 0) {
      return {"kind":229,"kindName":"TemplateExpression"}
    }
    else {
      return {"kind":229,"kindName":"TemplateExpression"}
    }
  }

  static isVideoFile(mimeType /* string */) {
    const videoMimeTypes = ["video/mp4", "video/3gp", "video/mpeg", "video/webm", "video/matroska"];
    return videoMimeTypes.includes(mimeType) || mimeType.startsWith("video/");
  }

}


//# sourceMappingURL=VideoProcessor.js.map