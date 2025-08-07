package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.system.SystemDetails
import com.maksimowiczm.foodyou.shared.common.infrastructure.system.AndroidSystemDetails
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

actual val systemDetails: Module.() -> Unit = {
    singleOf(::AndroidSystemDetails).bind<SystemDetails>()
}
