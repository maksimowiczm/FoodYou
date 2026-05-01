package com.maksimowiczm.foodyou.account.di

import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.application.RemoveDeletedFoodFromFavoritesHandler
import com.maksimowiczm.foodyou.common.event.di.integrationEventHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val accountModule = module {
    factoryOf(::AccountService)

    integrationEventHandler { RemoveDeletedFoodFromFavoritesHandler(get()) }
}
