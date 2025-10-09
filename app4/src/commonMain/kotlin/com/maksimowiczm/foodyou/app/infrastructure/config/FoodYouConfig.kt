package com.maksimowiczm.foodyou.app.infrastructure.config

import com.maksimowiczm.foodyou.app.BuildConfig
import com.maksimowiczm.foodyou.common.config.AppConfig

class FoodYouConfig : AppConfig {
    override val versionName: String = BuildConfig.VERSION_NAME
}
