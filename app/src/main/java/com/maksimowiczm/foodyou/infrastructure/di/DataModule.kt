package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.data.DateProvider
import com.maksimowiczm.foodyou.data.DateProviderImpl
import com.maksimowiczm.foodyou.data.LinkHandler
import com.maksimowiczm.foodyou.data.linkHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { DateProviderImpl() }.bind<DateProvider>()
    factory {
        androidContext().linkHandler
    }.bind<LinkHandler>()
}
