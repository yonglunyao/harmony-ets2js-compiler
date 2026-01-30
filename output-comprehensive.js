class ComprehensiveTest extends View {
  constructor() {
    super();
    this.num__ = this.createState('num', () => this.num);
    this.str__ = this.createState('str', () => this.str);
    this.bool__ = this.createState('bool', () => this.bool);
    this.arr__ = this.createState('arr', () => this.arr);
    this.obj__ = this.createState('obj', () => this.obj);
  }

  initialRender() {
        Column.create();
      Text('Sum: ' + (this.num + 10));
      Text(this.bool ? 'Yes' : 'No');
      Text('First: ' + this.arr[0]);
      Text('X: ' + this.obj.x);
      Text('Hello').fontSize(this.num > 50 ? 20 : 16);
      ForEach.create();
      const __itemGenFunction__ = (item) => {
        Text('Item: ' + item)
      };
      const __keyGenFunction__ = (item) => item;
      ForEach.keyGenerator(__keyGenFunction__);
      ForEach.itemGenerator(__itemGenFunction__);
      ForEach.pop();
    Column.pop();

  }

  private num__: ObservedPropertySimple<number> = 42;

  get num() {
    return this.num__.get();
  }

  set num(newValue) {
    this.num__.set(newValue);
  }

  private str__: ObservedPropertySimple<string> = 'hello';

  get str() {
    return this.str__.get();
  }

  set str(newValue) {
    this.str__.set(newValue);
  }

  private bool__: ObservedPropertySimple<boolean> = true;

  get bool() {
    return this.bool__.get();
  }

  set bool(newValue) {
    this.bool__.set(newValue);
  }

  private arr__: ObservedPropertySimple<number[]> = [1, 2, 3];

  get arr() {
    return this.arr__.get();
  }

  set arr(newValue) {
    this.arr__.set(newValue);
  }

  private obj__: ObservedPropertySimple<Object> = { x: 1, y: 2 };

  get obj() {
    return this.obj__.get();
  }

  set obj(newValue) {
    this.obj__.set(newValue);
  }

}


//# sourceMappingURL=output-comprehensive.js.map