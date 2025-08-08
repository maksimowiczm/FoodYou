package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.settings.application.command.SetHomeCardOrderCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetLastRememberedVersionCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetNutrientsOrderCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetSecureScreenCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetTranslationCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetTranslationWarningCommandHandler
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
    factoryOf(::SetSecureScreenCommandHandler) { named("SetSecureScreenCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetNutrientsOrderCommandHandler) { named("SetNutrientsOrderCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetTranslationWarningCommandHandler) {
            named("SetTranslationWarningCommandHandler")
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetLastRememberedVersionCommandHandler) {
            named("SetLastRememberedVersionCommandHandler")
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetHomeCardOrderCommandHandler) { named("SetHomeCardOrderCommandHandler") }
        .bind<CommandHandler<*, *, *>>()

    factoryOf(::DataStoreSettingsDataSource).bind<LocalSettingsDataSource>()
}
