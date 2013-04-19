package net.ontrack.service;

import net.ontrack.core.model.SearchResult;

import java.util.Collection;

public interface SearchProvider {

    boolean isTokenSearchable(String token);

    Collection<SearchResult> search(String token);
}
