package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalGoalsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class ObserveDailyGoalsQuery(val date: LocalDate) : Query

internal class ObserveDailyGoalsQueryHandler(private val localGoals: LocalGoalsDataSource) :
    QueryHandler<ObserveDailyGoalsQuery, DailyGoal> {
    override val queryType: KClass<ObserveDailyGoalsQuery>
        get() = ObserveDailyGoalsQuery::class

    override fun handle(query: ObserveDailyGoalsQuery): Flow<DailyGoal> =
        localGoals.observeWeeklyGoals().map {
            when (query.date.dayOfWeek) {
                DayOfWeek.MONDAY -> it.monday
                DayOfWeek.TUESDAY -> it.tuesday
                DayOfWeek.WEDNESDAY -> it.wednesday
                DayOfWeek.THURSDAY -> it.thursday
                DayOfWeek.FRIDAY -> it.friday
                DayOfWeek.SATURDAY -> it.saturday
                DayOfWeek.SUNDAY -> it.sunday
            }
        }
}
