package net.ontrack.extension.svn;

import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SubversionExtension extends ExtensionAdapter {

    public static final String EXTENSION = "svn";

    @Autowired
    public SubversionExtension(
            SubversionPathPropertyExtension subversionPathPropertyExtension,
            SubversionBuildPathPropertyExtension subversionBuildPathPropertyExtension,
            SubversionConfigurationExtension subversionConfigurationExtension,
            IndexationConfigurationExtension indexationConfigurationExtension,
            IndexationActionController indexationActionController) {
        super(
                EXTENSION,
                Arrays.asList(
                        subversionPathPropertyExtension,
                        subversionBuildPathPropertyExtension
                ),
                Arrays.asList(
                        subversionConfigurationExtension,
                        indexationConfigurationExtension
                ),
                Arrays.asList(
                        indexationActionController
                ));
    }
}
