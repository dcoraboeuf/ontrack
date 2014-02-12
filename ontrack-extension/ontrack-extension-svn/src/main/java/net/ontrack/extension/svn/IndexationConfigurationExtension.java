package net.ontrack.extension.svn;

import com.google.common.collect.Lists;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.IntegerConfigurationExtensionField;
import net.ontrack.extension.api.configuration.LongConfigurationExtensionField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Deprecated
public class IndexationConfigurationExtension implements ConfigurationExtension {

    public static final String SCAN_INTERVAL = "scanInterval";
    public static final String START_REVISION = "startRevision";

    private int scanInterval = 0;
    private long startRevision = 1;

    public int getScanInterval() {
        return scanInterval;
    }

    public long getStartRevision() {
        return startRevision;
    }

    @Override
    public String getExtension() {
        return SubversionExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "indexation";
    }

    @Override
    public String getTitleKey() {
        return "subversion.indexation.configuration";
    }

    @Override
    public List<? extends ConfigurationExtensionField> getFields() {
        // Converts to fields
        return Lists.newArrayList(
                new IntegerConfigurationExtensionField(SCAN_INTERVAL, "subversion.indexation.configuration.scanInterval", 0, 0, 10000, scanInterval),
                new LongConfigurationExtensionField(START_REVISION, "subversion.indexation.configuration.startRevision", 1, 1, Long.MAX_VALUE, startRevision)
        );
    }

    @Override
    public void configure(String name, String value) {
        switch (name) {
            case SCAN_INTERVAL:
                scanInterval = Integer.parseInt(value, 10);
                break;
            case START_REVISION:
                startRevision = Long.parseLong(value, 10);
                break;
        }
    }
}
