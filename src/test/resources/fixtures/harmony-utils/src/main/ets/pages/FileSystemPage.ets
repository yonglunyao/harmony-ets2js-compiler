import { FileManager, FileInfo } from '../services/FileManager';
import { Logger } from '../utils/Logger';

@Entry
@Component
export struct FileSystemPage {
  @State currentPath: string = '/data/storage/el2/base/haps/entry/files';
  @State files: FileInfo[] = [];
  @State selectedFile: FileInfo | null = null;
  @State selectedTargetFile: string = '';
  @State searchText: string = '';
  @State fileContent: string = '';
  @State isDirectoryCreation: boolean = false;

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
        Scroll() {
          Column({ space: 16 }) {
            Text('文件浏览器')
              .fontSize(20)
              .fontWeight(FontWeight.Bold)

            Divider()

            Row({ space: 16 }) {
              Button('刷新')
                .onClick(() => {
                  this.listFiles();
                })

              Button('新建文件夹')
                .onClick(() => {
                  this.createDirectory();
                })
            }

            Divider()

            Row({ space: 16 }) {
              TextInput({ placeholder: '搜索关键词...' })
                .width('60%')
                .onChange((value) => {
                  this.searchText = value;
                })

              Button('搜索')
                .onClick(() => {
                  this.searchFiles();
                })
            }

            Divider()

            Row({ space: 16 }) {
              Button('选择源文件')
                .onClick(() => {
                  this.selectSourceFile();
                })

              Button('选择目标文件')
                .onClick(() => {
                  this.selectTargetFile();
                })
            }

            Divider()

            Text('当前路径:')
              .fontSize(12)
              .fontColor('#666666')
              .margin({ left: 16 })

            Text(`${this.currentPath}`)
              .fontSize(14)
              .fontColor('#666666')

            Divider()

            Row({ space: 16 }) {
              Button('清空路径')
                .onClick(() => {
                  this.currentPath = '/data/storage/el2/base/haps/entry/files';
                })

              Button('根目录')
                .onClick(() => {
                  this.currentPath = '/';
                })
            }

            Divider()

            Text('注意：默认路径为：`/data/storage/el2/base/haps/entry/files`')
              .fontSize(12)
              .fontColor('#666666')
              .margin({ left: 16 })
          }
          .width('100%')
          .padding(16)
        }
      }
      .tabBar(this.TabBar(0, '📁', '浏览'))

      TabContent() {
        Scroll() {
          Column({ space: 16 }) {
            Text('文件操作')
              .fontSize(20)
              .fontWeight(FontWeight.Bold)

            Divider()

            Row({ space: 16 }) {
              Button('选择源文件')
                .onClick(() => {
                  this.selectSourceFile();
                })

              Button('选择目标文件')
                .onClick(() => {
                  this.selectTargetFile();
                })
            }

            Divider()

            Button('文件操作')
              .onClick(() => {
                this.showFileOptions();
              })

            Divider()

            Row({ space: 16 }) {
              Button('刷新')
                .onClick(() => {
                  this.listFiles();
                })
            }

            Divider()

            Text('文件搜索')
              .fontSize(14)
              .fontColor('#666666')

            TextInput({ placeholder: '搜索关键词...' })
              .width('60%')
              .onChange((value) => {
                this.searchText = value;
              })

            Button('搜索')
              .onClick(() => {
                this.searchFiles();
              })
          }
          .width('100%')
          .padding(16)
        }
      }
      .tabBar(this.TabBar(1, '📝', '操作'))

      TabContent() {
        Scroll() {
          Column({ space: 16 }) {
            Text('文件信息')
              .fontSize(20)
              .fontColor('#6666')

            Divider()

            if (this.selectedFile) {
              Text('文件名:')
                .fontSize(12)
                .fontColor('#6666')
                .margin({ left: 16 })

              Text(`${this.selectedFile.name}`)
                .fontSize(14)
                .fontColor('#6666')
                .margin({ left: 16 })

              Divider()

              Text('文件大小:')
                .fontSize(12)
                .fontColor('#6666')
                .margin({ left: 16 })

              Text(`${this.selectedFile.size} bytes`)
                .fontSize(14)
                .fontColor('#6666')
                .margin({ left: 16 })

              Divider()

              Text('文件类型:')
                .fontSize(12)
                .fontColor('#6666')
                .margin({ left: 16 })

              Text(`${this.selectedFile.isDirectory ? '目录' : '文件'}`)
                .fontSize(14)
                .fontColor('#6666')
                .margin({ left: 16 })

              Divider()

              Button('删除文件')
                .onClick(async () => {
                  if (this.selectedFile) {
                    const success = await FileManager.deleteFile(this.selectedFile.uri);
                    if (success) {
                      Logger.success('删除文件成功');
                      this.selectedFile = null;
                      this.files = this.files.filter(f => f.uri !== this.selectedFile?.uri);
                    }
                  }
                })
            } else {
              Text('未选择文件')
                .fontSize(14)
                .fontColor('#999999')
            }

            Divider()

            Text('文件内容:')
              .fontSize(14)
              .fontColor('#6666')
              .margin({ left: 16 })

            Text(this.fileContent)
              .width('100%')
              .maxLines(10)
              .fontColor('#6666')
              .padding(12)
              .borderRadius(8)
              .backgroundColor('#F5F5F5')
          }
          .width('100%')
          .padding(16)
        }
      }
      .tabBar(this.TabBar(2, 'ℹ️', '信息'))
    }
    .barWidth(100)
    .animationDuration(300)
  }

  // Methods
  listFiles(): void {
    FileManager.listFiles(this.currentPath).then((files) => {
      this.files = files;
      Logger.success(`列出文件成功，共 ${files.length} 个文件`);
    }).catch((error: Error) => {
      Logger.error('列出文件失败', error);
    });
  }

  createDirectory(): void {
    FileManager.createDirectory(this.currentPath + '/new_folder').then(() => {
      Logger.success('创建目录成功');
      this.listFiles();
    }).catch((error: Error) => {
      Logger.error('创建目录失败', error);
    });
  }

  selectSourceFile(): void {
    Logger.info('选择源文件功能待实现');
  }

  selectTargetFile(): void {
    Logger.info('选择目标文件功能待实现');
  }

  showFileOptions(): void {
    Logger.info('文件操作功能待实现');
  }

  searchFiles(): void {
    Logger.info('搜索文件功能待实现');
  }

  onBackPressed(): void {
    Logger.info('返回功能待实现');
  }
}
