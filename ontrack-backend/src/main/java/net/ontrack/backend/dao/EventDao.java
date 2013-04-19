package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TEvent;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EventType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EventDao {

    int createEvent(String author, Integer authorId, EventType eventType, Map<Entity, Integer> entities, Map<String, String> values);

    List<TEvent> list(int offset, int count, Map<Entity, Integer> entities);

    TEvent getById(int id);

    Collection<TEvent> findEventsToSend();

    void eventSent(int id);

    void renameAuthor(int id, String name);
}
