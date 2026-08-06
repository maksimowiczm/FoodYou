package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.domain.search.SearchQueryRecognizer
import kotlinx.serialization.Serializable

/**
 * Search query parsed from a FoodData Central URL.
 *
 * @property url The FoodData Central product URL e.g.
 *   `https://fdc.nal.usda.gov/food-details/123456/nutrients`
 * @property fdcId The extracted FoodData Central ID e.g. `123456`
 */
@ConsistentCopyVisibility
@Serializable
data class FoodDataCentralUrlSearchQuery private constructor(val url: String, val fdcId: Int) :
    SearchQuery.NotBlank {
    override val query: String = url

    companion object {
        private val regex =
            "https://fdc\\.nal\\.usda\\.gov/food-details/(?<fdcId>\\d+)(/nutrients)?".toRegex()

        /** Recognizes FoodData Central product URLs and extracts the FDC ID. */
        val recognizer = SearchQueryRecognizer { query ->
            regex.find(query)?.let {
                val fdcId = it.groups["fdcId"]?.value?.toInt() ?: return@let null
                FoodDataCentralUrlSearchQuery(url = it.value, fdcId = fdcId)
            }
        }
    }
}
