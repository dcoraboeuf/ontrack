package net.ontrack.backend.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.core.security.GlobalFunction;

public interface GlobalAuthorizationDao {

    Ack set(int account, GlobalFunction fn);

    Ack unset(int account, GlobalFunction fn);

}
