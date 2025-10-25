package com.maksimowiczm.foodyou.app.ui.profile.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.profile.ProfileFormScreen
import com.maksimowiczm.foodyou.common.domain.ProfileId
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditProfileScreen(
    profileId: ProfileId,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: EditProfileViewModel = koinViewModel { parametersOf(profileId) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            EditProfileEvent.Edited -> onEdit()
        }
    }

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = uiState.isModified) { showDiscardDialog = true }
    if (showDiscardDialog) {
        DiscardChangesDialog(onDismissRequest = { showDiscardDialog = false }, onDiscard = onBack) {
            Text(stringResource(Res.string.question_discard_changes))
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_edit_profile)) },
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = { if (uiState.isModified) showDiscardDialog = true else onBack() }
                    )
                },
                actions = {
                    val buttonHeight = ButtonDefaults.ExtraSmallContainerHeight
                    Button(
                        onClick = viewModel::edit,
                        shapes = ButtonDefaults.shapesFor(buttonHeight),
                        modifier = Modifier.height(buttonHeight),
                        enabled = uiState.isValid,
                        contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight),
                    ) {
                        Text(
                            text = stringResource(Res.string.action_save),
                            style = ButtonDefaults.textStyleFor(buttonHeight),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            item {
                ProfileFormScreen(
                    uiState = uiState,
                    onSetAvatar = viewModel::setAvatar,
                    autoFocusName = false,
                )
            }
        }
    }
}
