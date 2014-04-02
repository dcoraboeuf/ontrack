package net.ontrack.acceptance.client;

import net.ontrack.client.ManageUIClient;
import net.ontrack.client.support.ManageClientCall;
import net.ontrack.core.model.*;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.*;

public class ITManage extends AbstractIT {

    @Test
    public void imageValidationStamp() throws IOException {
        // Prerequisites
        final ValidationStampSummary validationStamp = data.doCreateValidationStamp();
        // Default image
        byte[] defaultContent = data.anonymous(new ManageClientCall<byte[]>() {
            @Override
            public byte[] onCall(ManageUIClient client) {
                return client.imageValidationStamp(
                        validationStamp.getBranch().getProject().getName(),
                        validationStamp.getBranch().getName(),
                        validationStamp.getName()
                );
            }
        });
        assertNotNull(defaultContent);
        // Checks the content (empty for the default image)
        assertArrayEquals(
                new byte[0],
                defaultContent
        );
        // Test image
        final MultipartFile file = data.mockImage("/images/fire.png");
        // Sets the image
        Ack ack = data.asAdmin(new ManageClientCall<Ack>() {
            @Override
            public Ack onCall(ManageUIClient client) {
                return client.setImageValidationStamp(
                        validationStamp.getBranch().getProject().getName(),
                        validationStamp.getBranch().getName(),
                        validationStamp.getName(),
                        file
                );
            }
        });
        assertNotNull(ack);
        assertTrue(ack.isSuccess());
        // Gets the image
        byte[] content = data.anonymous(new ManageClientCall<byte[]>() {
            @Override
            public byte[] onCall(ManageUIClient client) {
                return client.imageValidationStamp(
                        validationStamp.getBranch().getProject().getName(),
                        validationStamp.getBranch().getName(),
                        validationStamp.getName()
                );
            }
        });
        // Checks equality
        assertArrayEquals(
                file.getBytes(),
                content
        );
    }

    @Test
    public void imagePromotionLevel() throws IOException {
        // Prerequisites
        final PromotionLevelSummary promotionLevel = data.doCreatePromotionLevel();
        // Default image
        byte[] defaultContent = data.anonymous(new ManageClientCall<byte[]>() {
            @Override
            public byte[] onCall(ManageUIClient client) {
                return client.imagePromotionLevel(
                        promotionLevel.getBranch().getProject().getName(),
                        promotionLevel.getBranch().getName(),
                        promotionLevel.getName()
                );
            }
        });
        assertNotNull(defaultContent);
        // Checks the content (empty for the default image)
        assertArrayEquals(
                new byte[0],
                defaultContent
        );
        // Test image
        final MultipartFile file = data.mockImage("/images/server.png");
        // Sets the image
        Ack ack = data.asAdmin(new ManageClientCall<Ack>() {
            @Override
            public Ack onCall(ManageUIClient client) {
                return client.setImagePromotionLevel(
                        promotionLevel.getBranch().getProject().getName(),
                        promotionLevel.getBranch().getName(),
                        promotionLevel.getName(),
                        file
                );
            }
        });
        assertNotNull(ack);
        assertTrue(ack.isSuccess());
        // Gets the image
        byte[] content = data.anonymous(new ManageClientCall<byte[]>() {
            @Override
            public byte[] onCall(ManageUIClient client) {
                return client.imagePromotionLevel(
                        promotionLevel.getBranch().getProject().getName(),
                        promotionLevel.getBranch().getName(),
                        promotionLevel.getName()
                );
            }
        });
        // Checks equality
        assertArrayEquals(
                file.getBytes(),
                content
        );
    }

    @Test
    public void createProject() {
        // No prerequisite
        data.delete_project("ui_createProject");
        // Call
        ProjectSummary summary = data.asAdmin(new ManageClientCall<ProjectSummary>() {
            @Override
            public ProjectSummary onCall(ManageUIClient ui) {
                return ui.createProject(
                        new ProjectCreationForm(
                                "ui_createProject",
                                "ui_createProject description"
                        )
                );
            }
        });
        // Checks
        assertNotNull(summary);
        assertTrue(summary.getId() > 0);
        assertEquals("ui_createProject", summary.getName());
        assertEquals("ui_createProject description", summary.getDescription());
    }

    @Test
    public void last_build_with_promotion_level_when_no_build() {
        // Prerequisites
        final PromotionLevelSummary promotionLevel = data.doCreatePromotionLevel();
        // Call
        OptionalBuildSummary optionalBuildSummary = data.asAdmin(new ManageClientCall<OptionalBuildSummary>() {
            @Override
            public OptionalBuildSummary onCall(ManageUIClient ui) {
                return ui.getLastBuildWithPromotionLevel(
                        Locale.ENGLISH,
                        promotionLevel.getBranch().getProject().getName(),
                        promotionLevel.getBranch().getName(),
                        promotionLevel.getName()
                );
            }
        });
        // Checks
        assertNotNull(optionalBuildSummary);
        assertNull("The returned build is null", optionalBuildSummary.getBuild());
    }

}
