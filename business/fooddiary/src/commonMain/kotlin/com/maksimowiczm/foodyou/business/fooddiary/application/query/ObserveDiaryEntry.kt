package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data class ObserveDiaryEntryQuery(val id: Long) : Query<DiaryEntry?>

internal class ObserveDiaryEntryQueryHandler(
    private val localDiaryEntry: LocalDiaryEntryDataSource
) : QueryHandler<ObserveDiaryEntryQuery, DiaryEntry?> {
    override fun handle(query: ObserveDiaryEntryQuery): Flow<DiaryEntry?> =
        localDiaryEntry.observeEntry(query.id)
}
