package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.settings.application.command.PartialSettingsUpdateCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetTranslationCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveCurrentTranslationQueryHandler
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveSettingsQueryHandler
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveTranslationsQueryHandler
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.datastore.DataStoreSettingsDataSource
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val businessSettingsModule = module {
    queryHandlerOf(::ObserveSettingsQueryHandler)
    queryHandlerOf(::ObserveTranslationsQueryHandler)
    queryHandlerOf(::ObserveCurrentTranslationQueryHandler)

    commandHandlerOf(::SetTranslationCommandHandler)
    commandHandlerOf(::PartialSettingsUpdateCommandHandler)

    factoryOf(::DataStoreSettingsDataSource).bind<LocalSettingsDataSource>()
}
