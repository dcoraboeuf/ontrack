package net.ontrack.extension.git.service;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.support.MessageAnnotator;

public interface GitMessageAnnotator {

    MessageAnnotator annotator(BranchSummary branch);

}
