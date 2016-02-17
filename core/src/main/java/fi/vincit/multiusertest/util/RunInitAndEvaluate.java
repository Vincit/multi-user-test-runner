package fi.vincit.multiusertest.util;

import org.junit.runners.model.Statement;

public class RunInitAndEvaluate extends Statement {

    private final Statement init;
    private final Statement next;

    public RunInitAndEvaluate(Statement init, Statement next) {
        this.init = init;
        this.next = next;
    }

    @Override
    public void evaluate() throws Throwable {
        init.evaluate();
        next.evaluate();
    }

}
