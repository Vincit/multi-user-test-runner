package fi.vincit.multiusertest.util;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MethodCalls {
    private Map<String, Calls> methodCalls = new HashMap<>();
    private int classCalls;

    public MethodCalls() {
    }

    public MethodCalls expectMethodCalls(String name, int expected) {
        methodCalls.put(name, Calls.expected(expected));
        return this;
    }

    public void before() {
        ++classCalls;
    }
    public void after() {
        --classCalls;
    }

    public void call(String methodName) {
        methodCalls.get(methodName).call();
    }

    public void validateMethodCalls() {
        if (classCalls == 0) {
            for (Map.Entry<String, Calls> e : methodCalls.entrySet()) {
                assertThat(e.getKey(), e.getValue().getCalled(), is(e.getValue().getExpected()));
            }
        }
    }

}
