package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows to notify for a build.
 */
public class OntrackBuildNotifier extends AbstractOntrackNotifier {

    public static final String REGEX_ENV_VARIABLE = "\\$\\{([a-zA-Z0-9_]+)\\}";

    private final String project;
    private final String branch;
    private final String build;

    @DataBoundConstructor
    public OntrackBuildNotifier(String project, String branch, String build) {
        this.project = project;
        this.branch = branch;
        this.build = build;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getBuild() {
        return build;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Expands the expressions into actual values
        String projectName = expand(project, theBuild, listener);
        String branchName = expand(branch, theBuild, listener);
        String buildName= expand(build, theBuild, listener);
        // Logging of parameters
        listener.getLogger().format("Creating build %s on project %s for branch %s%n", buildName, projectName, branchName);
        // FIXME Calling ontrack UI
        // OK
        return true;
    }

    protected String expand(String template, AbstractBuild<?, ?> theBuild, BuildListener listener) {
        if (StringUtils.isBlank(template)) {
            return template;
        } else {
            Pattern pattern = Pattern.compile(REGEX_ENV_VARIABLE);
            Matcher matcher = pattern.matcher(template);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String name = matcher.group(1);
                String value = getParameter(name, theBuild, listener);
                if (value == null) {
                    throw new IllegalStateException("Cannot find any replacement value for environment variable " + name);
                }
                matcher = matcher.appendReplacement(result, value);
            }
            matcher.appendTail(result);
            return result.toString();
        }
    }

    private String getParameter(String name, AbstractBuild<?, ?> theBuild, BuildListener listener) {
        String value = (String) theBuild.getBuildVariableResolver().resolve(name);
        if (value != null) {
            return value;
        } else {
            try {
                return theBuild.getEnvironment(listener).get(name);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot get value for " + name, ex);
            }
        }
    }

    @Override
    public OntrackBuildDescriptorImpl getDescriptor() {
        return (OntrackBuildDescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class OntrackBuildDescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String ontrackUrl;
        private String ontrackUser;
        private String ontrackPassword;

        public OntrackBuildDescriptorImpl() {
            super(OntrackBuildNotifier.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "ontrack Build creation";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            ontrackUrl = json.getString("ontrackUrl");
            ontrackUser = json.getString("ontrackUser");
            ontrackPassword = json.getString("ontrackPassword");
            save();
            return super.configure(req, json);
        }

        public String getOntrackUrl() {
            return ontrackUrl;
        }

        public String getOntrackUser() {
            return ontrackUser;
        }

        public String getOntrackPassword() {
            return ontrackPassword;
        }
    }
}
