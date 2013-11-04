package net.ontrack.backend.security;

import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertTrue;

public class SecurityUtilsImplTest {

    private final SecurityUtils utils = new SecurityUtilsImpl();

    @Test
    public void asAdmin_has_admin_role() {
        SecurityContextHolder.clearContext();
        utils.asAdmin(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Checks all the global functions
                for (GlobalFunction fn : GlobalFunction.values()) {
                    assertTrue("As admin must be granted " + fn, utils.isGranted(fn));
                }
                // Checks all the project functions
                for (ProjectFunction fn : ProjectFunction.values()) {
                    assertTrue("As admin must be granted " + fn + " on any project", utils.isGranted(fn, 1));
                }
                // OK
                return null;
            }
        });
    }

}
