package com.maksimowiczm.foodyou.app.ui.userfood

import androidx.compose.runtime.*
import foodyou.app.generated.resources.*

internal enum class ValuesPer {
    Grams100,
    Milliliters100,
    Serving,
    Package;

    @Composable
    fun stringResource(): String =
        when (this) {
            Grams100 ->
                "100 " + org.jetbrains.compose.resources.stringResource(Res.string.unit_gram_short)
            Milliliters100 ->
                "100 " +
                    org.jetbrains.compose.resources.stringResource(Res.string.unit_milliliter_short)
            Serving ->
                org.jetbrains.compose.resources.stringResource(
                    Res.string.x_times_y,
                    "1",
                    org.jetbrains.compose.resources.stringResource(Res.string.product_serving),
                )

            Package ->
                org.jetbrains.compose.resources.stringResource(
                    Res.string.x_times_y,
                    "1",
                    org.jetbrains.compose.resources.stringResource(Res.string.product_package),
                )
        }
}
