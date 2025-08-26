package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data class ObserveMealQuery(val mealId: Long) : Query<Meal?>

internal class ObserveMealQueryHandler(private val localMeals: LocalMealDataSource) :
    QueryHandler<ObserveMealQuery, Meal?> {

    override fun handle(query: ObserveMealQuery): Flow<Meal?> =
        localMeals.observeMealById(query.mealId)
}
