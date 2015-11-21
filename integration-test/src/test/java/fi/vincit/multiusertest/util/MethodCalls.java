package fi.vincit.multiusertest.util;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MethodCalls {
    private Map<String, Calls> methodCalls = new HashMap<>();
    private int classCalls;
    private int totalClassCallsExpected;

    public MethodCalls(int classCallsTotalExpected) {
        totalClassCallsExpected = classCallsTotalExpected;
    }

    public void expectMethodCalls(String name, int expected) {
        methodCalls.put(name, Calls.expected(expected));
    }

    public void addClassCall() {
        ++classCalls;
    }

    public boolean shouldInit() {
        return classCalls == 0;
    }

    public void validateClassCalls() {
        assertThat(classCalls, lessThanOrEqualTo(totalClassCallsExpected));
    }

    public void call(String methodName) {
        methodCalls.get(methodName).call();
    }

    public void validateMethodCalls() {
        if (classCalls == totalClassCallsExpected) {
            for (Map.Entry<String, Calls> e : methodCalls.entrySet()) {
                assertThat(e.getKey(), e.getValue().getCalled(), is(e.getValue().getExpected()));
            }
        }
    }

}
