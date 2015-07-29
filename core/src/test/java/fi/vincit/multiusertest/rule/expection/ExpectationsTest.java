package fi.vincit.multiusertest.rule.expection;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ExpectationsTest {

    @Test
    public void testValueOf() throws Exception {
        assertThat(Expectations.valueOf(new ReturnValueCall<Object>() {
            @Override
            public Object call() {
                return null;
            }
        }), notNullValue());
    }

    @Test(expected = NullPointerException.class)
    public void testValueOf_Null() throws Exception {
        Expectations.valueOf(null);
    }

    @Test
    public void testCall() throws Exception {
        assertThat(Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
            }
        }), notNullValue());
    }

    @Test(expected = NullPointerException.class)
    public void testCall_Null() throws Exception {
        Expectations.call(null);
    }
}