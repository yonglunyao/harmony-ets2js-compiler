import { CryptographyUtil } from '../services/CryptographyUtil';
import { Logger } from '../utils/Logger';

export class CryptographyPage extends View {
  constructor() {
    super();
    this.currentTab__ = this.createState('currentTab', () => this.currentTab);
    this.inputText__ = this.createState('inputText', () => this.inputText);
    this.outputText__ = this.createState('outputText', () => this.outputText);
    this.keyText__ = this.createState('keyText', () => this.keyText);
    this.hashResult__ = this.createState('hashResult', () => this.hashResult);
    this.base64Input__ = this.createState('base64Input', () => this.base64Input);
    this.base64Result__ = this.createState('base64Result', () => this.base64Result);
  }

  TabBar(index /* number */, icon /* string */, label /* string */) {
        Column.create();
            Text.create(icon);
      Text.fontSize(24)
      Text.pop();

            Text.create(label);
      Text.fontSize(12)
      Text.margin({top: 4})
      Text.pop();

    Column.pop();

    padding({top: 8, bottom: 8}).width("100%").justifyContent(FlexAlign.Center);
  }

  initialRender() {
        Tabs.create({barPosition: BarPosition.Start});
            TabContent.create();
                Column.create({space: 16});
                    Text.create("AES 加密/解密");
          Text.fontSize(20)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

          TextInput({placeholder: "输入文本"}).width("100%").height(50).onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.inputText = value;
  });
          TextInput({placeholder: "输入密钥（Base64编码）"}).width("100%").height(50).onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.keyText = value;
  });
                    Row.create({space: 16});
            Button("生成密钥").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.keyText = await CryptographyUtil.generateAESKey();
  });
            Button("加密").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.inputText && {"kind":110,"kindName":"ThisKeyword"}.keyText) {
    {"kind":110,"kindName":"ThisKeyword"}.outputText = await CryptographyUtil.encryptAES(this.inputText, this.keyText);
    }
  });
            Button("解密").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.inputText && {"kind":110,"kindName":"ThisKeyword"}.keyText) {
    {"kind":110,"kindName":"ThisKeyword"}.outputText = await CryptographyUtil.decryptAES(this.inputText, this.keyText);
    }
  });
          Row.pop();

                    Text.create("结果:");
          Text.fontSize(14)
          Text.pop();

          Text(this.outputText).width("100%").maxLines(10).fontSize(14).padding(12).borderRadius(8).backgroundColor("#F5F5F5");
        Column.pop();

        width("100%").height("100%").padding(16);
      TabContent.pop();

      tabBar(this.TabBar(0, '🔐', 'AES'));
            TabContent.create();
                Column.create({space: 16});
                    Text.create("哈希计算");
          Text.fontSize(20)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

          TextInput({placeholder: "输入文本"}).width("100%").height(50).onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.inputText = value;
  });
                    Row.create({space: 16});
            Button("SHA256").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.inputText) {
    {"kind":110,"kindName":"ThisKeyword"}.hashResult = await CryptographyUtil.sha256(this.inputText);
    }
  });
            Button("MD5").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.inputText) {
    {"kind":110,"kindName":"ThisKeyword"}.hashResult = await CryptographyUtil.md5(this.inputText);
    }
  });
          Row.pop();

                    Text.create("哈希结果:");
          Text.fontSize(14)
          Text.pop();

          Text(this.hashResult).width("100%").maxLines(10).fontSize(14).padding(12).borderRadius(8).backgroundColor("#F5F5F5");
        Column.pop();

        width("100%").height("100%").padding(16);
      TabContent.pop();

      tabBar(this.TabBar(1, '🔒', '哈希'));
            TabContent.create();
                Column.create({space: 16});
                    Text.create("Base64 编码/解码");
          Text.fontSize(20)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

          TextInput({placeholder: "输入文本"}).width("100%").height(50).onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.base64Input = value;
  });
                    Row.create({space: 16});
            Button("编码").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.base64Input) {
    {"kind":110,"kindName":"ThisKeyword"}.base64Result = CryptographyUtil.encodeBase64(this.base64Input);
    }
  });
            Button("解码").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.base64Input) {
    {"kind":110,"kindName":"ThisKeyword"}.base64Result = CryptographyUtil.decodeBase64(this.base64Input);
    }
  });
          Row.pop();

                    Text.create("结果:");
          Text.fontSize(14)
          Text.pop();

          Text(this.base64Result).width("100%").maxLines(10).fontSize(14).padding(12).borderRadius(8).backgroundColor("#F5F5F5");
        Column.pop();

        width("100%").height("100%").padding(16);
      TabContent.pop();

      tabBar(this.TabBar(2, '📝', 'Base64'));
    Tabs.pop();

    barWidth(100).animationDuration(300);
  }

  private currentTab__ = 0;

  get currentTab() {
    return this.currentTab__.get();
  }

  set currentTab(newValue) {
    this.currentTab__.set(newValue);
  }

  private inputText__ = '';

  get inputText() {
    return this.inputText__.get();
  }

  set inputText(newValue) {
    this.inputText__.set(newValue);
  }

  private outputText__ = '';

  get outputText() {
    return this.outputText__.get();
  }

  set outputText(newValue) {
    this.outputText__.set(newValue);
  }

  private keyText__ = '';

  get keyText() {
    return this.keyText__.get();
  }

  set keyText(newValue) {
    this.keyText__.set(newValue);
  }

  private hashResult__ = '';

  get hashResult() {
    return this.hashResult__.get();
  }

  set hashResult(newValue) {
    this.hashResult__.set(newValue);
  }

  private base64Input__ = '';

  get base64Input() {
    return this.base64Input__.get();
  }

  set base64Input(newValue) {
    this.base64Input__.set(newValue);
  }

  private base64Result__ = '';

  get base64Result() {
    return this.base64Result__.get();
  }

  set base64Result(newValue) {
    this.base64Result__.set(newValue);
  }

}


//# sourceMappingURL=CryptographyPage.js.map