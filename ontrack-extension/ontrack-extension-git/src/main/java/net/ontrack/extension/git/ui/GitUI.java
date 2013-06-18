package net.ontrack.extension.git.ui;

import net.ontrack.extension.git.model.ChangeLogCommits;
import net.ontrack.extension.git.model.ChangeLogRequest;
import net.ontrack.extension.git.model.ChangeLogSummary;

import java.util.Locale;

public interface GitUI {

    ChangeLogSummary getChangeLogSummary(Locale locale, ChangeLogRequest request);

    ChangeLogCommits getChangeLogCommits(String uuid);

}
