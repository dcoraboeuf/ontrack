package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.web.api.model.BuildResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuildAssemblerImpl extends AbstractAssembler implements BuildAssembler {

    @Autowired
    public BuildAssemblerImpl(SecurityUtils securityUtils) {
        super(securityUtils);
    }

    @Override
    public Function<BuildSummary, BuildResource> summary() {
        return new Function<BuildSummary, BuildResource>() {
            @Override
            public BuildResource apply(BuildSummary o) {
                return new BuildResource(
                        o.getId(),
                        o.getBranch().getProject().getName(),
                        o.getBranch().getName(),
                        o.getName(),
                        o.getDescription()
                )
                        // Build view
                        .withView("/project/%s/branch/%s/build/%s",
                                o.getBranch().getProject().getName(),
                                o.getBranch().getName(),
                                o.getName())
                        // TODO Build UI
                        ;
            }
        };
    }
}
