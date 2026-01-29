@Component
struct ListComponent {
  @State items: Array<string> = ['A', 'B', 'C']

  build() {
    Column() {
      ForEach(this.items, (item: string, index: number) => {
        Text(item)
      }, (item: string) => item)
    }
  }
}
