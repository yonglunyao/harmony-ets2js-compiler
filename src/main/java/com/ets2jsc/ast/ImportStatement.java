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
     * CC: 3 (early return + null check + else)
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("import ");

        if (specifiers.isEmpty()) {
            // Side effect import: import 'module'
            sb.append('\'').append(module).append('\'');
        } else {
            // Regular import with specifiers
            sb.append(buildImportClause());
            sb.append(" from '").append(module).append('\'');
        }

        sb.append(';');
        return sb.toString();
    }

    /**
     * Builds the import clause part (excluding 'from' and module).
     * CC: 2 (null check + loop)
     */
    private String buildImportClause() {
        final SpecifierClassifier classifier = classifySpecifiers();
        final List<String> parts = new ArrayList<>();

        // Add default imports
        for (final ImportSpecifier spec : classifier.defaultSpecs) {
            parts.add(spec.getLocalName());
        }

        // Add named imports
        if (!classifier.namedSpecs.isEmpty()) {
            parts.add(buildNamedImports(classifier.namedSpecs));
        }

        // Add namespace imports
        for (final ImportSpecifier spec : classifier.namespaceSpecs) {
            parts.add("* as " + spec.getLocalName());
        }

        return String.join(", ", parts);
    }

    /**
     * Builds named imports string like "{ A, B as C }".
     * CC: 2 (loop + ternary)
     */
    private String buildNamedImports(List<ImportSpecifier> namedSpecs) {
        final StringBuilder namedPart = new StringBuilder("{ ");
        for (int i = 0; i < namedSpecs.size(); i++) {
            final ImportSpecifier spec = namedSpecs.get(i);
            if (i > 0) {
                namedPart.append(", ");
            }
            if (!spec.getImportedName().equals(spec.getLocalName())) {
                namedPart.append(spec.getImportedName()).append(" as ");
            }
            namedPart.append(spec.getLocalName());
        }
        namedPart.append(" }");
        return namedPart.toString();
    }

    /**
     * Classifies specifiers by their kind.
     * CC: 1 (simple loop with switch-like if-else)
     */
    private SpecifierClassifier classifySpecifiers() {
        SpecifierClassifier classifier = new SpecifierClassifier();
        for (ImportSpecifier spec : specifiers) {
            if (spec.isDefault()) {
                classifier.defaultSpecs.add(spec);
            } else if (spec.isNamespace()) {
                classifier.namespaceSpecs.add(spec);
            } else {
                classifier.namedSpecs.add(spec);
            }
        }
        return classifier;
    }

    /**
     * Helper class to hold classified specifiers.
     */
    private static class SpecifierClassifier {
        final List<ImportSpecifier> defaultSpecs = new ArrayList<>();
        final List<ImportSpecifier> namedSpecs = new ArrayList<>();
        final List<ImportSpecifier> namespaceSpecs = new ArrayList<>();
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
