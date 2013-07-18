package net.ontrack.web.hateoas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountResource extends AbstractResource<AccountResource> {

    private final int accountId;
    private final String name;
    private final String fullName;

}
