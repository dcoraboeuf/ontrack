package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TComment;
import net.ontrack.core.model.Entity;

import java.util.Collection;

public interface CommentDao {

    int createComment(Entity entity, int entityId, String content, String author, Integer authorId);

    Collection<TComment> findByEntityAndText(Entity entity, String text);

    void renameAuthor(int id, String name);
}
