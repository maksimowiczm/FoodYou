package com.maksimowiczm.foodyou.common.infrastructure.crypto

import org.koin.core.module.Module

internal expect fun Module.masterCryptoDefinition()

fun Module.cryptoModule() {
    masterCryptoDefinition()
}
