package com.maksimowiczm.foodyou.account.di

import com.maksimowiczm.foodyou.account.application.UpdateSettingsCommandHandler
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.infrastructure.AccountManagerImpl
import com.maksimowiczm.foodyou.account.infrastructure.AccountRepositoryImpl
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountDatabase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val accountModule = module {
    factoryOf(::UpdateSettingsCommandHandler)
    factoryOf(::AccountRepositoryImpl).bind<AccountRepository>()

    factory { get<AccountDatabase>().accountDao }

    single { AccountManagerImpl() }.bind<AccountManager>()
}
