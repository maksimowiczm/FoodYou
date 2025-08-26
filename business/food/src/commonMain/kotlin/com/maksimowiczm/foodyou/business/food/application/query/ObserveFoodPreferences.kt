package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.FoodPreferences
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data object ObserveFoodPreferencesQuery : Query<FoodPreferences>

internal class ObserveFoodPreferencesQueryHandler(
    private val localFoodPreferences: LocalFoodPreferencesDataSource
) : QueryHandler<ObserveFoodPreferencesQuery, FoodPreferences> {

    override fun handle(query: ObserveFoodPreferencesQuery): Flow<FoodPreferences> =
        localFoodPreferences.observe()
}
