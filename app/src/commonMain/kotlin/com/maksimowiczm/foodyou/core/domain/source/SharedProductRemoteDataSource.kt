package com.maksimowiczm.foodyou.core.domain.source

interface SharedProductRemoteDataSource {

    /**
     * Extracts a product URL from the given text.
     *
     * @return The extracted URL, or null if no URL was found.
     */
    fun extractUrl(text: String): String?
}
