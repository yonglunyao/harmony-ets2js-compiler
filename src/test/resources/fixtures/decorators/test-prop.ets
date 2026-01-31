@Component
struct ParentComponent {
  @State parentCount: number = 10

  build() {
    Column() {
      ChildComponent({ count: this.parentCount })
    }
  }
}

@Component
struct ChildComponent {
  @Prop count: number = 0

  build() {
    Text('Count: ' + this.count)
  }
}
