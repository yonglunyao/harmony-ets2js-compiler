@Component
struct ComprehensiveTest {
  // Test all @State types
  @State num: number = 42
  @State str: string = 'hello'
  @State bool: boolean = true
  @State arr: number[] = [1, 2, 3]
  @State obj: Object = { x: 1, y: 2 }

  build() {
    Column() {
      // Binary expressions
      Text('Sum: ' + (this.num + 10))

      // Conditional expressions
      Text(this.bool ? 'Yes' : 'No')

      // Array access
      Text('First: ' + this.arr[0])

      // Object property access
      Text('X: ' + this.obj.x)

      // Chained method calls with complex expressions
      Text('Hello')
        .fontSize(this.num > 50 ? 20 : 16)

      // ForEach
      ForEach(this.arr, (item) => {
        Text('Item: ' + item)
      }, (item) => item)
    }
  }
}
