package net.ontrack.web.config;

import freemarker.cache.URLTemplateLoader;
import net.ontrack.extension.api.Extension;
import net.ontrack.extension.api.ExtensionManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtensionTemplateLoader extends URLTemplateLoader {

    private final Pattern namePattern = Pattern.compile("extension/([a-zA-Z0-9_\\-\\.]+)/(.+)");
    private final ExtensionManager extensionManager;

    public ExtensionTemplateLoader(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    @Override
    protected URL getURL(String name) {
        Matcher m = namePattern.matcher(name);
        if (m.matches()) {
            String extensionName = m.group(1);
            String extraPath = m.group(2);
            // Gets the corresponding extension
            Extension extension = extensionManager.getExtension(extensionName);
            // Gets the path
            String path = String.format("META-INF/views/extension/%s/%s", extensionName, extraPath);
            // Loads the URL
            URL url = extension.getClass().getClassLoader().getResource(path);
            // OK (maybe null if not found)
            return url;
        } else {
            return null;
        }
    }
}
