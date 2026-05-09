package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.domain.search.SearchQueryRecognizer
import kotlinx.serialization.Serializable

/**
 * Search query parsed from an Open Food Facts product URL.
 *
 * @property url The Open Food Facts product URL e.g.
 *   `https://world.openfoodfacts.org/product/5449000000996/coca-cola`
 * @property barcode The extracted product barcode e.g. `5449000000996`
 */
@ConsistentCopyVisibility
@Serializable
data class OpenFoodFactsUrlSearchQuery private constructor(val url: String, val barcode: String) :
    SearchQuery.NotBlank {
    override val query: String = url

    companion object {
        private val regex =
            "https://\\w+\\.openfoodfacts\\.org/product/(?<barcode>\\d+)(?:/[^/]*)?".toRegex()

        /** Recognizes Open Food Facts product URLs and extracts the barcode. */
        val recognizer = SearchQueryRecognizer { query ->
            regex.find(query)?.let {
                val barcode = it.groups["barcode"]?.value ?: return@SearchQueryRecognizer null
                OpenFoodFactsUrlSearchQuery(url = it.value, barcode = barcode)
            }
        }
    }
}
