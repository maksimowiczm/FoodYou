package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.runtime.Composable
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

enum class ProductFormFieldError {
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
