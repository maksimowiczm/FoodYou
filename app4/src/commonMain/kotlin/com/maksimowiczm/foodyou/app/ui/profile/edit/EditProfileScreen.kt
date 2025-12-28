package com.maksimowiczm.foodyou.app.ui.profile.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.profile.ProfileForm
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
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: EditProfileViewModel = koinViewModel { parametersOf(profileId) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val canDelete by viewModel.canDelete.collectAsStateWithLifecycle()

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            EditProfileEvent.Edited -> onEdit()
            EditProfileEvent.Deleted -> onDelete()
        }
    }

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = uiState.isModified,
        onBackCompleted = { showDiscardDialog = true },
    )
    if (showDiscardDialog) {
        DiscardChangesDialog(onDismissRequest = { showDiscardDialog = false }, onDiscard = onBack) {
            Text(stringResource(Res.string.question_discard_changes))
        }
    }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.headline_delete_profile)) },
            icon = { Icon(imageVector = Icons.Outlined.Delete, contentDescription = null) },
            iconContentColor = MaterialTheme.colorScheme.onErrorContainer,
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.delete()
                    },
                    shapes = ButtonDefaults.shapes(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                ) {
                    Text(stringResource(Res.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(Res.string.action_cancel))
                }
            },
            text = { Text(stringResource(Res.string.description_delete_profile)) },
        )
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
                        enabled = uiState.isValid && !uiState.isLocked,
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
            modifier =
                Modifier.fillMaxSize()
                    .imePadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            item {
                ProfileForm(
                    uiState = uiState,
                    onSetAvatar = viewModel::setAvatar,
                    autoFocusName = false,
                )
            }
            if (canDelete) {
                item {
                    Spacer(Modifier.height(32.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(
                            onClick = { showDeleteDialog = true },
                            shapes = ButtonDefaults.shapes(),
                            enabled = !uiState.isLocked,
                        ) {
                            Text(stringResource(Res.string.action_delete_profile))
                        }
                    }
                }
            }
        }
    }
}
