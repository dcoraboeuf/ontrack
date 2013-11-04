package net.ontrack.core.security;

import net.ontrack.core.model.Account;
import net.ontrack.core.model.Signature;

import java.util.concurrent.Callable;

public interface SecurityUtils {

    boolean isLogged();

    Account getCurrentAccount();

    int getCurrentAccountId();

    Signature getCurrentSignature();

    void checkIsLogged();

    <T> T asAdmin(Callable<T> call);

    <T> Callable<T> withCurrentCredentials(Callable<T> callable);

    void checkGrant(GlobalFunction fn);

    void checkGrant(ProjectFunction fn, int project);

    boolean isGranted(GlobalFunction fn);

    boolean isGranted(ProjectFunction fn, int project);
}
