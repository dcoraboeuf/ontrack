package net.ontrack.backend.search;

import net.ontrack.backend.dao.ProjectDao;
import net.ontrack.backend.dao.model.TProject;
import net.ontrack.core.model.SearchResult;
import net.ontrack.service.GUIService;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

@Component
public class ProjectSearchProvider extends AbstractEntitySearchProvider {

    private final ProjectDao projectDao;

    @Autowired
    public ProjectSearchProvider(GUIService guiService, ProjectDao projectDao) {
        super(guiService);
        this.projectDao = projectDao;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return Pattern.matches("[A-Za-z0-9_\\.\\-]+", token);
    }

    @Override
    public Collection<SearchResult> search(String token) {
        TProject p = projectDao.findByName(token);
        if (p != null) {
            return Collections.singleton(
                    new SearchResult(
                            p.getName(),
                            new LocalizableMessage("search.project", p.getName()),
                            guiPath("project/%s", p.getName())
                    )
            );
        } else {
            return Collections.emptySet();
        }
    }
}
