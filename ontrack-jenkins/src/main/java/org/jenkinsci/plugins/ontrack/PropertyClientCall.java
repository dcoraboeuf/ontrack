package org.jenkinsci.plugins.ontrack;

import net.ontrack.client.PropertyUIClient;

public interface PropertyClientCall<T> {

    T onCall(PropertyUIClient ui);

}
