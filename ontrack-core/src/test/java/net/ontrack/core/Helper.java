package net.ontrack.core;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

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

	private Helper() {
	}

}
