package net.ontrack.extension.issue.support;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import net.ontrack.extension.issue.IssueService;
import net.ontrack.extension.issue.IssueServiceFactory;
import net.ontrack.extension.issue.IssueServiceNotFoundException;
import net.ontrack.extension.issue.IssueServiceSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class IssueServiceFactoryImpl implements IssueServiceFactory {

    private Map<String, IssueService> services;

    @Autowired(required = false)
    public void setServices(Collection<IssueService> services) {
        this.services = Maps.uniqueIndex(
                services,
                new Function<IssueService, String>() {
                    @Override
                    public String apply(IssueService service) {
                        return service.getId();
                    }
                }
        );
    }

    @Override
    public IssueService getServiceByName(String name) {
        Optional<IssueService> option = getOptionalServiceByName(name);
        if (option.isPresent()) {
            return option.get();
        } else {
            throw new IssueServiceNotFoundException(name);
        }
    }

    @Override
    public Optional<IssueService> getOptionalServiceByName(String name) {
        IssueService service = services.get(name);
        if (service != null) {
            return Optional.of(service);
        } else {
            return Optional.absent();
        }
    }

    @Override
    public Collection<IssueServiceSummary> getAllServices() {
        return Collections2.transform(
                services.values(),
                IssueService.summaryFn
        );
    }

}
