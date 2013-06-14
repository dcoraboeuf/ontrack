package net.ontrack.backend;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.SearchResult;
import net.ontrack.service.SearchProvider;
import net.ontrack.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class DefaultSearchService implements SearchService {

    private final List<SearchProvider> searchProviders;

    @Autowired
    public DefaultSearchService(List<SearchProvider> searchProviders) {
        this.searchProviders = searchProviders;
    }

    @Override
    public Collection<SearchResult> search(final String token) {
        // List of eligible search providers
        Collection<SearchProvider> providers = Collections2.filter(
                searchProviders,
                new Predicate<SearchProvider>() {
                    @Override
                    public boolean apply(SearchProvider provider) {
                        return provider.isTokenSearchable(token);
                    }
                }
        );
        // TODO Fork/join for the search
        // Global result
        Collection<SearchResult> results = new HashSet<>();
        // For each selected provider
        for (SearchProvider provider : providers) {
            Collection<SearchResult> list = provider.search(token);
            if (list != null) {
                results.addAll(list);
            }
        }
        // OK
        return results;
    }

}
