package com.maksimowiczm.foodyou.account.di

import com.maksimowiczm.foodyou.account.application.RemoveDeletedFoodFromFavoritesHandler
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.infrastructure.AccountRepositoryImpl
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountDatabase
import com.maksimowiczm.foodyou.common.event.di.integrationEventHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val accountModule = module {
    factoryOf(::AccountRepositoryImpl).bind<AccountRepository>()

    factory { get<AccountDatabase>().accountDao }

    integrationEventHandler { RemoveDeletedFoodFromFavoritesHandler(get(), get()) }
}
