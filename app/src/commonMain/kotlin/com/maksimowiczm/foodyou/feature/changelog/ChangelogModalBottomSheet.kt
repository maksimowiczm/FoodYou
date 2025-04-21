package com.maksimowiczm.foodyou.feature.changelog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.changelog.ui.ChangelogModalBottomSheet

@Composable
fun ChangelogModalBottomSheet(onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    ChangelogModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    )
}
