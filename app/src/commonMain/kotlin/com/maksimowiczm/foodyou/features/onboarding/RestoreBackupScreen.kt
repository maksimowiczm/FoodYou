package com.maksimowiczm.foodyou.features.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.common.onSuccess
import com.maksimowiczm.foodyou.features.onboarding.dataimport.foodyou3.OpenFoodYou3DatabaseFilePickerError
import com.maksimowiczm.foodyou.features.onboarding.dataimport.foodyou3.openFoodYou3DatabaseFilePicker
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.utility.LocalAppConfig
import foodyou.app.generated.resources.*
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RestoreBackupScreen(
    onBack: () -> Unit,
    onSkip: () -> Unit,
    onRestoreFoodYou3: (path: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appConfig = LocalAppConfig.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    var openingFile by remember { mutableStateOf(false) }
    var openingFileJob by remember { mutableStateOf<Job?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val errorReadFile = stringResource(Res.string.error_failed_to_read_file)
    val invalidFoodYou3 = stringResource(Res.string.error_invalid_food_you_3_database)

    Crossfade(targetState = openingFile, modifier = modifier) { opening ->
        if (opening) {
            LoadingScreen(
                onBack = {
                    openingFileJob?.cancel()
                    openingFile = false
                }
            )
        } else {
            RestoreBackupScreen(
                onBack = onBack,
                onSkip = onSkip,
                onRestoreFoodYou3 = {
                    openingFileJob?.cancel()
                    openingFile = true
                    openingFileJob = scope.launch {
                        FileKit.openFoodYou3DatabaseFilePicker()
                            .onSuccess { file ->
                                if (file != null) onRestoreFoodYou3(file.path)
                                else openingFile = false
                            }
                            .onError { error ->
                                Logger.withTag("RestoreBackupScreen").e(error.cause) {
                                    "Failed to Food You 3 database"
                                }
                                openingFile = false
                                val message =
                                    when (error) {
                                        is OpenFoodYou3DatabaseFilePickerError.ReadFileFailed ->
                                            errorReadFile

                                        is OpenFoodYou3DatabaseFilePickerError.InvalidSqliteDatabase,
                                        is OpenFoodYou3DatabaseFilePickerError.InvalidFoodYou3Database ->
                                            invalidFoodYou3
                                    }
                                snackbarHostState.showSnackbar(message)
                            }
                    }
                },
                onContactEmail = { uriHandler.openUri(appConfig.emailContactUri) },
                onOpenGithub = { uriHandler.openUri(appConfig.featureRequestUri) },
                snackbarHostState = snackbarHostState,
            )
        }
    }
}

@Composable
private fun RestoreBackupScreen(
    onBack: () -> Unit,
    onSkip: () -> Unit,
    onRestoreFoodYou3: () -> Unit,
    onContactEmail: () -> Unit,
    onOpenGithub: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val fabHeight = 56.dp
    val colors =
        ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    var isDifferentAppExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_restore_backup)) },
                subtitle = { Text(stringResource(Res.string.description_restore_backup)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Button(
                onClick = onSkip,
                shapes = ButtonDefaults.shapesFor(fabHeight),
                modifier = Modifier.widthIn(min = 250.dp),
                contentPadding = ButtonDefaults.contentPaddingFor(fabHeight),
            ) {
                Text(
                    text = stringResource(Res.string.action_skip),
                    style = ButtonDefaults.textStyleFor(fabHeight),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp).add(bottom = fabHeight + 16.dp),
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                            Text(
                                text =
                                    stringResource(Res.string.headline_import_available_only_once),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        Text(
                            text = stringResource(Res.string.description_restore_backup_onboarding),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                SegmentedListItem(
                    onClick = onRestoreFoodYou3,
                    shapes =
                        ListItemDefaults.segmentedShapes(
                            index = 0,
                            count = 1,
                            defaultShapes = ListItemDefaults.shapes(MaterialTheme.shapes.large),
                        ),
                    colors = colors,
                    content = { Text(stringResource(Res.string.headline_food_you_3)) },
                    leadingContent = {
                        Icon(
                            painterResource(Res.drawable.ic_logo),
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                        )
                    },
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                DifferentAppSection(
                    expanded = isDifferentAppExpanded,
                    onExpandedChange = { isDifferentAppExpanded = it },
                    onContactEmail = onContactEmail,
                    onOpenGithub = onOpenGithub,
                    colors = colors,
                    modifier = Modifier.clip(MaterialTheme.shapes.large),
                )
            }
        }
    }
}

@Composable
private fun DifferentAppSection(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onContactEmail: () -> Unit,
    onOpenGithub: () -> Unit,
    colors: ListItemColors,
    modifier: Modifier = Modifier,
) {
    val transition = updateTransition(expanded, label = "DifferentAppExpanded")

    Column(modifier) {
        SegmentedListItem(
            onClick = { onExpandedChange(!expanded) },
            shapes = ListItemDefaults.shapes(),
            colors = colors,
            content = { Text(stringResource(Res.string.headline_different_application)) },
            supportingContent = {
                Text(stringResource(Res.string.description_request_different_application))
            },
            trailingContent = {
                val rotationState =
                    animateFloatAsState(
                        if (expanded) 180f else 0f,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                    )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { rotationZ = rotationState.value },
                )
            },
        )
        transition.AnimatedVisibility(
            visible = { it },
            enter =
                fadeIn(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                    expandIn(MaterialTheme.motionScheme.defaultSpatialSpec()),
            exit =
                fadeOut(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                    shrinkOut(MaterialTheme.motionScheme.fastSpatialSpec()),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                SegmentedListItem(
                    onClick = onContactEmail,
                    shapes = ListItemDefaults.shapes(),
                    colors = colors,
                    content = { Text(stringResource(Res.string.action_contact_via_email)) },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.Email,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                )
                SegmentedListItem(
                    onClick = onOpenGithub,
                    shapes = ListItemDefaults.shapes(),
                    colors = colors,
                    content = { Text(stringResource(Res.string.action_open_issue_on_github)) },
                    leadingContent = {
                        Icon(
                            painterResource(Res.drawable.ic_git_hub_invertocat_black),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun RestoreBackupScreenPreview() {
    PreviewFoodYouTheme {
        RestoreBackupScreen(
            onBack = {},
            onSkip = {},
            onRestoreFoodYou3 = {},
            onContactEmail = {},
            onOpenGithub = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
