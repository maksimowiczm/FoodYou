package com.maksimowiczm.foodyou.userproduct.di

import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val userProductModule = module { factoryOf(::UserProductService) }
