import hilog from '@ohos.hilog';

const TAG = "HarmonyOSUtils";
const DOMAIN = 0;
export class Logger {
  static info(message /* string */) {
    hilog.info(DOMAIN, TAG, `[INFO] ${message}`);
  }

  static error(message /* string */, error /* Error | string */) {
    const errorMsg = error ? {"kind":229,"kindName":"TemplateExpression"} : message;
    hilog.error(DOMAIN, TAG, `[ERROR] ${errorMsg}`);
  }

  static success(message /* string */) {
    hilog.info(DOMAIN, TAG, `[SUCCESS] ${message}`);
  }

  static warn(message /* string */) {
    hilog.warn(DOMAIN, TAG, `[WARN] ${message}`);
  }

  static debug(message /* string */) {
    hilog.debug(DOMAIN, TAG, `[DEBUG] ${message}`);
  }

}


//# sourceMappingURL=Logger.js.map