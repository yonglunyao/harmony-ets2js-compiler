package com.ets2jsc.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Import statement AST node.
 * Represents an import declaration like:
 * - import { A, B } from 'module'
 * - import Module from 'module'
 * - import * as Module from 'module'
 */
public class ImportStatement implements AstNode {
    private final String module;
    private final List<ImportSpecifier> specifiers;

    public ImportStatement(String module) {
        this.module = module;
        this.specifiers = new ArrayList<>();
    }

    public void addSpecifier(ImportSpecifier specifier) {
        specifiers.add(specifier);
    }

    public String getModule() {
        return module;
    }

    public List<ImportSpecifier> getSpecifiers() {
        return specifiers;
    }

    @Override
    public String getType() {
        return "ImportStatement";
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Generate the import statement as a string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("import ");

        if (specifiers.isEmpty()) {
            // Side effect import: import 'module'
            sb.append("'").append(module).append("'");
        } else {
            // Classify specifiers
            List<ImportSpecifier> defaultSpecs = new ArrayList<>();
            List<ImportSpecifier> namedSpecs = new ArrayList<>();
            List<ImportSpecifier> namespaceSpecs = new ArrayList<>();

            for (ImportSpecifier spec : specifiers) {
                if (spec.isDefault()) {
                    defaultSpecs.add(spec);
                } else if (spec.isNamespace()) {
                    namespaceSpecs.add(spec);
                } else {
                    namedSpecs.add(spec);
                }
            }

            // Build import string
            List<String> parts = new ArrayList<>();

            for (ImportSpecifier spec : defaultSpecs) {
                parts.add(spec.getLocalName());
            }

            if (!namedSpecs.isEmpty()) {
                StringBuilder namedPart = new StringBuilder("{ ");
                for (int i = 0; i < namedSpecs.size(); i++) {
                    ImportSpecifier spec = namedSpecs.get(i);
                    if (i > 0) namedPart.append(", ");
                    if (!spec.getImportedName().equals(spec.getLocalName())) {
                        namedPart.append(spec.getImportedName())
                               .append(" as ");
                    }
                    namedPart.append(spec.getLocalName());
                }
                namedPart.append(" }");
                parts.add(namedPart.toString());
            }

            for (ImportSpecifier spec : namespaceSpecs) {
                parts.add("* as " + spec.getLocalName());
            }

            sb.append(String.join(", ", parts));
            sb.append(" from '").append(module).append("'");
        }

        sb.append(";");
        return sb.toString();
    }

    /**
     * Import specifier - represents a single imported item.
     */
    public static class ImportSpecifier {
        private final String importedName;  // The name in the module
        private final String localName;     // The local name (after 'as' if present)
        private final SpecifierKind kind;

        public enum SpecifierKind {
            NAMED, DEFAULT, NAMESPACE
        }

        public ImportSpecifier(String importedName, String localName, SpecifierKind kind) {
            this.importedName = importedName;
            this.localName = localName;
            this.kind = kind;
        }

        public String getImportedName() {
            return importedName;
        }

        public String getLocalName() {
            return localName;
        }

        public boolean isDefault() {
            return kind == SpecifierKind.DEFAULT;
        }

        public boolean isNamespace() {
            return kind == SpecifierKind.NAMESPACE;
        }

        public boolean isNamed() {
            return kind == SpecifierKind.NAMED;
        }
    }
}
