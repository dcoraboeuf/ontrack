package net.ontrack.client;

import net.ontrack.core.ui.ManageUI;

public interface ManageUIClient extends Client, ManageUI {

    String getProjectURL (String project);

    String getBranchURL (String project, String branch);

}
