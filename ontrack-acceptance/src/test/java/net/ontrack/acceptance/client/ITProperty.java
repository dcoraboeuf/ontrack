package net.ontrack.acceptance.client;

import net.ontrack.client.AdminUIClient;
import net.ontrack.client.PropertyUIClient;
import net.ontrack.client.support.AdminClientCall;
import net.ontrack.client.support.PropertyClientCall;
import net.ontrack.core.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ITProperty extends AbstractIT {

    @Before
    public void before() {
        data.asAdmin(new AdminClientCall<Void>() {
            @Override
            public Void onCall(AdminUIClient ui) {
                // Enables 'link' & 'svnexplorer' extensions
                ui.enableExtension("link");
                ui.enableExtension("svnexplorer");
                // OK
                return null;
            }
        });
    }

    @Test
    public void editableProperty_french() {
        // Prerequisites
        final ProjectSummary project = data.doCreateProject();
        // Call
        List<EditableProperty> editableProperties = data.asAdmin(new PropertyClientCall<List<EditableProperty>>() {
            @Override
            public List<EditableProperty> onCall(PropertyUIClient client) {
                return client.getEditableProperties(
                        Locale.FRENCH,
                        Entity.PROJECT,
                        project.getId()
                );
            }
        });
        // Checks
        assertNotNull(editableProperties);
        assertEquals(2, editableProperties.size());
        {
            EditableProperty editableProperty = editableProperties.get(0);
            assertNotNull(editableProperty);
            assertEquals("link", editableProperty.getExtension());
            assertEquals("url", editableProperty.getName());
            assertEquals("Lien", editableProperty.getDisplayName());
            System.out.println(editableProperty);
        }
        {
            EditableProperty editableProperty = editableProperties.get(1);
            assertNotNull(editableProperty);
            assertEquals("svnexplorer", editableProperty.getExtension());
            assertEquals("rootPath", editableProperty.getName());
            assertEquals("Chemin racine SVN", editableProperty.getDisplayName());
            System.out.println(editableProperty);
        }
    }

    @Test
    public void editableProperty_english() {
        // Prerequisites
        final ProjectSummary project = data.doCreateProject();
        // Call
        List<EditableProperty> editableProperties = data.asAdmin(new PropertyClientCall<List<EditableProperty>>() {
            @Override
            public List<EditableProperty> onCall(PropertyUIClient client) {
                return client.getEditableProperties(
                        Locale.ENGLISH,
                        Entity.PROJECT,
                        project.getId()
                );
            }
        });
        // Checks
        assertNotNull(editableProperties);
        assertEquals(2, editableProperties.size());
        {
            EditableProperty editableProperty = editableProperties.get(0);
            assertNotNull(editableProperty);
            assertEquals("link", editableProperty.getExtension());
            assertEquals("url", editableProperty.getName());
            assertEquals("Link", editableProperty.getDisplayName());
            System.out.println(editableProperty);
        }
        {
            EditableProperty editableProperty = editableProperties.get(1);
            assertNotNull(editableProperty);
            assertEquals("svnexplorer", editableProperty.getExtension());
            assertEquals("rootPath", editableProperty.getName());
            assertEquals("SVN root path", editableProperty.getDisplayName());
            System.out.println(editableProperty);
        }
    }

    @Test
    public void findEntity() {
        final String value = "http://" + data.uid("P");
        // Prerequisites
        final BuildSummary build = data.doCreateBuild();
        // Sets a property
        data.asAdmin(new PropertyClientCall<Void>() {
            @Override
            public Void onCall(PropertyUIClient ui) {
                ui.saveProperty(Entity.BUILD, build.getId(), "link", "url", new PropertyForm(value));
                return null;
            }
        });
        // Retrieves the build
        Collection<EntityStub> stubs = data.anonymous(new PropertyClientCall<EntityStubCollection>() {
            @Override
            public EntityStubCollection onCall(PropertyUIClient ui) {
                return ui.getEntitiesForPropertyValue(Entity.BUILD, new PropertyValue("link", "url", value));
            }
        }).getEntities();
        // Checks
        assertEquals(1, stubs.size());
        EntityStub stub = stubs.iterator().next();
        assertEquals(Entity.BUILD, stub.getEntity());
        assertEquals(build.getId(), stub.getId());
        assertEquals(build.getName(), stub.getName());
    }

}
