package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MealsProvider
import com.maksimowiczm.foodyou.infrastructure.ComposeMealsProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val localDatabaseModule = module {
    factoryOf(::ComposeMealsProvider).bind<MealsProvider>()
}