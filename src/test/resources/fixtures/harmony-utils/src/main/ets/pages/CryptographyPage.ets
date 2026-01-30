import { CryptographyUtil } from '../services/CryptographyUtil';
import { Logger } from '../utils/Logger';

@Entry
@Component
export struct CryptographyPage {
  @State currentTab: number = 0;
  @State inputText: string = '';
  @State outputText: string = '';
  @State keyText: string = '';
  @State hashResult: string = '';
  @State base64Input: string = '';
  @State base64Result: string = '';

  @Builder
  TabBar(index: number, icon: string, label: string) {
    Column() {
      Text(icon)
        .fontSize(24)
      Text(label)
        .fontSize(12)
        .margin({ top: 4 })
    }
    .padding({ top: 8, bottom: 8 })
    .width('100%')
    .justifyContent(FlexAlign.Center)
  }

  build() {
    Tabs({ barPosition: BarPosition.Start }) {
      TabContent() {
        Column({ space: 16 }) {
          Text('AES 加密/解密')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)

          TextInput({ placeholder: '输入文本' })
            .width('100%')
            .height(50)
            .onChange((value) => {
              this.inputText = value;
            })

          TextInput({ placeholder: '输入密钥（Base64编码）' })
            .width('100%')
            .height(50)
            .onChange((value) => {
              this.keyText = value;
            })

          Row({ space: 16 }) {
            Button('生成密钥')
              .onClick(async () => {
                this.keyText = await CryptographyUtil.generateAESKey();
              })

            Button('加密')
              .onClick(async () => {
                if (this.inputText && this.keyText) {
                  this.outputText = await CryptographyUtil.encryptAES(this.inputText, this.keyText);
                }
              })

            Button('解密')
              .onClick(async () => {
                if (this.inputText && this.keyText) {
                  this.outputText = await CryptographyUtil.decryptAES(this.inputText, this.keyText);
                }
              })
          }

          Text('结果:')
            .fontSize(14)

          Text(this.outputText)
            .width('100%')
            .maxLines(10)
            .fontSize(14)
            .padding(12)
            .borderRadius(8)
            .backgroundColor('#F5F5F5')
        }
        .width('100%')
        .height('100%')
        .padding(16)
      }
      .tabBar(this.TabBar(0, '🔐', 'AES'))

      TabContent() {
        Column({ space: 16 }) {
          Text('哈希计算')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)

          TextInput({ placeholder: '输入文本' })
            .width('100%')
            .height(50)
            .onChange((value) => {
              this.inputText = value;
            })

          Row({ space: 16 }) {
            Button('SHA256')
              .onClick(async () => {
                if (this.inputText) {
                  this.hashResult = await CryptographyUtil.sha256(this.inputText);
                }
              })

            Button('MD5')
              .onClick(async () => {
                if (this.inputText) {
                  this.hashResult = await CryptographyUtil.md5(this.inputText);
                }
              })
          }

          Text('哈希结果:')
            .fontSize(14)

          Text(this.hashResult)
            .width('100%')
            .maxLines(10)
            .fontSize(14)
            .padding(12)
            .borderRadius(8)
            .backgroundColor('#F5F5F5')
        }
        .width('100%')
        .height('100%')
        .padding(16)
      }
      .tabBar(this.TabBar(1, '🔒', '哈希'))

      TabContent() {
        Column({ space: 16 }) {
          Text('Base64 编码/解码')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)

          TextInput({ placeholder: '输入文本' })
            .width('100%')
            .height(50)
            .onChange((value) => {
              this.base64Input = value;
            })

          Row({ space: 16 }) {
            Button('编码')
              .onClick(() => {
                if (this.base64Input) {
                  this.base64Result = CryptographyUtil.encodeBase64(this.base64Input);
                }
              })

            Button('解码')
              .onClick(() => {
                if (this.base64Input) {
                  this.base64Result = CryptographyUtil.decodeBase64(this.base64Input);
                }
              })
          }

          Text('结果:')
            .fontSize(14)

          Text(this.base64Result)
            .width('100%')
            .maxLines(10)
            .fontSize(14)
            .padding(12)
            .borderRadius(8)
            .backgroundColor('#F5F5F5')
        }
        .width('100%')
        .height('100%')
        .padding(16)
      }
      .tabBar(this.TabBar(2, '📝', 'Base64'))
    }
    .barWidth(100)
    .animationDuration(300)
  }
}
