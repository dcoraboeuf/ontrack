package net.ontrack.backend.dao;

import net.ontrack.core.model.Entity;

public interface CommentDao {

    int createComment(Entity entity, int entityId, String content, String author, Integer authorId);

}
