package com.maksimowiczm.foodyou.shared.ui.form

import io.konform.validation.ValidationBuilder
import io.konform.validation.path.ValidationPath
import kotlin.jvm.JvmName

fun ValidationBuilder<String>.validateDouble(
    path: ValidationPath = ValidationPath.EMPTY,
    validation: ValidationBuilder<Double?>.() -> Unit,
) = validate(path, { it.toDoubleOrNull() }, validation)

@JvmName("ValidateDoubleNullableString")
fun ValidationBuilder<String?>.validateDouble(
    path: ValidationPath = ValidationPath.EMPTY,
    validation: ValidationBuilder<Double?>.() -> Unit,
) = validate(path, { it?.toDoubleOrNull() }, validation)
