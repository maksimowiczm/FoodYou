package com.maksimowiczm.foodyou.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A feature that can be added to the settings screen.
 */
fun interface SettingsFeature {

    /**
     * List item that will be added to the settings list.
     */
    @Composable
    fun SettingsListItem(modifier: Modifier)
}
