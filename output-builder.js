class BuilderTest extends View {
  customText(title /* string */, content /* string */) {
        Text.create(title);
    Text.pop();

        Text.create(content);
    Text.pop();

  }

  initialRender() {
        Column.create();
      this.customText('Hello', 'World');
      this.customText('Count:', '10');
    Column.pop();

  }

}


//# sourceMappingURL=output-builder.js.map