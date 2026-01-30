class ParentComponent extends View {
  constructor() {
    super();
    this.parentCount__ = this.createState('parentCount', () => this.parentCount);
  }

  initialRender() {
        Column.create();
      ChildComponent({ count: this.parentCount });
    Column.pop();

  }

  private parentCount__: ObservedPropertySimple<number> = 10;

  get parentCount() {
    return this.parentCount__.get();
  }

  set parentCount(newValue) {
    this.parentCount__.set(newValue);
  }

}

class ChildComponent extends View {
  initialRender() {
    Text('Count: ' + this.count);
  }

  private count__: ObservedPropertySimpleOneWay<number>;

  get count() {
    return this.count__.get();
  }

  set count(newValue) {
    this.count__.set(newValue);
  }

}


//# sourceMappingURL=output-prop.js.map