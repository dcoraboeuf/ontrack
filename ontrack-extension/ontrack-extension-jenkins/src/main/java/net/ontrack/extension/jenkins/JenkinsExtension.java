package net.ontrack.extension.jenkins;

import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class JenkinsExtension extends ExtensionAdapter {

    public static final String EXTENSION = "jenkins";
    private final JenkinsUrlPropertyDescriptor jenkinsUrlPropertyDescriptor;
    private final ValidationStampJenkinsJobStateDecorator validationStampJenkinsJobStateDecorator;
    private final BranchJenkinsJobStateDecorator branchJenkinsJobStateDecorator;

    @Autowired
    public JenkinsExtension(
            JenkinsUrlPropertyDescriptor jenkinsUrlPropertyDescriptor,
            ValidationStampJenkinsJobStateDecorator validationStampJenkinsJobStateDecorator,
            BranchJenkinsJobStateDecorator branchJenkinsJobStateDecorator) {
        super(EXTENSION);
        this.jenkinsUrlPropertyDescriptor = jenkinsUrlPropertyDescriptor;
        this.validationStampJenkinsJobStateDecorator = validationStampJenkinsJobStateDecorator;
        this.branchJenkinsJobStateDecorator = branchJenkinsJobStateDecorator;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.singletonList(jenkinsUrlPropertyDescriptor);
    }

    @Override
    public Collection<? extends EntityDecorator> getDecorators() {
        return Arrays.asList(
                validationStampJenkinsJobStateDecorator,
                branchJenkinsJobStateDecorator);
    }
}
