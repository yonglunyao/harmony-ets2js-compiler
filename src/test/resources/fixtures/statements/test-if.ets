@Component
struct IfTest {
  @State isActive: boolean = true

  build() {
    Column() {
      if (this.isActive) {
        Text('Active')
      } else {
        Text('Inactive')
      }
    }
  }
}
