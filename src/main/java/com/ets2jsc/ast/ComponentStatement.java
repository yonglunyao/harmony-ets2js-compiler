package com.ets2jsc.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a component statement after create/pop transformation.
 * Example: Text.create('Hello'); Text.fontSize(16); Text.pop();
 */
public class ComponentStatement implements AstNode {
    private final String componentName;
    private final List<ComponentPart> parts;

    public ComponentStatement(String componentName) {
        this.componentName = componentName;
        this.parts = new ArrayList<>();
    }

    public String getComponentName() {
        return componentName;
    }

    public void addPart(ComponentPart part) {
        parts.add(part);
    }

    public List<ComponentPart> getParts() {
        return parts;
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
    public static class ComponentPart {
        private final PartKind kind;
        private final String code;

        public ComponentPart(PartKind kind, String code) {
            this.kind = kind;
            this.code = code;
        }

        public PartKind getKind() {
            return kind;
        }

        public String getCode() {
            return code;
        }
    }

    public enum PartKind {
        CREATE,   // Text.create(...)
        METHOD,   // Text.fontSize(...)
        POP       // Text.pop()
    }
}
