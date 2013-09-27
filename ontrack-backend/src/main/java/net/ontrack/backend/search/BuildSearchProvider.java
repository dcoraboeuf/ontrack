package net.ontrack.backend.search;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.backend.dao.BranchDao;
import net.ontrack.backend.dao.BuildDao;
import net.ontrack.backend.dao.ProjectDao;
import net.ontrack.backend.dao.model.TBranch;
import net.ontrack.backend.dao.model.TBuild;
import net.ontrack.backend.dao.model.TProject;
import net.ontrack.core.model.SearchResult;
import net.ontrack.service.GUIService;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.regex.Pattern;

@Component
public class BuildSearchProvider extends AbstractEntitySearchProvider {

    private final BuildDao buildDao;
    private final BranchDao branchDao;
    private final ProjectDao projectDao;

    @Autowired
    public BuildSearchProvider(GUIService guiService, BuildDao buildDao, BranchDao branchDao, ProjectDao projectDao) {
        super(guiService);
        this.buildDao = buildDao;
        this.branchDao = branchDao;
        this.projectDao = projectDao;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return Pattern.matches("[A-Za-z0-9_\\.\\-]+", token);
    }

    @Override
    public Collection<SearchResult> search(String name) {
        Collection<TBuild> builds = buildDao.findByName(name);
        return Collections2.transform(
                builds,
                new Function<TBuild, SearchResult>() {
                    @Override
                    public SearchResult apply(TBuild build) {
                        TBranch branch = branchDao.getById(build.getBranch());
                        TProject project = projectDao.getById(branch.getProject());
                        return new SearchResult(
                                String.format("%s/%s/%s", project.getName(), branch.getName(), build.getName()),
                                new LocalizableMessage("search.build", project.getName(), branch.getName(), build.getName()),
                                guiPath("project/%s/branch/%s/build/%s", project.getName(), branch.getName(), build.getName()),
                                90
                        );
                    }
                }
        );
    }
}
