package com.ets2jsc.domain.model.ast;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a component statement after create/pop transformation.
 * Example: Text.create('Hello'); Text.fontSize(16); Text.pop();
 */
@Getter
@Setter
public class ComponentStatement implements AstNode {
    private final String componentName;
    private final List<ComponentPart> parts;
    private Block children;  // Children block for components like Column() { ... }

    public ComponentStatement(String componentName) {
        this.componentName = componentName;
        this.parts = new ArrayList<>();
    }

    public void addPart(ComponentPart part) {
        parts.add(part);
    }

    public boolean hasChildren() {
        return children != null;
    }

    @Override
    public String getType() {
        return "ComponentStatement";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
         * Represents a part of the component statement.
         */
        public record ComponentPart(PartKind kind, String code) {
    }

    public enum PartKind {
        CREATE,   // Text.create(...)
        METHOD,   // Text.fontSize(...)
        POP       // Text.pop()
    }
}
