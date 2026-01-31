@Component
struct StyledText {
  @State text: string = 'Hello'

  build() {
    Text(this.text)
      .fontSize(16)
      .fontColor('#FF0000')
      .onClick(() => {
        console.log('clicked')
      })
  }
}
