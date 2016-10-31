package fi.vincit.multiusertest.rule.expection;

import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ExpectationsTest {

    @Test
    public void testValueOf() throws Exception {
        assertThat(Expectations.valueOf(() -> null), notNullValue());
    }

    @Test(expected = NullPointerException.class)
    public void testValueOf_Null() throws Exception {
        Expectations.valueOf(null);
    }

    @Test
    public void testCall() throws Exception {
        assertThat(Expectations.call(() -> {
        }), notNullValue());
    }

    @Test(expected = NullPointerException.class)
    public void testCall_Null() throws Exception {
        Expectations.call(null);
    }
}