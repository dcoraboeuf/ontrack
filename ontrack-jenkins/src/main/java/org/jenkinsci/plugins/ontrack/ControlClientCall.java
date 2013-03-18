package org.jenkinsci.plugins.ontrack;

import net.ontrack.core.ui.ControlUI;

public interface ControlClientCall<T> {

    T onCall (ControlUI ui);

}
