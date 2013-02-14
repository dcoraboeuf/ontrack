package org.jenkinsci.plugins.ontrack;

import net.ontrack.core.ui.ControlUI;

public interface ClientCall<T> {

    T onCall (ControlUI ui);

}
