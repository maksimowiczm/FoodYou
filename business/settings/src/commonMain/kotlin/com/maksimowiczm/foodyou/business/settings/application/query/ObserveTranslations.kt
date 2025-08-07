package com.maksimowiczm.foodyou.business.settings.application.query

import com.maksimowiczm.foodyou.business.settings.domain.Translation
import com.maksimowiczm.foodyou.business.settings.domain.languages
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data object ObserveTranslationsQuery : Query

internal class ObserveTranslationsQueryHandler :
    QueryHandler<ObserveTranslationsQuery, List<Translation>> {
    override val queryType: KClass<ObserveTranslationsQuery>
        get() = ObserveTranslationsQuery::class

    override fun handle(query: ObserveTranslationsQuery): Flow<List<Translation>> =
        flowOf(languages)
}
