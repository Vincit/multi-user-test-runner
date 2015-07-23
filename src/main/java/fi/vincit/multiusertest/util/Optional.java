package fi.vincit.multiusertest.util;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A custom optional class that works in Java 7 so
 * that Java 8 isn't needed
 */
public class Optional<T> {

    private static final Optional EMPTY = of(null);

    private T value;

    Optional(T value) {
        this.value = value;
    }

    public static <T> Optional ofNullable(T value) {
        return new Optional<>(value);
    }

    public static <T> Optional of(T value) {
        Objects.requireNonNull(value);
        return new Optional<>(value);
    }

    public static <T> Optional<T> empty() {
        return EMPTY;
    }

    public T get() {
        if (isPresent()) {
            return value;
        } else {
            throw new NoSuchElementException("Value must no be null");
        }
    }

    public T orElse(T otherValue) {
        if (isPresent()) {
            return value;
        } else {
            return otherValue;
        }
    }

    public boolean isPresent() {
        return value != null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Optional<?> optional = (Optional<?>) other;
        return Objects.equals(value, optional.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
