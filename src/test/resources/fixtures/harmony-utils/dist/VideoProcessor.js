import media from '@ohos.multimedia.media';
import image from '@ohos.multimedia.image';
import { Logger } from '../utils/Logger';

export class VideoProcessor {
  TAG = "VideoProcessor";

  static async getVideoInfo(videoUri /* string */) {
    try {
  const avPlayer = await media.createAVPlayer();
  avPlayer.url = videoUri;
  const infoPromise = new Promise(/* arrow function */);
  avPlayer.prepare();
  const result = await infoPromise;
  Logger.success("获取视频信息成功");
  return result;
} catch (error) {
  Logger.error("获取视频信息失败", error);
  return {width: 0, height: 0, duration: 0, durationFormatted: "", mimeType: ""};
}
  }

  static async captureFrame(videoUri /* string */, timeMs /* number */) {
    try {
  const avPlayer = await media.createAVPlayer();
  avPlayer.url = videoUri;
  const framePromise = new Promise(/* arrow function */);
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
  const durationPromise = new Promise(/* arrow function */);
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
      return `${hours}:${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
    }
    else {
      return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
    }
  }

  static isVideoFile(mimeType /* string */) {
    const videoMimeTypes = ["video/mp4", "video/3gp", "video/mpeg", "video/webm", "video/matroska"];
    return videoMimeTypes.includes(mimeType) || mimeType.startsWith("video/");
  }

}


//# sourceMappingURL=VideoProcessor.js.map