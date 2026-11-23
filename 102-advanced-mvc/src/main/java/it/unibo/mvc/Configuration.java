package it.unibo.mvc;

import java.io.IOException;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Encapsulates the concept of configuration.
 */
public final class Configuration {

    private int max;
    private int min;
    private int attempts;

    private Configuration(final String stringPath) {
        final Path path = Path.of(stringPath);
        try (Stream<String> stream = Files.lines(path)) {
            stream
                    .filter(line -> !line.isBlank() && line.contains(":"))
                    .map(line -> line.split(": "))
                    .forEach(splittedLine -> {
                        switch (splittedLine[0]) {
                            case "minimum":
                                min = Integer.parseInt(splittedLine[1]);
                                break;
                            case "maximum":
                                max = Integer.parseInt(splittedLine[1]);
                                break;
                            case "attempts":
                                attempts = Integer.parseInt(splittedLine[1]);
                                break;
                            default:
                                break;
                        }
                    });
        } catch (final IOException e) {
            System.getLogger("App").log(Level.ERROR,
                    "An error occurred while fetching configuration: " + e.getMessage());
        }
    }

    /**
     * The maximum value.
     *
     * @return the maximum value
     */
    public int getMax() {
        return max;
    }

    /**
     * The minium value.
     *
     * @return the minimum value
     */
    public int getMin() {
        return min;
    }

    /**
     * The number of attempts.
     *
     * @return the number of attempts
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Checks whether the configuration is consistent.
     *
     * @return true if the configuration is consistent
     */
    public boolean isConsistent() {
        return attempts > 0 && min < max;
    }

    /**
     * Pattern builder: used here because:
     * <br>
     * - all the parameters of the Configuration class have a default value, which
     * means that we would like to have all the possible combinations of
     * constructors (one with three parameters, three with two parameters, three
     * with a single parameter), which are way too many and confusing to use
     * <br>
     * - moreover, it would be impossible to provide all of them, because they are
     * all the same type, and only a single constructor can exist with a given
     * list of parameter types.
     * <br>
     * - the Configuration class has three parameters of the same type, and it is
     * unclear to understand, in a call to its constructor, which is which. By using
     * the builder, we emulate the so-called "named arguments".
     */
    public static final class Builder {

        private static final int DEFAULT_MIN = 0;
        private static final int DEFAULT_MAX = 100;
        private static final int DEFAULT_ATTEMPTS = 10;

        private int currentMin = DEFAULT_MIN;
        private int currentMax = DEFAULT_MAX;
        private int currentAttempts = DEFAULT_ATTEMPTS;
        private boolean consumed;

        /**
         * Default constructor. Produces a builder with the default configuration.
         */
        public Builder() {
            // Default constructor
        }

        /**
         * Sets the minimum value.
         *
         * @param min the minimum value
         * @return this builder, for method chaining
         */
        public Builder withMin(final int min) {
            this.currentMin = min;
            return this;
        }

        /**
         * Sets the maximum value.
         *
         * @param max the maximum value
         * @return this builder, for method chaining
         */
        public Builder withMax(final int max) {
            this.currentMax = max;
            return this;
        }

        /**
         * Sets the attempts count.
         *
         * @param attempts the attempts count
         * @return this builder, for method chaining
         */
        public Builder withAttempts(final int attempts) {
            this.currentAttempts = attempts;
            return this;
        }

        /**
         * Getter for {@link currentAttempts}.
         * 
         * @return returns the value above
         */
        public int getCurrentAttempts() {
            return currentAttempts;
        }

        /**
         * Getter for {@link currentMax}.
         * 
         * @return returns the value above
         */
        public int getCurrentMax() {
            return currentMax;
        }

        /**
         * Getter for {@link currentMin}.
         * 
         * @return returns the value above
         */
        public int getCurrentMin() {
            return currentMin;
        }

        /**
         * Builds the configuration.
         *
         * @return a configuration
         */
        public Configuration build() {
            if (consumed) {
                throw new IllegalStateException("The builder can only be used once");
            }
            consumed = true;
            return new Configuration("src/main/resources/config.yml");
        }
    }

}
