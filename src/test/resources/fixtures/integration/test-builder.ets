@Component
struct BuilderTest {
  @Builder customText(title: string, content: string) {
    Text(title)
    Text(content)
  }

  build() {
    Column() {
      this.customText('Hello', 'World')
      this.customText('Count:', '10')
    }
  }
}
