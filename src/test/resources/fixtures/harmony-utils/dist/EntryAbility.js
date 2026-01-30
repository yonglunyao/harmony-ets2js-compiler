import { AbilityConstant, ConfigurationConstant, UIAbility, Want } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { window } from '@kit.ArkUI';

const DOMAIN = 0;
export class EntryAbility {
  onCreate(want /* Want */, launchParam /* AbilityConstant.LaunchParam */) {
    try {
  {"kind":110,"kindName":"ThisKeyword"}.context.getApplicationContext().setColorMode(ConfigurationConstant.ColorMode.COLOR_MODE_NOT_SET);
} catch (err) {
  hilog.error(DOMAIN, "testTag", "Failed to set colorMode. Cause: %{public}s", JSON.stringify(err));
}
    hilog.info(DOMAIN, "testTag", "%{public}s", "Ability onCreate");
  }

  onDestroy() {
    hilog.info(DOMAIN, "testTag", "%{public}s", "Ability onDestroy");
  }

  onWindowStageCreate(windowStage /* window.WindowStage */) {
    hilog.info(DOMAIN, "testTag", "%{public}s", "Ability onWindowStageCreate");
    windowStage.loadContent("pages/Index", (err) => {
    if (err.code) {
    hilog.error(DOMAIN, "testTag", "Failed to load the content. Cause: %{public}s", JSON.stringify(err));
    return;
    }
    hilog.info(DOMAIN, "testTag", "Succeeded in loading the content.");
  });
  }

  onWindowStageDestroy() {
    hilog.info(DOMAIN, "testTag", "%{public}s", "Ability onWindowStageDestroy");
  }

  onForeground() {
    hilog.info(DOMAIN, "testTag", "%{public}s", "Ability onForeground");
  }

  onBackground() {
    hilog.info(DOMAIN, "testTag", "%{public}s", "Ability onBackground");
  }

}


//# sourceMappingURL=EntryAbility.js.map