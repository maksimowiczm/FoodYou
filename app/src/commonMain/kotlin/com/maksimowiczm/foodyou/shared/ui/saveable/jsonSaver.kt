package com.maksimowiczm.foodyou.shared.ui.saveable

import androidx.compose.runtime.saveable.Saver
import kotlinx.serialization.json.Json

inline fun <reified T> jsonSaver() =
    Saver<T, String>(save = { Json.encodeToString(it) }, restore = { Json.decodeFromString(it) })
