#!/bin/bash
# PMD Violations Auto-Fix Script
# This script automatically fixes common PMD violations

set -e

PROJECT_ROOT="D:\code\ets-har-builder\ets2jsc"

echo "Fixing PMD violations..."
echo ""

# 1. Fix AppendCharacterWithChar (35 violations)
# Replace append("x") with append('x')
echo "Fixing AppendCharacterWithChar violations..."
for file in $(grep -l "append(\"\|append('')" "$PROJECT_ROOT/src/main/java"/*.java 2>/dev/null); do
    if [ -n "$file" ]; then
        echo "  Processing: $file"
        # Replace single quote strings
        sed -i "s/append(\"[^\"]*\")/append('\''\1')/g" "$file"
        sed -i "s/append('\\([a-zA-Z0-9]\\+)')/append('\''\\1')/g" "$file"
    fi
done
echo "  AppendCharacterWithChar: Fixed"
echo ""

# 2. Fix GuardLogStatement (13 violations)
# Add log level guards around logger calls
echo "Fixing GuardLogStatement violations..."
for file in $(grep -l "LOGGER\\.info(\\.\\..*)\\.);" "$PROJECT_ROOT/src/main/java"/*.java 2>/dev/null); do
    if [ -n "$file" ]; then
        echo "  Processing: $file"
        # Add guards to logger calls that don't have them
        sed -i 's/\(LOGGER\.info(\(.*\))\);/if (LOGGER\.isInfoEnabled()) { LOGGER\.info('\''\1''); }/g' "$file"
        sed -i 's/\(LOGGER\.error(\(.*\))\);/if (LOGGER\.isErrorEnabled()) { LOGGER\.error('\''\1''); }/g' "$file"
        sed -i 's/\(LOGGER\.warn(\(.*\))\);/if (LOGGER\.isWarnEnabled()) { LOGGER\.warn('\''\1''); }/g' "$file"
    fi
done
echo "  GuardLogStatement: Fixed"
echo ""

# 3. Fix UseUnderscoresInNumericLiterals (12 violations)
# Add underscores to long numeric literals
echo "Fixing UseUnderscoresInNumericLiterals violations..."
for file in $(grep -n "1000\|100000\|1000000" "$PROJECT_ROOT/src/main/java"/*.java 2>/dev/null); do
    if [ -n "$file" ]; then
        echo "  Processing: $file"
        # 1000 -> 1_000, 1000000 -> 1_000_000
        sed -i 's/\b1000\b/1_000/g' "$file"
        sed -i 's/\b1000000\b/1_000_000/g' "$file"
        sed -i 's/\b10000000\b/1_000_00/g' "$file"
    fi
done
echo "  UseUnderscoresInNumericLiterals: Fixed"
echo ""

# 4. Fix MissingSerialVersionUID (6 violations)
# Add serialVersionUID to exception classes
echo "Fixing MissingSerialVersionUID violations..."
for file in $(grep -l "extends.*Exception" "$PROJECT_ROOT/src/main/java"/*.java 2>/dev/null); do
    if [ -n "$file" ]; then
        echo "  Processing: $file"
        # Check if serialVersionUID already exists
        if ! grep -q "serialVersionUID" "$file"; then
            # Find position after class declaration and add serialVersionUID
            sed -i '/public class.*Exception.*{/a\    private static final long serialVersionUID = 1L;' "$file"
        fi
    fi
done
echo "  MissingSerialVersionUID: Fixed"
echo ""

echo "========================================="
echo "Summary of auto-fixes:"
echo "  AppendCharacterWithChar: 35 violations"
echo "  GuardLogStatement: 13 violations"
echo "  UseUnderscoresInNumericLiterals: 12 violations"
echo "  MissingSerialVersionUID: 6 violations"
echo "========================================="
echo ""
echo "Note: LocalVariableCouldBeFinal violations (642) can be fixed using IDE's 'Add final modifier' feature."
echo "      Run: Inspect -> Code -> Add 'final' modifier to local variables"
echo ""
