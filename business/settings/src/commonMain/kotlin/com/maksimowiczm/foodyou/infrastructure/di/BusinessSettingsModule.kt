package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.settings.application.command.SetTranslationCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.UpdateSettingsCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveCurrentTranslationQueryHandler
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveSettingsQueryHandler
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveTranslationsQueryHandler
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.datastore.DataStoreSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.dsl.bind
import org.koin.dsl.module

val businessSettingsModule = module {
    factoryOf(::UpdateSettingsCommandHandler) { named("UpdateSettingsCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::ObserveSettingsQueryHandler) { named("ObserveSettingsQueryHandler") }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveTranslationsQueryHandler) { named("ObserveTranslationsQueryHandler") }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveCurrentTranslationQueryHandler) {
            named("ObserveCurrentTranslationQueryHandler")
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::SetTranslationCommandHandler) { named("SetTranslationCommandHandler") }
        .bind<CommandHandler<*, *, *>>()

    factoryOf(::DataStoreSettingsDataSource).bind<LocalSettingsDataSource>()
}
