package com.maksimowiczm.foodyou.feature.diary.openfoodfacts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.diary.openfoodfacts.ui.OpenFoodFactsErrorCard

@Composable
fun OpenFoodFactsErrorCard(
    throwable: Throwable?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    OpenFoodFactsErrorCard(
        throwable = throwable,
        onRetry = onRetry,
        modifier = modifier
    )
}
