package net.ontrack.web.hateoas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.ontrack.core.model.AccountSummary;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountNameResource extends AbstractResource<AccountNameResource> {

    private final int accountId;
    private final String name;
    private final String fullName;

    public AccountNameResource(AccountSummary o) {
        this(o.getId(), o.getName(), o.getFullName());
    }
}
