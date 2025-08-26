package com.maksimowiczm.foodyou.business.settings.application.query

import com.maksimowiczm.foodyou.business.settings.domain.Translation
import com.maksimowiczm.foodyou.business.settings.domain.languages
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data object ObserveTranslationsQuery : Query<List<Translation>>

internal class ObserveTranslationsQueryHandler :
    QueryHandler<ObserveTranslationsQuery, List<Translation>> {
    override fun handle(query: ObserveTranslationsQuery): Flow<List<Translation>> =
        flowOf(languages)
}
