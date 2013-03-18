package org.jenkinsci.plugins.ontrack;

import net.ontrack.client.ManageUIClient;

public interface ManageClientCall<T> {

    T onCall(ManageUIClient ui);

}
