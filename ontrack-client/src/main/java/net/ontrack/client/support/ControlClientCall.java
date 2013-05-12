package net.ontrack.client.support;

import net.ontrack.client.ControlUIClient;

public interface ControlClientCall<T> {

    T onCall(ControlUIClient ui);

}
