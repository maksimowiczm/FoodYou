package com.maksimowiczm.foodyou.app.ui.common.form

import io.konform.validation.ValidationBuilder
import io.konform.validation.path.ValidationPath

fun ValidationBuilder<String>.validateDouble(
    path: ValidationPath = ValidationPath.EMPTY,
    validation: ValidationBuilder<Double?>.() -> Unit,
) = validate(path, { it.toDoubleOrNull() }, validation)
