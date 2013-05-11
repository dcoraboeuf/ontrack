package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Decoration;
import net.sf.jstring.LocalizableMessage;
import org.springframework.stereotype.Component;

@Component
public class DefaultJenkinsDecorator implements JenkinsDecorator {
    @Override
    public Decoration getJobDecoration(JenkinsJobState jenkinsJobState) {
        switch (jenkinsJobState) {
            case DISABLED:
                return new Decoration(new LocalizableMessage("jenkins.job.disabled")).withIconPath("extension/jenkins-job-disabled.png");
            case RUNNING:
                return new Decoration(new LocalizableMessage("jenkins.job.running")).withIconPath("extension/jenkins-job-running.png");
            case IDLE:
                return new Decoration(new LocalizableMessage("jenkins.job.idle")).withIconPath("extension/jenkins-job-idle.png");
            default:
                return null;
        }
    }
}
