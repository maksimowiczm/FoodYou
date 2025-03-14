package com.maksimowiczm.foodyou

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name)
    ) { }
}
