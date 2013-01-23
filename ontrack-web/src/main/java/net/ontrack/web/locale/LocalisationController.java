package net.ontrack.web.locale;

import static org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript;
import static org.apache.commons.lang3.StringUtils.replace;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jstring.Strings;

public abstract class LocalisationController {

	protected final Strings strings;

	public LocalisationController(Strings strings) {
		this.strings = strings;
	}
	
	public abstract void localisation(Locale locale, HttpServletResponse response, String language, String version) throws IOException;

	protected String generateJS(Locale locale) {
		// Restricts the locale
		locale = strings.getSupportedLocales().filterForLookup(locale);
		
		// Gets the list of key/values
		Map<String, String> map = strings.getKeyValues(locale);
		// Output
		StringBuilder js = new StringBuilder();
		js.append("// ").append(new Date()).append("\n");
		js.append("var l = {\n");
		int i = 0;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (i > 0) {
				js.append(",\n");
			}
			js.append(String.format("'%s': '%s'", key, escape(value)));
			i++;
		}
		js.append("\n};\n");
		// Content
		String content = js.toString();
		return content;
	}

	protected String escape(String value) {
		return escapeEcmaScript(replace(value, "''", "'"));
	}

	protected void writeJS(String content, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		byte[] bytes = content.getBytes("UTF-8");
		response.setContentType("text/javascript");
		response.setContentLength(bytes.length);
		ServletOutputStream outputStream = response.getOutputStream();
		outputStream.write(bytes);
		outputStream.flush();
	}

}
