package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalMealsPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

data object ObserveMealsPreferencesQuery : Query

internal class ObserveMealsPreferencesQueryHandler(
    private val localMealsPreferences: LocalMealsPreferencesDataSource
) : QueryHandler<ObserveMealsPreferencesQuery, MealsPreferences> {
    override val queryType: KClass<ObserveMealsPreferencesQuery>
        get() = ObserveMealsPreferencesQuery::class

    override fun handle(query: ObserveMealsPreferencesQuery): Flow<MealsPreferences> =
        localMealsPreferences.observe()
}
