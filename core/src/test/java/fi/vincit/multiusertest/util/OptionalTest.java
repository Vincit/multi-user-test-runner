package fi.vincit.multiusertest.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.NoSuchElementException;

import org.junit.Test;

public class OptionalTest {

    @Test
    public void of() {
        Optional<Integer> optional = Optional.of(100);
        assertThat(optional.isPresent(), is(true));
        assertThat(optional.get(), is(100));
    }

    @Test(expected = NullPointerException.class)
    public void of_Null_CreationThrows() {
        Optional.of(null);
    }

    @Test
    public void ofNullable() {
        Optional<Integer> optional = Optional.ofNullable(1000);
        assertThat(optional.isPresent(), is(true));
        assertThat(optional.get(), is(1000));
    }

    @Test
    public void ofNullable_Null_IsNotPresent() {
        Optional<Integer> optional = Optional.ofNullable(null);
        assertThat(optional.isPresent(), is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void ofNullable_Null_GetValueThrows() {
        Optional<Integer> optional = Optional.ofNullable(null);
        optional.get();
    }

    @Test
    public void ofNullable_Null_EqualsEmpty() {
        Optional<Integer> optional = Optional.ofNullable(null);
        assertThat(optional.equals(Optional.<Integer>empty()), is(true));
    }

    @Test
    public void nonEmptyOptional_NotEqualToEmpty() {
        Optional<Integer> optional = Optional.ofNullable(1045);
        assertThat(optional.equals(Optional.<Integer>empty()), is(false));
    }

    @Test
    public void equalsWorks() {
        Optional<Integer> value1 = Optional.of(10012);
        Optional<Integer> value2 = Optional.of(10012);

        assertThat(value1.equals(value2), is(true));
    }

    @Test
    public void notEqualsWorks() {
        Optional<Integer> value1 = Optional.of(10012);
        Optional<Integer> value2 = Optional.of(20012);

        assertThat(value1.equals(value2), is(false));
    }

    @Test
    public void equalsNullWorks() {
        Optional<Integer> value1 = Optional.of(10012);
        Optional<Integer> value2 = null;

        assertThat(value1.equals(value2), is(false));
    }

    @Test
    public void equalsSameObject() {
        Optional<Integer> value = Optional.of(10012);

        assertThat(value.equals(value), is(true));
    }

    @Test
    public void notEquals_DifferentType() {
        Optional<Integer> value = Optional.of(10012);

        assertThat(value.equals(new Double(10.4d)), is(false));
    }

    @Test
    public void hashCodeReturned() {
        String string = "Hash of String";
        Optional<String> optional = Optional.of(string);

        assertThat(optional.hashCode(), is(string.hashCode()));
    }

    @Test
    public void hashCodeOfNull() {
        Optional<String> optional = Optional.ofNullable(null);

        assertThat(optional.hashCode(), is(0));
    }

    @Test
    public void orElse_WhenValuePresent() {
        Optional<Integer> optional = Optional.ofNullable(999);
        assertThat(optional.orElse(4), is(999));
    }

    @Test
    public void orElse_WhenValueNotPresent() {
        Optional<Integer> optional = Optional.ofNullable(null);
        assertThat(optional.orElse(4), is(4));
    }

}