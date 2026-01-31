export class MainPage extends View {
  constructor() {
    super();
    this.message__ = this.createState('message', () => this.message);
  }

  initialRender() {
        Row.create();
            Column.create();
        Text(this.message).fontSize($r('app.float.page_text_font_size')).fontWeight(FontWeight.Bold).onClick(() => {
    this.message = "Welcome";
  });
      Column.pop();

      width("100%");
    Row.pop();

    height("100%");
  }

  message__ = "Hello World";

  get message() {
    return this.message__.get();
  }

  set message(newValue) {
    this.message__.set(newValue);
  }

}


//# sourceMappingURL=MainPage.js.map