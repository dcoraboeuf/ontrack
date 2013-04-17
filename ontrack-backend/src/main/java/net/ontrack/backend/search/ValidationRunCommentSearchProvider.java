package net.ontrack.backend.search;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.backend.dao.*;
import net.ontrack.backend.dao.model.*;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.SearchResult;
import net.ontrack.service.GUIService;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.NonLocalizable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ValidationRunCommentSearchProvider extends AbstractEntitySearchProvider {

    private final CommentDao commentDao;
    private final ProjectDao projectDao;
    private final BranchDao branchDao;
    private final BuildDao buildDao;
    private final ValidationStampDao validationStampDao;
    private final ValidationRunDao validationRunDao;
    private final ValidationRunStatusDao validationRunStatusDao;

    @Autowired
    public ValidationRunCommentSearchProvider(GUIService guiService, CommentDao commentDao, ProjectDao projectDao, BranchDao branchDao, BuildDao buildDao, ValidationStampDao validationStampDao, ValidationRunDao validationRunDao, ValidationRunStatusDao validationRunStatusDao) {
        super(guiService);
        this.commentDao = commentDao;
        this.projectDao = projectDao;
        this.branchDao = branchDao;
        this.buildDao = buildDao;
        this.validationStampDao = validationStampDao;
        this.validationRunDao = validationRunDao;
        this.validationRunStatusDao = validationRunStatusDao;
    }

    /**
     * Accepts any text
     */
    @Override
    public boolean isTokenSearchable(String token) {
        return true;
    }

    @Override
    public Collection<SearchResult> search(String token) {
        Collection<SearchResult> results = new ArrayList<>();
        results.addAll(searchOnComments(token));
        // FIXME results.addAll(searchOnStatus(token));
        return results;
    }

    private Collection<? extends SearchResult> searchOnComments(String token) {
        Collection<TComment> comments = commentDao.findByEntityAndText(Entity.VALIDATION_RUN, token);
        return Collections2.transform(
                comments,
                new Function<TComment, SearchResult>() {
                    @Override
                    public SearchResult apply(TComment t) {
                        int validationRunId = t.getEntities().get(Entity.VALIDATION_RUN);
                        TValidationRun validationRun = validationRunDao.getById(validationRunId);
                        TValidationStamp validationStamp = validationStampDao.getById(validationRun.getValidationStamp());
                        TBuild build = buildDao.getById(validationRun.getBuild());
                        TBranch branch = branchDao.getById(build.getBranch());
                        TProject project = projectDao.getById(branch.getProject());
                        return new SearchResult(
                                new LocalizableMessage(
                                        "search.comment",
                                        String.format(
                                                "%s/%s/%s/%s/%s",
                                                project.getName(),
                                                branch.getName(),
                                                build.getName(),
                                                validationStamp.getName(),
                                                validationRun.getRunOrder())),
                                new NonLocalizable(t.getContent()),
                                guiPath(
                                        "project/%s/branch/%s/build/%s/validation_stamp/%s/validation_run/%s",
                                        project.getName(),
                                        branch.getName(),
                                        build.getName(),
                                        validationStamp.getName(),
                                        validationRun.getRunOrder()
                                )
                        );
                    }
                }
        );
    }
}
