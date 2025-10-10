package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    onFoodDatabase: () -> Unit,
    onPersonalization: () -> Unit,
    onDataBackupAndExport: () -> Unit,
    onLanguage: () -> Unit,
    onPrivacy: () -> Unit,
    onAbout: () -> Unit,
    onAddProfile: () -> Unit,
    onEditProfile: (ProfileUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileSwitcherScreen(
        onBack = {},
        onFoodDatabase = onFoodDatabase,
        onPersonalization = onPersonalization,
        onDataBackupAndExport = onDataBackupAndExport,
        onLanguage = onLanguage,
        onPrivacy = onPrivacy,
        onAbout = onAbout,
        onAddProfile = onAddProfile,
        onEditProfile = onEditProfile,
        modifier = modifier,
    )
}
