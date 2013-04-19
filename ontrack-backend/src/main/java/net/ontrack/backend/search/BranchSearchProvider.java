package net.ontrack.backend.search;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.backend.dao.BranchDao;
import net.ontrack.backend.dao.ProjectDao;
import net.ontrack.backend.dao.model.TBranch;
import net.ontrack.backend.dao.model.TProject;
import net.ontrack.core.model.SearchResult;
import net.ontrack.service.GUIService;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.regex.Pattern;

@Component
public class BranchSearchProvider extends AbstractEntitySearchProvider {

    private final BranchDao branchDao;
    private final ProjectDao projectDao;

    @Autowired
    public BranchSearchProvider(GUIService guiService, BranchDao branchDao, ProjectDao projectDao) {
        super(guiService);
        this.branchDao = branchDao;
        this.projectDao = projectDao;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return Pattern.matches("[A-Za-z0-9_\\.\\-]+", token);
    }

    @Override
    public Collection<SearchResult> search(String name) {
        Collection<TBranch> branches = branchDao.findByName(name);
        return Collections2.transform(
                branches,
                new Function<TBranch, SearchResult>() {
                    @Override
                    public SearchResult apply(TBranch branch) {
                        TProject project = projectDao.getById(branch.getProject());
                        return new SearchResult(
                                String.format("%s/%s", project.getName(), branch.getName()),
                                new LocalizableMessage("search.branch", project.getName(), branch.getName()),
                                guiPath("project/%s/branch/%s", project.getName(), branch.getName())
                        );
                    }
                }
        );
    }
}
