package net.ontrack.test;

import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Helper {

    private Helper() {
    }

    public static String getResourceAsString(Object ref, String path) throws IOException {
        InputStream in = ref.getClass().getResourceAsStream(path);
        return readString(path, in);
    }

    public static String getResourceAsString(String path) throws IOException {
        InputStream in = Helper.class.getResourceAsStream(path);
        return readString(path, in);
    }

    public static String uid(String prefix) {
        return prefix + new SimpleDateFormat("mmssSSS").format(new Date());
    }

    public static MultipartFile getResourceAsMultipartFile(Object ref, String path, String type) throws IOException {
        return new MockMultipartFile(
                path,
                path,
                type,
                ref.getClass().getResourceAsStream(path)
        );
    }

    private static String readString(String path, InputStream in) throws IOException {
        if (in == null) {
            throw new IOException("Cannot find resource at " + path);
        } else {
            try {
                return IOUtils.toString(in, "UTF-8");
            } finally {
                in.close();
            }
        }
    }
}
