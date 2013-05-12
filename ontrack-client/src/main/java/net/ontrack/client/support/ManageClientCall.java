package net.ontrack.client.support;

import net.ontrack.client.ManageUIClient;

public interface ManageClientCall<T> {

    T onCall(ManageUIClient ui);

}
