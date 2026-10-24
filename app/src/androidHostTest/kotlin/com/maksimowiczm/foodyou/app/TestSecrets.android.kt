package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.unavailable

actual object TestSecrets {
    actual val usdaApiKey: String?
        get() = unavailable("Secrets not available on Android host")
}
