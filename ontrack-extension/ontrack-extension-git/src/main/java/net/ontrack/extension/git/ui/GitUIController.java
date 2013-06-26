package net.ontrack.extension.git.ui;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.extension.git.model.*;
import net.ontrack.extension.git.service.GitService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public boolean isGitConfigured(int branchId) {
        return gitService.isGitConfigured(branchId);
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

    @Override
    @RequestMapping(value = "/changelog/{uuid}/commits", method = RequestMethod.GET)
    public
    @ResponseBody
    ChangeLogCommits getChangeLogCommits(Locale locale, @PathVariable String uuid) {
        // Gets the change log
        ChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        ChangeLogCommits commits = changeLog.getCommits();
        if (commits != null) {
            return commits;
        }
        // Loads the revisions
        commits = gitService.getChangeLogCommits(locale, changeLog.getSummary());
        // Stores in cache
        changeLog.setCommits(commits);
        // OK
        return commits;
    }

    @Override
    @RequestMapping(value = "/changelog/{uuid}/files", method = RequestMethod.GET)
    public
    @ResponseBody
    ChangeLogFiles getChangeLogFiles(Locale locale, @PathVariable String uuid) {
        // Gets the change log
        ChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        ChangeLogFiles files = changeLog.getFiles();
        if (files != null) {
            return files;
        }
        // Loads the files
        files = gitService.getChangeLogFiles(locale, changeLog.getSummary());
        // Stores in cache
        changeLog.setFiles(files);
        // OK
        return files;
    }

    private ChangeLog getChangeLog(String uuid) {
        ChangeLog changeLog = logCache.getIfPresent(uuid);
        if (changeLog != null) {
            return changeLog;
        } else {
            throw new ChangeLogUUIDException(uuid);
        }
    }
}
