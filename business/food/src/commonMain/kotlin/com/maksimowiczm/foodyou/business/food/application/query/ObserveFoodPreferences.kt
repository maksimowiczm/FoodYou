package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.FoodPreferences
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

data object ObserveFoodPreferencesQuery : Query

internal class ObserveFoodPreferencesQueryHandler(
    private val localFoodPreferences: LocalFoodPreferencesDataSource
) : QueryHandler<ObserveFoodPreferencesQuery, FoodPreferences> {
    override val queryType: KClass<ObserveFoodPreferencesQuery>
        get() = ObserveFoodPreferencesQuery::class

    override fun handle(query: ObserveFoodPreferencesQuery): Flow<FoodPreferences> =
        localFoodPreferences.observe()
}
