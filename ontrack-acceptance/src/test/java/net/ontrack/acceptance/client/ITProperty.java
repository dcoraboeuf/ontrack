package net.ontrack.acceptance.client;

import net.ontrack.client.AdminUIClient;
import net.ontrack.client.PropertyUIClient;
import net.ontrack.client.support.AdminClientCall;
import net.ontrack.client.support.PropertyClientCall;
import net.ontrack.core.model.EditableProperty;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectSummary;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ITProperty extends AbstractIT {

    @Test
    public void editableProperty_french() {
        // Prerequisites
        final ProjectSummary project = data.doCreateProject();
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

}
