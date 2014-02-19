package net.ontrack.backend.extension;

import com.google.common.collect.Sets;
import net.ontrack.backend.AbstractBackendTest;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.JenkinsExtension;
import net.ontrack.extension.jenkins.JenkinsUrlPropertyDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class DefaultPropertiesServiceTest extends AbstractBackendTest {

    @Autowired
    private PropertiesService propertiesService;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private ExtensionManager extensionManager;

    @Before
    public void activate_extensions() {
        // Makes sure the Jenkins extension is enabled
        securityUtils.asAdmin(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                extensionManager.enableExtension("jenkins");
                return null;
            }
        });
    }

    @Test
    public void findEntityByPropertyValue_none() {
        Collection<Integer> ids = propertiesService.findEntityByPropertyValue(Entity.PROJECT, "xxx", "xxx", "xxx");
        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    @Test
    public void findEntityByPropertyValue_build_one() throws Exception {
        String value = "http://" + uid("P");
        // For one build...
        BuildSummary build = doCreateBuild();
        // ... adds a property
        propertiesService.saveProperty(Entity.BUILD, build.getId(), JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, value);
        // Gets the ids back
        Collection<Integer> ids = propertiesService.findEntityByPropertyValue(Entity.BUILD, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, value);
        assertEquals(Arrays.asList(build.getId()), ids);
    }

    @Test
    public void findEntityByPropertyValue_build_no_match() throws Exception {
        String value = "http://" + uid("P");
        // For one build...
        BuildSummary build = doCreateBuild();
        // ... adds a property
        propertiesService.saveProperty(Entity.BUILD, build.getId(), JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, value);
        // Gets the ids back
        Collection<Integer> ids = propertiesService.findEntityByPropertyValue(Entity.BUILD, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, StringUtils.substring(value, 0, -1));
        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    @Test
    public void findEntityByPropertyValue_two_builds_two_matches() throws Exception {
        String value = "http://" + uid("P");
        // For two builds...
        BuildSummary build1 = doCreateBuild();
        BuildSummary build2 = doCreateBuild();
        // ... adds the same property
        propertiesService.saveProperty(Entity.BUILD, build1.getId(), JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, value);
        propertiesService.saveProperty(Entity.BUILD, build2.getId(), JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, value);
        // Gets the ids back
        Collection<Integer> ids = propertiesService.findEntityByPropertyValue(Entity.BUILD, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, value);
        assertEquals(
                Sets.newHashSet(build1.getId(), build2.getId()),
                Sets.newHashSet(ids)
        );
    }

    @Test
    public void findEntityByPropertyValue_two_builds_one_match() throws Exception {
        String value = "http://" + uid("P");
        // For two builds...
        BuildSummary build1 = doCreateBuild();
        BuildSummary build2 = doCreateBuild();
        // ... adds the same property
        propertiesService.saveProperty(Entity.BUILD, build1.getId(), JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, value);
        propertiesService.saveProperty(Entity.BUILD, build2.getId(), JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, StringUtils.substring(value, 0, -1));
        // Gets the ids back
        Collection<Integer> ids = propertiesService.findEntityByPropertyValue(Entity.BUILD, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, value);
        assertEquals(
                Sets.newHashSet(build1.getId()),
                Sets.newHashSet(ids)
        );
    }

}
