package com.maksimowiczm.foodyou.ui.res

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.data.model.WeightUnit
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeightUnit.pluralString(count: Int): String = when (this) {
    WeightUnit.Gram -> pluralStringResource(Res.plurals.unit_gram, count)
    WeightUnit.Milliliter -> pluralStringResource(Res.plurals.unit_milliliter, count)
}

@Composable
fun WeightUnit.stringResourceShort(): String = when (this) {
    WeightUnit.Gram -> stringResource(Res.string.unit_gram_short)
    WeightUnit.Milliliter -> stringResource(Res.string.unit_milliliter_short)
}
