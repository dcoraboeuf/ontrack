package net.ontrack.extension.pkg;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.service.GUIService;
import net.ontrack.service.ManagementService;
import net.ontrack.service.SearchProvider;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PackageSearchProvider implements SearchProvider {

    private final PropertiesService propertiesService;
    private final ManagementService managementService;
    private final GUIService guiService;

    @Autowired
    public PackageSearchProvider(PropertiesService propertiesService, ManagementService managementService, GUIService guiService) {
        this.propertiesService = propertiesService;
        this.managementService = managementService;
        this.guiService = guiService;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return true;
    }

    @Override
    public Collection<SearchResult> search(final String pkg) {
        // Gets the builds that have <token> has value for the <package> property
        Collection<Integer> buildIds = propertiesService.findEntityByPropertyValue(Entity.BUILD, PackageExtension.EXTENSION, PackagePropertyDescriptor.PACKAGE, pkg);
        // Gets the build links
        return Collections2.transform(
                buildIds,
                new Function<Integer, SearchResult>() {
                    @Override
                    public SearchResult apply(Integer buildId) {
                        return createBuildResult(buildId, pkg);
                    }
                }
        );
    }

    private SearchResult createBuildResult(int buildId, String pkg) {
        // Gets the build
        BuildSummary build = managementService.getBuild(buildId);
        // OK
        return new SearchResult(
                String.format("%s/%s/%s", build.getBranch().getProject().getName(), build.getBranch().getName(), build.getName()),
                new LocalizableMessage("package.search", build.getName(), pkg),
                guiService.getBuildGUIURL(build)
        );
    }
}
