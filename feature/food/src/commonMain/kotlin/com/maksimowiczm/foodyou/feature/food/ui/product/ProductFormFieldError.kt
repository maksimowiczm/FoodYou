package com.maksimowiczm.foodyou.feature.food.ui.product

import androidx.compose.runtime.Composable
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

internal enum class ProductFormFieldError {
    Required,
    NotANumber,
    NotPositive,
    Negative;

    @Composable
    fun stringResource() = when (this) {
        Required -> stringResource(Res.string.neutral_required)
        NotANumber -> stringResource(Res.string.error_invalid_number)
        NotPositive -> stringResource(Res.string.error_value_must_be_positive)
        Negative -> stringResource(Res.string.error_value_cannot_be_negative)
    }
}
