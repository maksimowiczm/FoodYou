package com.maksimowiczm.foodyou.common.infrastructure.crypto

import com.maksimowiczm.foodyou.common.crypto.IdentityCrypto
import com.maksimowiczm.foodyou.common.crypto.MasterCrypto
import com.maksimowiczm.foodyou.common.crypto.SignatureVerifier
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

internal expect fun Module.masterCryptoDefinition(): KoinDefinition<out MasterCrypto>

internal expect fun Module.identityCryptoDefinition(): KoinDefinition<out IdentityCrypto>

internal expect fun Module.signatureVerifierDefinition(): KoinDefinition<out SignatureVerifier>

fun Module.cryptoModule() {
    masterCryptoDefinition()
    identityCryptoDefinition()
    signatureVerifierDefinition()
}
