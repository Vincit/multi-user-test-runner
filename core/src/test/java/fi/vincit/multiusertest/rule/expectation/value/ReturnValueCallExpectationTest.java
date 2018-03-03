package fi.vincit.multiusertest.rule.expectation.value;

import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReturnValueCallExpectationTest {

    @Rule
    public ExpectedException expectException = ExpectedException.none();

    @Test
    public void throwIfExceptionIsExpected_value() throws Exception {
        ReturnValueCallExpectation<Integer> sut = new ReturnValueCallExpectation<>(1);
        sut.handleExceptionNotThrown(UserIdentifier.getAnonymous());
    }

    @Test
    public void throwIfExceptionIsExpected_assertion() throws Exception {
        ReturnValueCallExpectation<Integer> sut = new ReturnValueCallExpectation<>(value -> {});
        sut.handleExceptionNotThrown(UserIdentifier.getAnonymous());
    }

    @Test
    public void throwIfExpectationNotExpected_value() throws Throwable {
        ReturnValueCallExpectation<Integer> sut = new ReturnValueCallExpectation<>(1);

        expectException.expect(AssertionError.class);
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

    @Test
    public void throwIfExpectationNotExpected_assertion() throws Throwable {
        ReturnValueCallExpectation<Integer> sut = new ReturnValueCallExpectation<>(value -> {});

        expectException.expect(AssertionError.class);
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

    @Test
    public void callAndAssertValue_value() throws Throwable {
        ReturnValueCallExpectation<Integer> sut = new ReturnValueCallExpectation<>(1);
        sut.callAndAssertValue(() -> 1);
    }

    @Test
    public void callAndAssertValue_assertion() throws Throwable {
        ReturnValueCallExpectation<Integer> sut =
                new ReturnValueCallExpectation<>(value -> assertThat(value, is(2)));
        sut.callAndAssertValue(() -> 2);
    }

    @Test
    public void callAndAssertValue_value_fail() throws Throwable {
        ReturnValueCallExpectation<Integer> sut =
                new ReturnValueCallExpectation<>(1);

        expectException.expect(AssertionError.class);
        sut.callAndAssertValue(() -> 2);
    }

    @Test
    public void callAndAssertValue_assertion_fail() throws Throwable {
        ReturnValueCallExpectation<Integer> sut =
                new ReturnValueCallExpectation<>(value -> assertThat(value, is(2)));

        expectException.expect(AssertionError.class);
        sut.callAndAssertValue(() -> 3);
    }

    @Test
    public void callAndAssertValue_assertion_other_fail() throws Throwable {
        ReturnValueCallExpectation<Integer> sut =
                new ReturnValueCallExpectation<>(
                        value -> {throw new IllegalStateException("Thrown from assertion");}
        );

        expectException.expect(IllegalStateException.class);
        expectException.expectMessage("Thrown from assertion");
        sut.callAndAssertValue(() -> 3);
    }

}