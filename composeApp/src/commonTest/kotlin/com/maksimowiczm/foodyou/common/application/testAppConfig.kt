package com.maksimowiczm.foodyou.common.application

fun testAppConfig(versionName: String = "testVersionName"): AppConfig =
    object : AppConfig {
        override val versionName: String = versionName
    }
