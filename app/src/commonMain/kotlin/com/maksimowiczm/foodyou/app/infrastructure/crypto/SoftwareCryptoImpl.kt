package com.maksimowiczm.foodyou.app.infrastructure.crypto

import com.maksimowiczm.foodyou.common.domain.crypto.SoftwareCrypto
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

expect class SoftwareCryptoImpl : SoftwareCrypto {
    override fun encrypt(data: ByteArray): ByteArray

    override fun decrypt(data: ByteArray): ByteArray
}

expect fun Module.softwareCryptoImpl(): KoinDefinition<out SoftwareCryptoImpl>
