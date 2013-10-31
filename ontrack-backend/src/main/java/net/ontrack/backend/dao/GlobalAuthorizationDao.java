package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TGlobalAuthorization;
import net.ontrack.core.model.Ack;
import net.ontrack.core.security.GlobalFunction;

import java.util.List;

public interface GlobalAuthorizationDao {

    Ack set(int account, GlobalFunction fn);

    Ack unset(int account, GlobalFunction fn);

    List<TGlobalAuthorization> all();

    List<TGlobalAuthorization> findByAccount(int account);
}
