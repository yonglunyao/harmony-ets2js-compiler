@Component
struct Counter {
  @State count: number = 0
  @State message: string = 'Hello'

  build() {
    Column() {
      Text(this.message)
      Text(this.count.toString())
    }
  }
}
