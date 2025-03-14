package com.maksimowiczm.foodyou.infrastructure.desktop

import java.io.File
import kotlin.apply

val preferencesDirectory: String
    get() {
        val file = File(System.getProperty("java.io.tmpdir"), "FindMyIp").apply {
            if (!exists()) {
                mkdir()
            }
        }

        return file.absolutePath
    }
