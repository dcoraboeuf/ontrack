package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.service.EntityService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class EntityServiceTest extends AbstractBackendTest {

    @Autowired
    private EntityService service;

    @Test
    public void getEntityId() throws Exception {
        ProjectSummary p = doCreateProject();
        assertEquals(p.getId(), service.getEntityId(Entity.PROJECT, p.getName(), Collections.<Entity, Integer>emptyMap()));
    }

    @Test(expected = EntityNameNotFoundException.class)
    public void getEntityId_not_found() {
        service.getEntityId(Entity.PROJECT, "PROJECTX", Collections.<Entity, Integer>emptyMap());
    }
}
