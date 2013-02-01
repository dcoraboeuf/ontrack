package net.ontrack.service;

import net.ontrack.core.model.BuildCreationForm;
import net.ontrack.core.model.BuildSummary;

public interface ControlService {

	BuildSummary createBuild(int branch, BuildCreationForm form);

}
