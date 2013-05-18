package net.ontrack.core.ui;

import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.Ack;

public interface AdminUI {

    Ack createAccount(AccountCreationForm form);

}
