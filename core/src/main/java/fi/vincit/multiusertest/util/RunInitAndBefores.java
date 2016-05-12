package fi.vincit.multiusertest.util;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.List;

public class RunInitAndBefores<USER, ROLE> extends Statement {

    private final Statement init;
    private final Statement next;

    private final Object target;

    private final List<FrameworkMethod> befores;

    public RunInitAndBefores(Statement init, Statement next, List<FrameworkMethod> befores, Object target) {
        this.init = init;
        this.next = next;
        this.befores = befores;
        this.target = target;
    }

    @Override
    public void evaluate() throws Throwable {
        init.evaluate();

        for (FrameworkMethod before : befores) {
            before.invokeExplosively(target);
        }

        // FIXME: Run login here somehow?

        next.evaluate();
    }

}
