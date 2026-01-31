@Component
export struct TestUI {
  @State message: string = 'Hello World';

  build() {
    Column() {
      Text(this.message)
        .fontSize(16)
        .fontColor(Color.Red)
      Button('Click')
    }
    .width('100%')
  }
}
