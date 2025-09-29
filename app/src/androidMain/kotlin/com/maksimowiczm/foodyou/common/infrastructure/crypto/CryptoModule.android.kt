package com.maksimowiczm.foodyou.common.infrastructure.crypto

import com.maksimowiczm.foodyou.common.crypto.MasterCrypto
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

actual fun Module.masterCryptoDefinition() {
    singleOf(::AndroidMasterCrypto).bind<MasterCrypto>()
}
