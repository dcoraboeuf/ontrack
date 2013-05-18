package net.ontrack.client.support;

import net.ontrack.client.AdminUIClient;

public interface AdminClientCall<T> {

    T onCall(AdminUIClient ui);

}
