package net.ontrack.extension.git.ui;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.extension.git.model.ChangeLog;
import net.ontrack.extension.git.model.ChangeLogRequest;
import net.ontrack.extension.git.model.ChangeLogSummary;
import net.ontrack.extension.git.service.GitService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/ui/extension/git")
public class GitUIController extends AbstractUIController implements GitUI {

    private final ManageUI manageUI;
    private final GitService gitService;
    private final Cache<String, ChangeLog> logCache;

    @Autowired
    public GitUIController(ErrorHandler errorHandler, Strings strings, ManageUI manageUI, GitService gitService) {
        super(errorHandler, strings);
        this.manageUI = manageUI;
        this.gitService = gitService;
        // Caching
        logCache = CacheBuilder.newBuilder()
                .maximumSize(20)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build();
    }

    @Override
    @RequestMapping(value = "/changelog", method = RequestMethod.POST)
    public
    @ResponseBody
    ChangeLogSummary getChangeLogSummary(Locale locale, @RequestBody ChangeLogRequest request) {
        // Build information
        BuildSummary buildFrom = manageUI.getBuild(request.getProject(), request.getBranch(), request.getFrom());
        BuildSummary buildTo = manageUI.getBuild(request.getProject(), request.getBranch(), request.getTo());
        // Loads the change log summary
        ChangeLogSummary summary = gitService.getChangeLogSummary(locale, buildFrom.getBranch().getId(), buildFrom.getId(), buildTo.getId());
        // Stores it into the cache
        logCache.put(summary.getUuid(), new ChangeLog(summary));
        // OK
        return summary;
    }
}
