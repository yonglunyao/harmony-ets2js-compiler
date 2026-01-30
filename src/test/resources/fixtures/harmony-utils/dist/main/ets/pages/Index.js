import { CryptographyPage } from './CryptographyPage';
import { ImagePage } from './ImagePage';
import { FileSystemPage } from './FileSystemPage';
import { VideoPage } from './VideoPage';

class Index extends View {
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

  FeatureCard(icon /* string */, description /* string */) {
        Row.create({space: 12});
            Text.create(icon);
      Text.fontSize(28)
      Text.pop();

            Text.create(description);
      Text.fontSize(14)
      Text.layoutWeight(1)
      Text.fontColor("#333333")
      Text.pop();

    Row.pop();

    width("100%").padding(16).backgroundColor("#F9F9F9").borderRadius(12);
  }

  initialRender() {
        Tabs.create({barPosition: BarPosition.Start});
            TabContent.create();
                Column.create({space: 20});
                    Text.create("HarmonyOS 工具集");
          Text.fontSize(24)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

                    Text.create("一站式工具应用，包含图像处理、密码学、文件管理、视频处理、NFC等功能");
          Text.fontSize(14)
          Text.fontColor("#666666")
          Text.pop();

                    Column.create({space: 16});
            this.FeatureCard("🔐 密码学", "AES加密/解密、SHA256/MD5哈希、Base64编解码");
            this.FeatureCard("🖼️ 图像处理", "图像压缩、裁剪、旋转、滤镜效果、格式转换");
            this.FeatureCard("📁 文件管理", "文件浏览、复制/移动/删除、信息查看、搜索");
            this.FeatureCard("🎬 视频处理", "视频信息获取、时长显示、视频截图");
            this.FeatureCard("📡 NFC", "NFC标签读取、写入、状态监控");
          Column.pop();

          width("100%");
                    Column.create({space: 8});
                        Text.create("提示");
            Text.fontSize(14)
            Text.fontWeight(FontWeight.Medium)
            Text.pop();

                        Text.create("• 点击上方标签页切换到不同功能模块");
            Text.fontSize(12)
            Text.fontColor("#666666")
            Text.pop();

                        Text.create("• 部分功能需要相应的设备支持（如NFC）");
            Text.fontSize(12)
            Text.fontColor("#666666")
            Text.pop();

                        Text.create("• 操作结果会通过日志记录，可使用hilog查看");
            Text.fontSize(12)
            Text.fontColor("#666666")
            Text.pop();

          Column.pop();

          width("100%").padding(16).backgroundColor("#FFF8E1").borderRadius(8);
        Column.pop();

        width("100%").height("100%").padding(20).justifyContent(FlexAlign.Start);
      TabContent.pop();

      tabBar(this.TabBar(0, '🏠', '首页'));
            TabContent.create();
        ImagePage();
      TabContent.pop();

      tabBar(this.TabBar(1, '🖼️', '图像'));
            TabContent.create();
        CryptographyPage();
      TabContent.pop();

      tabBar(this.TabBar(2, '🔐', '密码学'));
            TabContent.create();
        FileSystemPage();
      TabContent.pop();

      tabBar(this.TabBar(3, '📁', '文件'));
            TabContent.create();
        VideoPage();
      TabContent.pop();

      tabBar(this.TabBar(4, '🎬', '视频'));
            TabContent.create();
                Column.create();
                    Text.create("NFC 工具");
          Text.fontSize(20)
          Text.fontWeight(FontWeight.Bold)
          Text.pop();

                    Text.create("注意：此功能需要设备支持NFC");
          Text.fontSize(12)
          Text.fontColor("#666666")
          Text.pop();

        Column.pop();

        width("100%").height("100%").padding(16).justifyContent(FlexAlign.Center);
      TabContent.pop();

      tabBar(this.TabBar(5, '📡', 'NFC'));
    Tabs.pop();

    barWidth(100).animationDuration(300);
  }

}


//# sourceMappingURL=Index.js.map