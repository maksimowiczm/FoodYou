package com.maksimowiczm.foodyou.common.domain.search

/** Parser for converting raw search input into typed SearchQuery instances. */
class SearchQueryParser(private val recognizers: List<SearchQueryRecognizer>) {

    constructor(vararg recognizer: SearchQueryRecognizer) : this(recognizer.asList())

    /**
     * Parses a raw query string into a typed SearchQuery.
     *
     * The input is trimmed before parsing to handle leading/trailing whitespace.
     *
     * @param query The raw search input from the user
     * @return A typed SearchQuery instance based on the detected format
     */
    fun parse(query: String?): SearchQuery {
        if (query.isNullOrBlank()) return SearchQuery.Blank
        val trimmed = query.trim()
        return recognizers.firstNotNullOfOrNull { it.recognize(trimmed) } ?: fallbackParse(trimmed)
    }

    private fun fallbackParse(query: String?) =
        when {
            query.isNullOrBlank() -> SearchQuery.Blank
            else -> SearchQuery.Text(query)
        }
}
