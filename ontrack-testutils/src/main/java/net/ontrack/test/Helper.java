package net.ontrack.test;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;

public final class Helper {

	public static String getResourceAsString(String path) throws IOException {
		InputStream in = Helper.class.getResourceAsStream(path);
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

    public static String uid(String prefix) {
        return prefix + new SimpleDateFormat("mmssSSS").format(new Date());
    }

	private Helper() {
	}

}
