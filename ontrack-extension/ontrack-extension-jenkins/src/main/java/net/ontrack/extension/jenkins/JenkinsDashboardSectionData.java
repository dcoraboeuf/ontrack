package net.ontrack.extension.jenkins;

import lombok.Data;
import net.ontrack.extension.jenkins.client.JenkinsJob;

@Data
public class JenkinsDashboardSectionData {

    private final JenkinsJob job;
    private final String css;

}
