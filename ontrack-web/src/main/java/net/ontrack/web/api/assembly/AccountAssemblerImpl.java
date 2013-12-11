package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.web.api.model.AccountResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountAssemblerImpl extends AbstractAssembler implements AccountAssembler {

    @Autowired
    public AccountAssemblerImpl(SecurityUtils securityUtils) {
        super(securityUtils);
    }

    @Override
    public Function<Account, AccountResource> summary() {
        return new Function<Account, AccountResource>() {
            @Override
            public AccountResource apply(Account o) {
                return new AccountResource(
                        o.getId(),
                        o.getName(),
                        o.getFullName(),
                        o.getEmail(),
                        o.getRoleName(),
                        o.getMode(),
                        o.getLocale(),
                        o.getGlobalACL(),
                        o.getProjectACL()
                );
            }
        };
    }
}
