package net.ontrack.service;

import net.ontrack.core.model.SearchResult;

import java.util.Collection;

public interface SearchService {

    Collection<SearchResult> search (String token);

}
