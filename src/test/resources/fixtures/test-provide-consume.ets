@Component
struct ProviderComponent {
  @Provide sharedCount: number = 100

  build() {
    Column() {
      ConsumerComponent()
    }
  }
}

@Component
struct ConsumerComponent {
  @Consume sharedCount: number = 0

  build() {
    Text('Shared: ' + this.sharedCount)
  }
}
