package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class AllSubscriptions {

    private final List<SubscriptionsForUser> users;

}
