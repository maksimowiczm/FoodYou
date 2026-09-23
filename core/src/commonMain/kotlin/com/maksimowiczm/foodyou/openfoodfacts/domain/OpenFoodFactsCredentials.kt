package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.common.infrastructure.crypto.SoftwareEncrypted
import com.maksimowiczm.foodyou.common.infrastructure.crypto.decryptString

data class OpenFoodFactsCredentials(val login: SoftwareEncrypted, val password: SoftwareEncrypted) {
    fun decrypt() = Decrypted(login = login.decryptString(), password = password.decryptString())

    data class Decrypted(val login: String, val password: String)
}
