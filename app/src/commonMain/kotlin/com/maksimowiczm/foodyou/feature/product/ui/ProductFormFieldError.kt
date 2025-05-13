package com.maksimowiczm.foodyou.feature.product.ui

import androidx.compose.runtime.Composable
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

internal enum class ProductFormFieldError {
    Required,
    NotANumber,
    MustBePositive;

    @Composable
    fun stringResource() = when (this) {
        Required -> stringResource(Res.string.neutral_required)
        NotANumber -> stringResource(Res.string.error_invalid_number)
        MustBePositive -> stringResource(Res.string.error_value_must_be_positive)
    }
}
