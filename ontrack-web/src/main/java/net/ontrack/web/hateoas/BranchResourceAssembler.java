package net.ontrack.web.hateoas;

import net.ontrack.core.model.BranchSummary;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class BranchResourceAssembler extends ResourceAssemblerSupport<BranchSummary, BranchResource> {

    public BranchResourceAssembler() {
        super(BranchResourceController.class, BranchResource.class);
    }

    @Override
    public BranchResource toResource(BranchSummary entity) {
        return createResourceWithId(entity.getId(), entity);
    }

    @Override
    protected BranchResource instantiateResource(BranchSummary entity) {
        return new BranchResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }
}
