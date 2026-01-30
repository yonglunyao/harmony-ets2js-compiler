import hilog from '@ohos.hilog';

const TAG: string = 'HarmonyOSUtils';
const DOMAIN: number = 0x0000;

export class Logger {
  static info(message: string): void {
    hilog.info(DOMAIN, TAG, `[INFO] ${message}`);
  }

  static error(message: string, error?: Error | string): void {
    const errorMsg = error ? `${message}: ${typeof error === 'string' ? error : error.message}` : message;
    hilog.error(DOMAIN, TAG, `[ERROR] ${errorMsg}`);
  }

  static success(message: string): void {
    hilog.info(DOMAIN, TAG, `[SUCCESS] ${message}`);
  }

  static warn(message: string): void {
    hilog.warn(DOMAIN, TAG, `[WARN] ${message}`);
  }

  static debug(message: string): void {
    hilog.debug(DOMAIN, TAG, `[DEBUG] ${message}`);
  }
}
