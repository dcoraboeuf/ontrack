package net.ontrack.extension.git.service;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.extension.git.model.GitConfiguration;

public interface GitConfigurator {

    GitConfiguration configure(GitConfiguration configuration, BranchSummary branch);

}
