package com.example.anchor;


import java.util.ArrayList;
import java.util.List;


// SearchController - Manages search operations and maintains search state

public class SearchController {

    // Properties
    public SearchIndex searchIndex;
    public SearchQuery lastQuery;
    public List<Note> results;


    // Constructor
    public SearchController() {
        this.results = new ArrayList<>();
    }


    // Constructor with SearchIndex

    public SearchController(SearchIndex searchIndex) {
        this.searchIndex = searchIndex;
        this.results = new ArrayList<>();
    }

    // Perform a search with the given query

    public List<Note> search(SearchQuery q) {
        this.lastQuery = q;

        if (searchIndex != null) {
            results = searchIndex.performSearch(q);
        } else {
            results = new ArrayList<>();
        }

        return results;
    }

    // Perform a quick text search

    public List<Note> searchByText(String searchText) {
        SearchQuery query = new SearchQuery().withText(searchText);
        return search(query);
    }

    // Clear the search results and query
    public void clear() {
        this.lastQuery = null;

        if (results != null) {
            results.clear();
        }
    }

    // Get the current search results without re-searching

    public List<Note> getResults() {
        return results != null ? results : new ArrayList<>();
    }

    // Get the count of current results
    public int getResultCount() {
        return results != null ? results.size() : 0;
    }

    // Check if there are any results

    public boolean hasResults() {
        return results != null && !results.isEmpty();
    }

    //  Check if a query is currently active
    public boolean hasActiveQuery() {
        return lastQuery != null && !lastQuery.isEmpty();
    }

    // Re-run the last search (useful after data changes)

    public List<Note> refreshResults() {
        if (lastQuery != null) {
            return search(lastQuery);
        }
        return new ArrayList<>();
    }
}