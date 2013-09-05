package net.ontrack.extension.jenkins.client;

import lombok.Data;
import net.ontrack.extension.jenkins.JenkinsJobResult;
import net.ontrack.extension.jenkins.JenkinsJobState;

import java.util.List;

@Data
public class JenkinsJob {

    private final String name;
    private final JenkinsJobResult result;
    private final JenkinsJobState state;
    private final List<JenkinsCulprit> culprits;
    private final JenkinsBuildLink lastBuild;

}
