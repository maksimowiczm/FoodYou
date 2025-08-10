package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.settings.application.command.SetExpandGoalCardCommand
import com.maksimowiczm.foodyou.business.settings.application.command.SetExpandGoalCardCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetHomeCardOrderCommand
import com.maksimowiczm.foodyou.business.settings.application.command.SetHomeCardOrderCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetLastRememberedVersionCommand
import com.maksimowiczm.foodyou.business.settings.application.command.SetLastRememberedVersionCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetNutrientsOrderCommand
import com.maksimowiczm.foodyou.business.settings.application.command.SetNutrientsOrderCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetSecureScreenCommand
import com.maksimowiczm.foodyou.business.settings.application.command.SetSecureScreenCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetTranslationCommand
import com.maksimowiczm.foodyou.business.settings.application.command.SetTranslationCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.command.SetTranslationWarningCommand
import com.maksimowiczm.foodyou.business.settings.application.command.SetTranslationWarningCommandHandler
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveCurrentTranslationQuery
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveCurrentTranslationQueryHandler
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveSettingsQuery
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveSettingsQueryHandler
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveTranslationsQuery
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
    factoryOf(::ObserveSettingsQueryHandler) { named(ObserveSettingsQuery::class.qualifiedName!!) }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveTranslationsQueryHandler) {
            named(ObserveTranslationsQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveCurrentTranslationQueryHandler) {
            named(ObserveCurrentTranslationQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::SetTranslationCommandHandler) {
            named(SetTranslationCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetSecureScreenCommandHandler) {
            named(SetSecureScreenCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetNutrientsOrderCommandHandler) {
            named(SetNutrientsOrderCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetTranslationWarningCommandHandler) {
            named(SetTranslationWarningCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetLastRememberedVersionCommandHandler) {
            named(SetLastRememberedVersionCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetHomeCardOrderCommandHandler) {
            named(SetHomeCardOrderCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SetExpandGoalCardCommandHandler) {
            named(SetExpandGoalCardCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()

    factoryOf(::DataStoreSettingsDataSource).bind<LocalSettingsDataSource>()
}
