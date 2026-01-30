import { FileManager, FileInfo } from '../services/FileManager';
import { Logger } from '../utils/Logger';

export class FileSystemPage extends View {
  constructor() {
    super();
    this.currentPath__ = this.createState('currentPath', () => this.currentPath);
    this.files__ = this.createState('files', () => this.files);
    this.selectedFile__ = this.createState('selectedFile', () => this.selectedFile);
    this.selectedTargetFile__ = this.createState('selectedTargetFile', () => this.selectedTargetFile);
    this.searchText__ = this.createState('searchText', () => this.searchText);
    this.fileContent__ = this.createState('fileContent', () => this.fileContent);
    this.isDirectoryCreation__ = this.createState('isDirectoryCreation', () => this.isDirectoryCreation);
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
                Scroll.create();
                    Column.create({space: 16});
                        Text.create("文件浏览器");
            Text.fontSize(20)
            Text.fontWeight(FontWeight.Bold)
            Text.pop();

                        Divider.create();
            Divider.pop();

                        Row.create({space: 16});
              Button("刷新").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.listFiles();
  });
              Button("新建文件夹").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.createDirectory();
  });
            Row.pop();

                        Divider.create();
            Divider.pop();

                        Row.create({space: 16});
              TextInput({placeholder: "搜索关键词..."}).width("60%").onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.searchText = value;
  });
              Button("搜索").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.searchFiles();
  });
            Row.pop();

                        Divider.create();
            Divider.pop();

                        Row.create({space: 16});
              Button("选择源文件").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.selectSourceFile();
  });
              Button("选择目标文件").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.selectTargetFile();
  });
            Row.pop();

                        Divider.create();
            Divider.pop();

                        Text.create("当前路径:");
            Text.fontSize(12)
            Text.fontColor("#666666")
            Text.margin({left: 16})
            Text.pop();

            Text(`${this.currentPath}`).fontSize(14).fontColor("#666666");
                        Divider.create();
            Divider.pop();

                        Row.create({space: 16});
              Button("清空路径").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.currentPath = "/data/storage/el2/base/haps/entry/files";
  });
              Button("根目录").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.currentPath = "/";
  });
            Row.pop();

                        Divider.create();
            Divider.pop();

                        Text.create("注意：默认路径为：`/data/storage/el2/base/haps/entry/files`");
            Text.fontSize(12)
            Text.fontColor("#666666")
            Text.margin({left: 16})
            Text.pop();

          Column.pop();

          width("100%").padding(16);
        Scroll.pop();

      TabContent.pop();

      tabBar(this.TabBar(0, '📁', '浏览'));
            TabContent.create();
                Scroll.create();
                    Column.create({space: 16});
                        Text.create("文件操作");
            Text.fontSize(20)
            Text.fontWeight(FontWeight.Bold)
            Text.pop();

                        Divider.create();
            Divider.pop();

                        Row.create({space: 16});
              Button("选择源文件").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.selectSourceFile();
  });
              Button("选择目标文件").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.selectTargetFile();
  });
            Row.pop();

                        Divider.create();
            Divider.pop();

            Button("文件操作").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.showFileOptions();
  });
                        Divider.create();
            Divider.pop();

                        Row.create({space: 16});
              Button("刷新").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.listFiles();
  });
            Row.pop();

                        Divider.create();
            Divider.pop();

                        Text.create("文件搜索");
            Text.fontSize(14)
            Text.fontColor("#666666")
            Text.pop();

            TextInput({placeholder: "搜索关键词..."}).width("60%").onChange((value) => {
    {"kind":110,"kindName":"ThisKeyword"}.searchText = value;
  });
            Button("搜索").onClick(() => {
    {"kind":110,"kindName":"ThisKeyword"}.searchFiles();
  });
          Column.pop();

          width("100%").padding(16);
        Scroll.pop();

      TabContent.pop();

      tabBar(this.TabBar(1, '📝', '操作'));
            TabContent.create();
                Scroll.create();
                    Column.create({space: 16});
                        Text.create("文件信息");
            Text.fontSize(20)
            Text.fontColor("#6666")
            Text.pop();

                        Divider.create();
            Divider.pop();

            If.create();
            if (this.selectedFile) {
              If.branchId(0);
                            Text.create("文件名:");
              Text.fontSize(12)
              Text.fontColor("#6666")
              Text.margin({left: 16})
              Text.pop();

              Text(`${this.selectedFile.name}`).fontSize(14).fontColor("#6666").margin({left: 16});
                            Divider.create();
              Divider.pop();

                            Text.create("文件大小:");
              Text.fontSize(12)
              Text.fontColor("#6666")
              Text.margin({left: 16})
              Text.pop();

              Text(`${this.selectedFile.size} bytes`).fontSize(14).fontColor("#6666").margin({left: 16});
                            Divider.create();
              Divider.pop();

                            Text.create("文件类型:");
              Text.fontSize(12)
              Text.fontColor("#6666")
              Text.margin({left: 16})
              Text.pop();

              Text(`${this.selectedFile.isDirectory ? '目录' : '文件'}`).fontSize(14).fontColor("#6666").margin({left: 16});
                            Divider.create();
              Divider.pop();

              Button("删除文件").onClick(() => {
    if ({"kind":110,"kindName":"ThisKeyword"}.selectedFile) {
    const success = await FileManager.deleteFile(this.selectedFile.uri);
    if (success) {
    Logger.success("删除文件成功");
    {"kind":110,"kindName":"ThisKeyword"}.selectedFile = null;
    {"kind":110,"kindName":"ThisKeyword"}.files = {"kind":110,"kindName":"ThisKeyword"}.files.filter((f) => f.uri !== {"kind":110,"kindName":"ThisKeyword"}.selectedFile.uri);
    }
    }
  });
            }
            else {
              If.branchId(1);
                            Text.create("未选择文件");
              Text.fontSize(14)
              Text.fontColor("#999999")
              Text.pop();

            }
            If.pop();
                        Divider.create();
            Divider.pop();

                        Text.create("文件内容:");
            Text.fontSize(14)
            Text.fontColor("#6666")
            Text.margin({left: 16})
            Text.pop();

            Text(this.fileContent).width("100%").maxLines(10).fontColor("#6666").padding(12).borderRadius(8).backgroundColor("#F5F5F5");
          Column.pop();

          width("100%").padding(16);
        Scroll.pop();

      TabContent.pop();

      tabBar(this.TabBar(2, 'ℹ️', '信息'));
    Tabs.pop();

    barWidth(100).animationDuration(300);
  }

  listFiles() {
    FileManager.listFiles(this.currentPath).then((files) => {
    {"kind":110,"kindName":"ThisKeyword"}.files = files;
    Logger.success(`列出文件成功，共 ${files.length} 个文件`);
  }).catch((error) => {
    Logger.error("列出文件失败", error);
  });
  }

  createDirectory() {
    FileManager.createDirectory(this.currentPath + '/new_folder').then(() => {
    Logger.success("创建目录成功");
    {"kind":110,"kindName":"ThisKeyword"}.listFiles();
  }).catch((error) => {
    Logger.error("创建目录失败", error);
  });
  }

  selectSourceFile() {
    Logger.info("选择源文件功能待实现");
  }

  selectTargetFile() {
    Logger.info("选择目标文件功能待实现");
  }

  showFileOptions() {
    Logger.info("文件操作功能待实现");
  }

  searchFiles() {
    Logger.info("搜索文件功能待实现");
  }

  onBackPressed() {
    Logger.info("返回功能待实现");
  }

  private currentPath__ = /data/storage/el2/base/haps/entry/files;

  get currentPath() {
    return this.currentPath__.get();
  }

  set currentPath(newValue) {
    this.currentPath__.set(newValue);
  }

  private files__ = [];

  get files() {
    return this.files__.get();
  }

  set files(newValue) {
    this.files__.set(newValue);
  }

  private selectedFile__ = null;

  get selectedFile() {
    return this.selectedFile__.get();
  }

  set selectedFile(newValue) {
    this.selectedFile__.set(newValue);
  }

  private selectedTargetFile__ = '';

  get selectedTargetFile() {
    return this.selectedTargetFile__.get();
  }

  set selectedTargetFile(newValue) {
    this.selectedTargetFile__.set(newValue);
  }

  private searchText__ = '';

  get searchText() {
    return this.searchText__.get();
  }

  set searchText(newValue) {
    this.searchText__.set(newValue);
  }

  private fileContent__ = '';

  get fileContent() {
    return this.fileContent__.get();
  }

  set fileContent(newValue) {
    this.fileContent__.set(newValue);
  }

  private isDirectoryCreation__ = false;

  get isDirectoryCreation() {
    return this.isDirectoryCreation__.get();
  }

  set isDirectoryCreation(newValue) {
    this.isDirectoryCreation__.set(newValue);
  }

}


//# sourceMappingURL=FileSystemPage.js.map