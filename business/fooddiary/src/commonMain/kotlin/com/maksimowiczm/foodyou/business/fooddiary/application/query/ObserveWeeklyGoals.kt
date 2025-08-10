package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.WeeklyGoals
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalGoalsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data object ObserveWeeklyGoalsQuery : Query<WeeklyGoals>

internal class ObserveWeeklyGoalsQueryHandler(private val localGoals: LocalGoalsDataSource) :
    QueryHandler<ObserveWeeklyGoalsQuery, WeeklyGoals> {

    override fun handle(query: ObserveWeeklyGoalsQuery): Flow<WeeklyGoals> =
        localGoals.observeWeeklyGoals()
}
