class ProviderComponent extends View {
  initialRender() {
        Column.create();
      ConsumerComponent();
    Column.pop();

  }

  private sharedCount__: ObservedPropertySimple<number> = 100;

  get sharedCount() {
    return this.sharedCount__.get();
  }

  set sharedCount(newValue) {
    this.sharedCount__.set(newValue);
  }

}

class ConsumerComponent extends View {
  initialRender() {
    Text('Shared: ' + this.sharedCount);
  }

  private sharedCount__: ObservedPropertySimple<number> = 0;

  get sharedCount() {
    return this.sharedCount__.get();
  }

  set sharedCount(newValue) {
    this.sharedCount__.set(newValue);
  }

}


//# sourceMappingURL=output-provide-consume.js.map