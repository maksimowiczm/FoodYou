package com.maksimowiczm.foodyou.account

import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.application.RemoveDeletedFoodFromFavoritesHandler
import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal fun Module.account() {
    factoryOf(::AccountService)
    eventHandlerOf(::RemoveDeletedFoodFromFavoritesHandler)
}
