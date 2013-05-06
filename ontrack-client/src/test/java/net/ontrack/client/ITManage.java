package net.ontrack.client;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.PromotionLevelSummary;
import net.ontrack.core.model.ValidationStampSummary;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.Assert.*;

public class ITManage extends AbstractEnv {

    @Test
    public void imageValidationStamp() throws IOException {
        // Prerequisites
        final ValidationStampSummary validationStamp = doCreateValidationStamp();
        // Default image
        byte[] defaultContent = anonymous(new ManageCall<byte[]>() {
            @Override
            public byte[] call(ManageUIClient client) {
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
        final MultipartFile file = mockImage("/images/fire.png");
        // Sets the image
        Ack ack = asAdmin(new ManageCall<Ack>() {
            @Override
            public Ack call(ManageUIClient client) {
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
        byte[] content = anonymous(new ManageCall<byte[]>() {
            @Override
            public byte[] call(ManageUIClient client) {
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
        final PromotionLevelSummary promotionLevel = doCreatePromotionLevel();
        // Default image
        byte[] defaultContent = anonymous(new ManageCall<byte[]>() {
            @Override
            public byte[] call(ManageUIClient client) {
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
        final MultipartFile file = mockImage("/images/server.png");
        // Sets the image
        Ack ack = asAdmin(new ManageCall<Ack>() {
            @Override
            public Ack call(ManageUIClient client) {
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
        byte[] content = anonymous(new ManageCall<byte[]>() {
            @Override
            public byte[] call(ManageUIClient client) {
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

}
