package net.ontrack.extension.github;

import com.google.common.base.Function;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.support.MessageAnnotation;
import net.ontrack.core.support.MessageAnnotator;
import net.ontrack.core.support.RegexMessageAnnotator;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.git.service.GitMessageAnnotator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitHubMessageAnnotator implements GitMessageAnnotator {

    private final ExtensionManager extensionManager;
    private final PropertiesService propertiesService;

    @Autowired
    public GitHubMessageAnnotator(ExtensionManager extensionManager, PropertiesService propertiesService) {
        this.extensionManager = extensionManager;
        this.propertiesService = propertiesService;
    }

    @Override
    public MessageAnnotator annotator(BranchSummary branch) {
        if (extensionManager.isExtensionEnabled(GitHubExtension.EXTENSION)) {
            final String project = propertiesService.getPropertyValue(
                    Entity.PROJECT,
                    branch.getProject().getId(),
                    GitHubExtension.EXTENSION,
                    GitHubProjectProperty.NAME);
            if (StringUtils.isNotBlank(project)) {
                return new RegexMessageAnnotator(
                        "(#\\d+)",
                        new Function<String, MessageAnnotation>() {
                            @Override
                            public MessageAnnotation apply(String token) {
                                String id = token.substring(1);
                                return MessageAnnotation.of("a")
                                        .attr("href", GitHubExtension.getIssueUrl(project, id))
                                        .text(token);
                            }
                        }
                );
            }
        }
        return null;
    }
}
