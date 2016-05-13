package fi.vincit.multiusertest.util;

import org.junit.runners.model.Statement;

public class RunInitAndBefores<USER, ROLE> extends Statement {

    private final Statement init;
    private final Statement next;
    private final Runnable befores;

    public RunInitAndBefores(Statement init, Statement next, Runnable befores) {
        this.init = init;
        this.next = next;
        this.befores = befores;
    }

    @Override
    public void evaluate() throws Throwable {
        init.evaluate();

        befores.run();

        // FIXME: Run login here somehow?

        next.evaluate();
    }

}
