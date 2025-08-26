package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.WeeklyGoals
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalGoalsDataSource
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data object ObserveWeeklyGoalsQuery : Query<WeeklyGoals>

internal class ObserveWeeklyGoalsQueryHandler(private val localGoals: LocalGoalsDataSource) :
    QueryHandler<ObserveWeeklyGoalsQuery, WeeklyGoals> {

    override fun handle(query: ObserveWeeklyGoalsQuery): Flow<WeeklyGoals> =
        localGoals.observeWeeklyGoals()
}
