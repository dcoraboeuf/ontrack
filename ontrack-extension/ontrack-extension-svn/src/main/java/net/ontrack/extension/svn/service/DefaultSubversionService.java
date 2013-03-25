package net.ontrack.extension.svn.service;

import net.ontrack.extension.svn.SubversionConfigurationExtension;
import net.ontrack.extension.svn.SubversionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultSubversionService implements SubversionService {

    private final SubversionConfigurationExtension configurationExtension;

    @Autowired
    public DefaultSubversionService(SubversionConfigurationExtension configurationExtension) {
        this.configurationExtension = configurationExtension;
    }

    @Override
    public String getURL(String path) {
        return configurationExtension.getConfiguration().getUrl() + path;
    }

    @Override
    public String getBrowsingURL(String path) {
        String browserForPath = configurationExtension.getConfiguration().getBrowserForPath();
        if (StringUtils.isNotBlank(browserForPath)) {
            return browserForPath.replace("*", path);
        } else {
            return getURL(path);
        }
    }
}
