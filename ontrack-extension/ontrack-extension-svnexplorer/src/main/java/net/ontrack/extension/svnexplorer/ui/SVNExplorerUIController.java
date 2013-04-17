package net.ontrack.extension.svnexplorer.ui;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.extension.svnexplorer.model.*;
import net.ontrack.extension.svnexplorer.service.SVNExplorerService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/ui/extension/svnexplorer")
public class SVNExplorerUIController extends AbstractUIController implements SVNExplorerUI {

    private final ManageUI manageUI;
    private final SVNExplorerService svnExplorerService;
    private final Cache<String, ChangeLog> logCache;

    @Autowired
    public SVNExplorerUIController(ErrorHandler errorHandler, Strings strings, ManageUI manageUI, SVNExplorerService svnExplorerService) {
        super(errorHandler, strings);
        this.manageUI = manageUI;
        this.svnExplorerService = svnExplorerService;
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
    ChangeLogSummary getChangeLogSummary(@RequestBody ChangeLogRequest request) {
        // Build information
        BuildSummary buildFrom = manageUI.getBuild(request.getProject(), request.getBranch(), request.getFrom());
        BuildSummary buildTo = manageUI.getBuild(request.getProject(), request.getBranch(), request.getTo());
        // Loads the change log summary
        ChangeLogSummary summary = svnExplorerService.getChangeLogSummary(buildFrom.getBranch().getId(), buildFrom.getId(), buildTo.getId());
        // Stores it into the cache
        logCache.put(summary.getUuid(), new ChangeLog(summary));
        // OK
        return summary;
    }

    @Override
    @RequestMapping(value = "/changelog/{uuid}/revisions", method = RequestMethod.GET)
    public
    @ResponseBody
    ChangeLogRevisions getChangeLogRevisions(@PathVariable String uuid) {
        // Gets the change log
        ChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        ChangeLogRevisions revisions = changeLog.getRevisions();
        if (revisions != null) {
            return revisions;
        }
        // Loads the revisions
        revisions = svnExplorerService.getChangeLogRevisions(changeLog.getSummary());
        // Stores in cache
        changeLog.setRevisions(revisions);
        // OK
        return revisions;
    }

    @Override
    @RequestMapping(value = "/changelog/{uuid}/issues", method = RequestMethod.GET)
    public
    @ResponseBody
    ChangeLogIssues getChangeLogIssues(@PathVariable String uuid) {
        // Gets the change log
        ChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        ChangeLogIssues issues = changeLog.getIssues();
        if (issues != null) {
            return issues;
        }
        // Makes sure the revisions are loaded
        ChangeLogRevisions revisions = loadChangeLogRevisions(uuid, changeLog);
        // Loads the issues
        issues = svnExplorerService.getChangeLogIssues(changeLog.getSummary(), revisions);
        // Stores in cache
        changeLog.setIssues(issues);
        // OK
        return issues;
    }

    @Override
    @RequestMapping(value = "/changelog/{uuid}/files", method = RequestMethod.GET)
    public
    @ResponseBody
    ChangeLogFiles getChangeLogFiles(@PathVariable String uuid) {
        // Gets the change log
        ChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        ChangeLogFiles files = changeLog.getFiles();
        if (files != null) {
            return files;
        }
        // Makes sure the revisions are loaded
        ChangeLogRevisions revisions = loadChangeLogRevisions(uuid, changeLog);
        // Loads the files
        files = svnExplorerService.getChangeLogFiles(changeLog.getSummary(), revisions);
        // Stores in cache
        changeLog.setFiles(files);
        // OK
        return files;
    }

    private ChangeLogRevisions loadChangeLogRevisions(String uuid, ChangeLog changeLog) {
        // Makes sure the revisions are loaded
        ChangeLogRevisions revisions = changeLog.getRevisions();
        if (revisions == null) {
            revisions = getChangeLogRevisions(uuid);
        }
        return revisions;
    }

    @Override
    @RequestMapping(value = "/changelog/{uuid}/info", method = RequestMethod.GET)
    public
    @ResponseBody
    ChangeLogInfo getChangeLogInfo(@PathVariable String uuid) {
        // Gets the change log
        ChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        ChangeLogInfo info = changeLog.getInfo();
        if (info != null) {
            return info;
        }
        // Makes sure the issues are loaded
        ChangeLogIssues issues = loadChangeLogIssues(uuid, changeLog);
        // Makes sure the files are loaded
        ChangeLogFiles files = loadChangeLogFiles(uuid, changeLog);
        // Loads the info
        info = svnExplorerService.getChangeLogInfo(changeLog.getSummary(), issues, files);
        // Stores in cache
        changeLog.setInfo(info);
        // OK
        return info;
    }

    private ChangeLogIssues loadChangeLogIssues(String uuid, ChangeLog changeLog) {
        ChangeLogIssues issues = changeLog.getIssues();
        if (issues == null) {
            issues = getChangeLogIssues(uuid);
        }
        return issues;
    }

    private ChangeLogFiles loadChangeLogFiles(String uuid, ChangeLog changeLog) {
        ChangeLogFiles files = changeLog.getFiles();
        if (files == null) {
            files = getChangeLogFiles(uuid);
        }
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
