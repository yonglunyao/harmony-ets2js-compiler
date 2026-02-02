package com.ets2jsc.shared.events;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

/**
 * Event object containing information about a compilation event.
 * <p>
 * This immutable class carries context about compilation lifecycle events
 * to registered listeners.
 * </p>
 *
 * @since 1.0
 */
public final class CompilationEvent {

    private final CompilationEventType type;
    private final Path sourcePath;
    private final Path outputPath;
    private final String message;
    private final Throwable error;
    private final Instant timestamp;
    private final int filesProcessed;
    private final int totalFiles;

    private CompilationEvent(Builder builder) {
        this.type = Objects.requireNonNull(builder.type, "Event type cannot be null");
        this.sourcePath = builder.sourcePath;
        this.outputPath = builder.outputPath;
        this.message = builder.message;
        this.error = builder.error;
        this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();
        this.filesProcessed = builder.filesProcessed;
        this.totalFiles = builder.totalFiles;
    }

    /**
     * Returns a new builder for creating compilation events.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a new builder with the type preset.
     *
     * @param type the event type
     * @return a new builder
     */
    public static Builder builder(CompilationEventType type) {
        return new Builder().type(type);
    }

    /**
     * Returns the event type.
     *
     * @return the event type
     */
    public CompilationEventType getType() {
        return type;
    }

    /**
     * Returns the source path, if applicable.
     *
     * @return the source path, or null
     */
    public Path getSourcePath() {
        return sourcePath;
    }

    /**
     * Returns the output path, if applicable.
     *
     * @return the output path, or null
     */
    public Path getOutputPath() {
        return outputPath;
    }

    /**
     * Returns the event message.
     *
     * @return the message, or null
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the error, if applicable.
     *
     * @return the error, or null
     */
    public Throwable getError() {
        return error;
    }

    /**
     * Returns the timestamp when the event occurred.
     *
     * @return the event timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the number of files processed so far.
     *
     * @return the files processed count
     */
    public int getFilesProcessed() {
        return filesProcessed;
    }

    /**
     * Returns the total number of files to process.
     *
     * @return the total files count
     */
    public int getTotalFiles() {
        return totalFiles;
    }

    /**
     * Builder for creating CompilationEvent instances.
     */
    public static final class Builder {
        private CompilationEventType type;
        private Path sourcePath;
        private Path outputPath;
        private String message;
        private Throwable error;
        private Instant timestamp;
        private int filesProcessed;
        private int totalFiles;

        private Builder() {
            // Private constructor
        }

        /**
         * Sets the event type.
         *
         * @param type the event type
         * @return this builder
         */
        public Builder type(CompilationEventType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the source path.
         *
         * @param path the source path
         * @return this builder
         */
        public Builder sourcePath(Path path) {
            this.sourcePath = path;
            return this;
        }

        /**
         * Sets the output path.
         *
         * @param path the output path
         * @return this builder
         */
        public Builder outputPath(Path path) {
            this.outputPath = path;
            return this;
        }

        /**
         * Sets the event message.
         *
         * @param message the message
         * @return this builder
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Sets the error.
         *
         * @param error the error
         * @return this builder
         */
        public Builder error(Throwable error) {
            this.error = error;
            return this;
        }

        /**
         * Sets the timestamp.
         *
         * @param timestamp the timestamp
         * @return this builder
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Sets the number of files processed.
         *
         * @param count the files processed count
         * @return this builder
         */
        public Builder filesProcessed(int count) {
            this.filesProcessed = count;
            return this;
        }

        /**
         * Sets the total number of files.
         *
         * @param count the total files count
         * @return this builder
         */
        public Builder totalFiles(int count) {
            this.totalFiles = count;
            return this;
        }

        /**
         * Builds the compilation event.
         *
         * @return a new CompilationEvent instance
         */
        public CompilationEvent build() {
            return new CompilationEvent(this);
        }
    }
}
