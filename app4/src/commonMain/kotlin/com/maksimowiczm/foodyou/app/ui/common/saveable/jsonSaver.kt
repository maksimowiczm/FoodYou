package com.maksimowiczm.foodyou.app.ui.common.saveable

import androidx.compose.runtime.saveable.Saver
import kotlinx.serialization.json.Json

inline fun <reified T> jsonSaver(): Saver<T, String> =
    Saver(save = { Json.encodeToString(it) }, restore = { Json.decodeFromString<T>(it) })
