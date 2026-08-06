package com.maksimowiczm.foodyou.app

import androidx.test.platform.app.InstrumentationRegistry

actual object TestSecrets {
    actual val usdaApiKey: String? =
        InstrumentationRegistry.getArguments().getString("usda.api.key")
}
