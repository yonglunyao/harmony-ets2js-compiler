class ParentLink extends View {
  constructor() {
    super();
    this.parentCount__ = this.createState('parentCount', () => this.parentCount);
  }

  initialRender() {
        Column.create();
      ChildLink({ count: $parentCount });
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

class ChildLink extends View {
  initialRender() {
    Text('Count: ' + this.count);
  }

  private count__: ObservedPropertySimpleTwoWay<number>;

  get count() {
    return this.count__.get();
  }

  set count(newValue) {
    this.count__.set(newValue);
  }

}


//# sourceMappingURL=output-link.js.map