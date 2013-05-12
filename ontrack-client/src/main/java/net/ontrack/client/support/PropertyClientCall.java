package net.ontrack.client.support;

import net.ontrack.client.PropertyUIClient;

public interface PropertyClientCall<T> {

    T onCall(PropertyUIClient ui);

}
