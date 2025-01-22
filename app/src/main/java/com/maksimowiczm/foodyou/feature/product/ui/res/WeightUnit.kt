package com.maksimowiczm.foodyou.feature.product.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit

@Composable
fun WeightUnit.pluralString(count: Int): String {
    return when (this) {
        WeightUnit.Gram -> pluralStringResource(R.plurals.unit_gram, count)
        WeightUnit.Millilitre -> pluralStringResource(R.plurals.unit_milligram, count)
    }
}

@Composable
fun WeightUnit.stringResourceShort(): String {
    return when (this) {
        WeightUnit.Gram -> stringResource(R.string.unit_gram_short)
        WeightUnit.Millilitre -> stringResource(R.string.unit_milligram_short)
    }
}
