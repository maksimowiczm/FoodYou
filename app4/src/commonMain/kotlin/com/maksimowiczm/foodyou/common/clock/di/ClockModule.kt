package com.maksimowiczm.foodyou.common.clock.di

import kotlin.time.Clock
import org.koin.dsl.module

val clockModule = module { single { Clock.System } }
