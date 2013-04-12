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
        // Loads the revisions
        ChangeLogRevisions revisions = svnExplorerService.getChangeLogRevisions(changeLog.getSummary());
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
        // Makes sure the revisions are loaded
        ChangeLogRevisions revisions = changeLog.getRevisions();
        if (revisions == null) {
            revisions = getChangeLogRevisions(uuid);
        }
        // Loads the issues
        ChangeLogIssues issues = svnExplorerService.getChangeLogIssues(changeLog.getSummary(), revisions);
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
        // Makes sure the revisions are loaded
        ChangeLogRevisions revisions = changeLog.getRevisions();
        if (revisions == null) {
            revisions = getChangeLogRevisions(uuid);
        }
        // Loads the files
        ChangeLogFiles files = svnExplorerService.getChangeLogFiles(changeLog.getSummary(), revisions);
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
