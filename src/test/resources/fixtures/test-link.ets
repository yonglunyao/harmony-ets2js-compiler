@Component
struct ParentLink {
  @State parentCount: number = 10

  build() {
    Column() {
      ChildLink({ count: $parentCount })
    }
  }
}

@Component
struct ChildLink {
  @Link count: number = 0

  build() {
    Text('Count: ' + this.count)
  }
}
