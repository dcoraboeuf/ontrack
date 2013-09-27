package net.ontrack.web.hateoas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.ontrack.core.model.Account;

import java.util.Locale;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountResource extends AbstractResource<AccountResource> {

    private final int accountId;
    private final String name;
    private final String fullName;
    private final String email;
    private final String roleName;
    private final String mode;
    private Locale locale;

    public AccountResource(Account o) {
        this(o.getId(), o.getName(), o.getFullName(), o.getEmail(), o.getRoleName(), o.getMode(), o.getLocale());
    }
}
