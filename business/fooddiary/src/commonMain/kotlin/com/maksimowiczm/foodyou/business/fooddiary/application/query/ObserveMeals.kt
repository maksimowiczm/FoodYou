package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

data object ObserveMealsQuery : Query

internal class ObserveMealsQueryHandler(private val localMealDataSource: LocalMealDataSource) :
    QueryHandler<ObserveMealsQuery, List<Meal>> {
    override val queryType: KClass<ObserveMealsQuery>
        get() = ObserveMealsQuery::class

    override fun handle(query: ObserveMealsQuery): Flow<List<Meal>> =
        localMealDataSource.observeAllMeals()
}
