package net.ontrack.extension.git.ui;

import net.ontrack.extension.git.model.*;

import java.util.Locale;

public interface GitUI {

    ChangeLogSummary getChangeLogSummary(Locale locale, ChangeLogRequest request);

    ChangeLog getChangeLog(String uuid);

    ChangeLogCommits getChangeLogCommits(Locale locale, String uuid);

    ChangeLogFiles getChangeLogFiles(Locale locale, String uuid);

    boolean isGitConfigured(int branchId);
}
