package net.ontrack.web.api.model;

import com.google.common.base.Function;
import lombok.Data;
import net.ontrack.core.model.BuildSummary;

@Data
public class BuildResource extends AbstractResource<BuildResource> {

    public static Function<BuildSummary, BuildResource> summary = new Function<BuildSummary, BuildResource>() {
        @Override
        public BuildResource apply(BuildSummary o) {
            return new BuildResource(
                    o.getId(),
                    o.getBranch().getProject().getName(),
                    o.getBranch().getName(),
                    o.getName(),
                    o.getDescription()
            );
            // TODO Build view
            // TODO Build UI
        }
    };
    private final int id;
    private final String project;
    private final String branch;
    private final String name;
    private final String description;

}
