package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data object ObserveMealsQuery : Query<List<Meal>>

internal class ObserveMealsQueryHandler(private val localMealDataSource: LocalMealDataSource) :
    QueryHandler<ObserveMealsQuery, List<Meal>> {

    override fun handle(query: ObserveMealsQuery): Flow<List<Meal>> =
        localMealDataSource.observeAllMeals()
}
