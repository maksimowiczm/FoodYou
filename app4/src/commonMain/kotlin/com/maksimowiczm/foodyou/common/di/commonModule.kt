package com.maksimowiczm.foodyou.common.di

import com.maksimowiczm.foodyou.common.infrastructure.systemDetails
import org.koin.dsl.module

val commonModule = module { systemDetails() }
