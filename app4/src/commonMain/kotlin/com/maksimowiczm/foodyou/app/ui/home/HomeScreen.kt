package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maksimowiczm.foodyou.app.ui.common.component.ModalSideSheet
import com.maksimowiczm.foodyou.app.ui.common.component.rememberSideSheetState
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
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
                navController = navController,
                selectedProfile = selectedProfile,
                onProfile = { scope.launch { sheetState.open() } },
            )
        },
        onDismissRequest = { scope.launch { sheetState.close() } },
        modifier = modifier,
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
            onSelectProfile = {
                viewModel.selectProfile(it)
                scope.launch { sheetState.close() }
            },
            onEditProfile = onEditProfile,
            profiles = profiles,
            selectedProfile = selectedProfile ?: return@ModalSideSheet,
        )
    }
}
