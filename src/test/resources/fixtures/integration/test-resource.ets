@Component
struct ResourceTest {
  @State resourceColor: string = '$r("app.string.title")'

  build() {
    Column() {
      Text('Title: ' + this.resourceColor)
      Divider()
      Image($r('sys.color.ohos_id_color_primary'))
    }
  }
}
