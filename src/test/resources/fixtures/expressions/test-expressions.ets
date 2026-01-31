@Component
struct TestExpressions {
  @State count: number = 0
  @State name: string = 'test'
  @State isActive: boolean = true
  @State data: Object = { a: 1, b: 2 }
  @State items: string[] = ['a', 'b', 'c']

  build() {
    Column() {
      // BinaryExpression
      Text('Count: ' + (this.count + 10))
      // ConditionalExpression
      Text(this.isActive ? 'Active' : 'Inactive')
      // ArrayLiteral
      Text('First: ' + this.items[0])
      // Nested expressions
      Text(this.count > 5 ? 'Many' : 'Few')
    }
  }
}
