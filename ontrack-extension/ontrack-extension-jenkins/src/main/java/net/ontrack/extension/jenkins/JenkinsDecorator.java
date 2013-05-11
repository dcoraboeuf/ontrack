package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Decoration;

public interface JenkinsDecorator {

    Decoration getJobDecoration(JenkinsJobState jenkinsJobState);

}
