package net.ontrack.backend;

import net.ontrack.backend.dao.EntityDao;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntityStub;
import net.ontrack.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class EntityServiceImpl implements EntityService {

    private final EntityDao entityDao;

    @Autowired
    public EntityServiceImpl(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    @Override
    @Transactional(readOnly = true)
    public int getEntityId(Entity entity, String name, Map<Entity, Integer> parentIds) {
        return entityDao.getEntityId(entity, name, parentIds);
    }

    @Override
    @Transactional(readOnly = true)
    public String getEntityName(Entity entity, int entityId) {
        return entityDao.getEntityName(entity, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public EntityStub getEntityStub(Entity entity, int entityId) {
        return new EntityStub(
                entity,
                entityId,
                getEntityName(entity, entityId)
        );
    }
}
