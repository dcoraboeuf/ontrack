package net.ontrack.core.model;

import lombok.Data;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Promotion;

import java.util.List;

@Data
public class BranchPromotions {

    private final BranchSummary branch;
    private final List<Promotion> promotions;

}
