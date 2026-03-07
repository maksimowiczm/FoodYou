package com.maksimowiczm.foodyou.app.ui.userfood

import androidx.compose.runtime.*
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

internal enum class QuantityUnit {
    Gram,
    Milliliter,
    Ounce,
    FluidOunce;

    @Composable
    fun stringResource(): String =
        when (this) {
            Gram -> stringResource(Res.string.unit_gram_short)
            Ounce -> stringResource(Res.string.unit_ounce_short)
            Milliliter -> stringResource(Res.string.unit_milliliter_short)
            FluidOunce -> stringResource(Res.string.unit_fluid_ounce_short)
        }
}
