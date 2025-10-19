package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ModalSideSheet
import com.maksimowiczm.foodyou.app.ui.common.component.rememberSideSheetState
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

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
) {
    val scope = rememberCoroutineScope()

    val viewModel: ProfileViewModel = koinViewModel()
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val selectedProfileId by viewModel.selectedProfile.collectAsStateWithLifecycle()
    val selectedProfile =
        remember(profiles, selectedProfileId) { profiles.find { it.id == selectedProfileId } }

    val sheetState = rememberSideSheetState(isOpen = false)

    ModalSideSheet(
        content = {
            HomeMainScreen(
                selectedProfile = selectedProfile,
                onProfile = { scope.launch { sheetState.open() } },
            )
        },
        onDismissRequest = { scope.launch { sheetState.close() } },
        sheetState = sheetState,
    ) {
        ProfileSwitcherScreen(
            onFoodDatabase = onFoodDatabase,
            onPersonalization = onPersonalization,
            onDataBackupAndExport = onDataBackupAndExport,
            onLanguage = onLanguage,
            onPrivacy = onPrivacy,
            onAbout = onAbout,
            onAddProfile = onAddProfile,
            onSelectProfile = viewModel::selectProfile,
            onEditProfile = onEditProfile,
            profiles = profiles,
            selectedProfile = selectedProfile ?: return@ModalSideSheet,
        )
    }
}
