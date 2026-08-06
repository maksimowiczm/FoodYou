package com.maksimowiczm.foodyou.app

actual object TestSecrets {
    actual val usdaApiKey: String?
        get() = unavailable("Tests secrets aren't available on this platform")
}
