package com.maksimowiczm.foodyou.business.settings.application.query

import com.maksimowiczm.foodyou.business.settings.domain.EnglishUS
import com.maksimowiczm.foodyou.business.settings.domain.Translation
import com.maksimowiczm.foodyou.business.settings.domain.languages
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.system.SystemDetails
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data object ObserveCurrentTranslationQuery : Query<Translation>

internal class ObserveCurrentTranslationQueryHandler(private val systemDetails: SystemDetails) :
    QueryHandler<ObserveCurrentTranslationQuery, Translation> {

    override fun handle(query: ObserveCurrentTranslationQuery): Flow<Translation> =
        systemDetails.languageTag.map { currentLanguage ->
            languages.firstOrNull { it.languageTag == currentLanguage } ?: EnglishUS
        }
}
