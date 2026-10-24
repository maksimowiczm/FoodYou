package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.common.domain.crypto.SoftwareCrypto
import com.maksimowiczm.foodyou.common.domain.crypto.SoftwareEncrypted
import com.maksimowiczm.foodyou.common.domain.crypto.decryptString

data class OpenFoodFactsCredentials(val login: SoftwareEncrypted, val password: SoftwareEncrypted) {
    fun decrypt(crypto: SoftwareCrypto) =
        Decrypted(login = login.decryptString(crypto), password = password.decryptString(crypto))

    data class Decrypted(val login: String, val password: String)
}
