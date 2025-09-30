package com.maksimowiczm.foodyou.common.infrastructure.crypto

import com.maksimowiczm.foodyou.common.crypto.IdentityCrypto
import com.maksimowiczm.foodyou.common.crypto.MasterCrypto
import com.maksimowiczm.foodyou.common.crypto.SignatureVerifier
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal actual fun Module.masterCryptoDefinition(): KoinDefinition<out MasterCrypto> =
    singleOf(::AndroidMasterCrypto).bind<MasterCrypto>()

internal actual fun Module.identityCryptoDefinition(): KoinDefinition<out IdentityCrypto> =
    singleOf(::AndroidIdentityCrypto).bind<IdentityCrypto>()

internal actual fun Module.signatureVerifierDefinition(): KoinDefinition<out SignatureVerifier> =
    singleOf(::AndroidSignatureVerifier).bind<SignatureVerifier>()
