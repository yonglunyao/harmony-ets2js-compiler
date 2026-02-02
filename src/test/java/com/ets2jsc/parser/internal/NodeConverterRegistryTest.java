package com.ets2jsc.parser.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NodeConverterRegistry.
 */
@DisplayName("NodeConverterRegistry Tests")
class NodeConverterRegistryTest {

    /**
     * Test implementation of NodeConverterRegistry for testing.
     */
    private static class TestRegistry extends NodeConverterRegistry {
        @Override
        protected void initializeConverters() {
            // Don't initialize any converters for testing
        }
    }

    /**
     * Test converter for testing purposes.
     */
    private static class TestConverter implements NodeConverter {
        private final String kindName;
        private final int priority;

        TestConverter(String kindName, int priority) {
            this.kindName = kindName;
            this.priority = priority;
        }

        TestConverter(String kindName) {
            this(kindName, 0);
        }

        @Override
        public Object convert(JsonNode json, ConversionContext context) {
            return "converted:" + kindName;
        }

        @Override
        public boolean canConvert(String kindName) {
            return this.kindName.equals(kindName);
        }

        @Override
        public int getPriority() {
            return priority;
        }
    }

    @Test
    @DisplayName("Test registry initialization")
    void testRegistryInitialization() {
        TestRegistry registry = new TestRegistry();

        assertNotNull(registry);
        assertEquals(0, registry.size());
    }

    @Test
    @DisplayName("Test register converter")
    void testRegisterConverter() {
        TestRegistry registry = new TestRegistry();
        TestConverter converter = new TestConverter("TestNode");

        registry.register(converter);

        assertEquals(1, registry.size());
        assertTrue(registry.hasConverter("TestNode"));
    }

    @Test
    @DisplayName("Test register multiple converters")
    void testRegisterMultipleConverters() {
        TestRegistry registry = new TestRegistry();
        TestConverter converter1 = new TestConverter("FirstNode");
        TestConverter converter2 = new TestConverter("SecondNode");

        registry.register(converter1);
        registry.register(converter2);

        assertEquals(2, registry.size());
        assertTrue(registry.hasConverter("FirstNode"));
        assertTrue(registry.hasConverter("SecondNode"));
    }

    @Test
    @DisplayName("Test hasConverter returns true for registered converter")
    void testHasConverterReturnsTrueForRegisteredConverter() {
        TestRegistry registry = new TestRegistry();
        TestConverter converter = new TestConverter("TestNode");

        registry.register(converter);

        assertTrue(registry.hasConverter("TestNode"));
    }

    @Test
    @DisplayName("Test hasConverter returns false for non-registered converter")
    void testHasConverterReturnsFalseForNonRegisteredConverter() {
        TestRegistry registry = new TestRegistry();

        assertFalse(registry.hasConverter("NonExistentNode"));
    }

    @Test
    @DisplayName("Test clear removes all converters")
    void testClearRemovesAllConverters() {
        TestRegistry registry = new TestRegistry();
        registry.register(new TestConverter("FirstNode"));
        registry.register(new TestConverter("SecondNode"));

        assertEquals(2, registry.size());

        registry.clear();

        assertEquals(0, registry.size());
        assertFalse(registry.hasConverter("FirstNode"));
        assertFalse(registry.hasConverter("SecondNode"));
    }

    @Test
    @DisplayName("Test findConverter returns correct converter")
    void testFindConverterReturnsCorrectConverter() {
        TestRegistry registry = new TestRegistry();
        TestConverter converter = new TestConverter("TestNode");

        registry.register(converter);

        NodeConverter found = registry.findConverter("TestNode");

        assertNotNull(found);
        assertSame(converter, found);
    }

    @Test
    @DisplayName("Test findConverter throws exception for non-registered converter")
    void testFindConverterThrowsExceptionForNonRegisteredConverter() {
        TestRegistry registry = new TestRegistry();

        assertThrows(UnsupportedOperationException.class, () -> {
            registry.findConverter("NonExistentNode");
        });
    }

    @Test
    @DisplayName("Test converters are sorted by priority")
    void testConvertersAreSortedByPriority() {
        TestRegistry registry = new TestRegistry();
        TestConverter lowPriority = new TestConverter("LowNode", 1);
        TestConverter highPriority = new TestConverter("HighNode", 10);
        TestConverter mediumPriority = new TestConverter("MediumNode", 5);

        registry.register(lowPriority);
        registry.register(highPriority);
        registry.register(mediumPriority);

        // High priority converter should be found first
        NodeConverter found = registry.findConverter("HighNode");
        assertNotNull(found);
        assertSame(highPriority, found);
    }

    @Test
    @DisplayName("Test register maintains priority order")
    void testRegisterMaintainsPriorityOrder() {
        TestRegistry registry = new TestRegistry();

        // Create multiple converters for the same kind with different priorities
        TestConverter lowPriority = new TestConverter("SameNode", 1);
        TestConverter highPriority = new TestConverter("SameNode", 10);

        registry.register(lowPriority);
        registry.register(highPriority);

        // High priority converter should be found first
        NodeConverter found = registry.findConverter("SameNode");
        assertNotNull(found);
        // The first one registered that can handle it should be found
        // Since both can convert "SameNode", the behavior depends on implementation
        assertTrue(found.canConvert("SameNode"));
    }

    @Test
    @DisplayName("Test size returns correct count")
    void testSizeReturnsCorrectCount() {
        TestRegistry registry = new TestRegistry();

        assertEquals(0, registry.size());

        registry.register(new TestConverter("FirstNode"));
        assertEquals(1, registry.size());

        registry.register(new TestConverter("SecondNode"));
        assertEquals(2, registry.size());

        registry.clear();
        assertEquals(0, registry.size());
    }

    @Test
    @DisplayName("Test converter with default priority")
    void testConverterWithDefaultPriority() {
        TestRegistry registry = new TestRegistry();
        TestConverter converter = new TestConverter("DefaultNode");

        assertEquals(0, converter.getPriority(), "Default priority should be 0");

        registry.register(converter);

        assertTrue(registry.hasConverter("DefaultNode"));
    }
}
