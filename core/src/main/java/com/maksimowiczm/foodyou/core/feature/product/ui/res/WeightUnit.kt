package com.maksimowiczm.foodyou.core.feature.product.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit

@Composable
fun WeightUnit.pluralString(count: Int): String = when (this) {
    WeightUnit.Gram -> pluralStringResource(R.plurals.unit_gram, count)
    WeightUnit.Milliliter -> pluralStringResource(R.plurals.unit_milliliter, count)
}

@Composable
fun WeightUnit.stringResourceShort(): String = when (this) {
    WeightUnit.Gram -> stringResource(R.string.unit_gram_short)
    WeightUnit.Milliliter -> stringResource(R.string.unit_milliliter_short)
}
