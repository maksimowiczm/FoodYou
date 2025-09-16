package com.maksimowiczm.foodyou.app.ui.database.opensource.externaldatabases

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.app.business.shared.domain.search.FoodSearchPreferences

@Immutable
internal data class FoodPreferencesModel(
    val useOpenFoodFacts: Boolean? = null,
    val useUsda: Boolean? = null,
) {
    constructor(
        domain: FoodSearchPreferences
    ) : this(useOpenFoodFacts = domain.isOpenFoodFactsEnabled, useUsda = domain.isUsdaEnabled)
}
