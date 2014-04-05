package net.ontrack.service;

import net.ontrack.core.model.UserMessage;

import java.util.Collection;

public interface InfoProvider {

    Collection<UserMessage> getInfo();

}
