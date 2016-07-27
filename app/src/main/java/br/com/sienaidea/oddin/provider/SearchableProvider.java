package br.com.sienaidea.oddin.provider;

import android.content.SearchRecentSuggestionsProvider;

public class SearchableProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "br.com.sienaidea.oddin.provider.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}