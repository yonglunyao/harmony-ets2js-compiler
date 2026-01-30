class IfTest extends View {
  constructor() {
    super();
    this.isActive__ = this.createState('isActive', () => this.isActive);
  }

  initialRender() {
        Column.create();
      If.create();
      if (this.isActive) {
        If.branchId(0);
                Text.create('Active');
        Text.pop();

      }
      else {
        If.branchId(1);
                Text.create('Inactive');
        Text.pop();

      }
      If.pop();
    Column.pop();

  }

  private isActive__: ObservedPropertySimple<boolean> = true;

  get isActive() {
    return this.isActive__.get();
  }

  set isActive(newValue) {
    this.isActive__.set(newValue);
  }

}


//# sourceMappingURL=output-if.js.map