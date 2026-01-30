import { CryptographyPage } from './CryptographyPage';
import { ImagePage } from './ImagePage';
import { FileSystemPage } from './FileSystemPage';
import { VideoPage } from './VideoPage';

@Entry
@Component
struct Index {
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

  @Builder
  FeatureCard(icon: string, description: string) {
    Row({ space: 12 }) {
      Text(icon)
        .fontSize(28)
      Text(description)
        .fontSize(14)
        .layoutWeight(1)
        .fontColor('#333333')
    }
    .width('100%')
    .padding(16)
    .backgroundColor('#F9F9F9')
    .borderRadius(12)
  }

  build() {
    Tabs({ barPosition: BarPosition.Start }) {
      TabContent() {
        Column({ space: 20 }) {
          Text('HarmonyOS 工具集')
            .fontSize(24)
            .fontWeight(FontWeight.Bold)

          Text('一站式工具应用，包含图像处理、密码学、文件管理、视频处理、NFC等功能')
            .fontSize(14)
            .fontColor('#666666')

          Column({ space: 16 }) {
            this.FeatureCard('🔐 密码学', 'AES加密/解密、SHA256/MD5哈希、Base64编解码')
            this.FeatureCard('🖼️ 图像处理', '图像压缩、裁剪、旋转、滤镜效果、格式转换')
            this.FeatureCard('📁 文件管理', '文件浏览、复制/移动/删除、信息查看、搜索')
            this.FeatureCard('🎬 视频处理', '视频信息获取、时长显示、视频截图')
            this.FeatureCard('📡 NFC', 'NFC标签读取、写入、状态监控')
          }
          .width('100%')

          Column({ space: 8 }) {
            Text('提示')
              .fontSize(14)
              .fontWeight(FontWeight.Medium)

            Text('• 点击上方标签页切换到不同功能模块')
              .fontSize(12)
              .fontColor('#666666')

            Text('• 部分功能需要相应的设备支持（如NFC）')
              .fontSize(12)
              .fontColor('#666666')

            Text('• 操作结果会通过日志记录，可使用hilog查看')
              .fontSize(12)
              .fontColor('#666666')
          }
          .width('100%')
          .padding(16)
          .backgroundColor('#FFF8E1')
          .borderRadius(8)
        }
        .width('100%')
        .height('100%')
        .padding(20)
        .justifyContent(FlexAlign.Start)
      }
      .tabBar(this.TabBar(0, '🏠', '首页'))

      TabContent() {
        ImagePage()
      }
      .tabBar(this.TabBar(1, '🖼️', '图像'))

      TabContent() {
        CryptographyPage()
      }
      .tabBar(this.TabBar(2, '🔐', '密码学'))

      TabContent() {
        FileSystemPage()
      }
      .tabBar(this.TabBar(3, '📁', '文件'))

      TabContent() {
        VideoPage()
      }
      .tabBar(this.TabBar(4, '🎬', '视频'))

      TabContent() {
        Column() {
          Text('NFC 工具')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)

          Text('注意：此功能需要设备支持NFC')
            .fontSize(12)
            .fontColor('#666666')
        }
        .width('100%')
        .height('100%')
        .padding(16)
        .justifyContent(FlexAlign.Center)
      }
      .tabBar(this.TabBar(5, '📡', 'NFC'))
    }
    .barWidth(100)
    .animationDuration(300)
  }
}
