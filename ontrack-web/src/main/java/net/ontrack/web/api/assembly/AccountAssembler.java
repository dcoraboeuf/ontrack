package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.Account;
import net.ontrack.web.api.model.AccountResource;

public interface AccountAssembler {

    Function<Account, AccountResource> summary();

}
