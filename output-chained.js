class StyledText extends View {
  constructor() {
    super();
    this.text__ = this.createState('text', () => this.text);
  }

  initialRender() {
    Text(this.text).fontSize(16).fontColor('#FF0000').onClick(() => {
        console.log('clicked')
      });
  }

  private text__: ObservedPropertySimple<string> = 'Hello';

  get text() {
    return this.text__.get();
  }

  set text(newValue) {
    this.text__.set(newValue);
  }

}


//# sourceMappingURL=output-chained.js.map