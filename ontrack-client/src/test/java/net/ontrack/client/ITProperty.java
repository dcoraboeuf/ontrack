package net.ontrack.client;

import net.ontrack.core.model.EditableProperty;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectSummary;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ITProperty extends AbstractEnv {

    @Test
    @Ignore
    public void editableProperty_french() {
        // Prerequisites
        final ProjectSummary project = doCreateProject();
        // Call
        List<EditableProperty> editableProperties = asAdmin(new PropertyCall<List<EditableProperty>>() {
            @Override
            public List<EditableProperty> call(PropertyUIClient client) {
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
            assertEquals("extension-link-url", editableProperty.getName());
            assertEquals("Lien", editableProperty.getDisplayName());
            System.out.println(editableProperty);
        }
    }

}
