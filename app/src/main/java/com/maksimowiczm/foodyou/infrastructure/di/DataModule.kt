package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.data.DateProvider
import com.maksimowiczm.foodyou.data.DateProviderImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { DateProviderImpl() }.bind<DateProvider>()
}
