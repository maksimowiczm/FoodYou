package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

data class ObserveMealQuery(val mealId: Long) : Query

internal class ObserveMealQueryHandler(private val localMeals: LocalMealDataSource) :
    QueryHandler<ObserveMealQuery, Meal?> {
    override val queryType: KClass<ObserveMealQuery>
        get() = ObserveMealQuery::class

    override fun handle(query: ObserveMealQuery): Flow<Meal?> =
        localMeals.observeMealById(query.mealId)
}
