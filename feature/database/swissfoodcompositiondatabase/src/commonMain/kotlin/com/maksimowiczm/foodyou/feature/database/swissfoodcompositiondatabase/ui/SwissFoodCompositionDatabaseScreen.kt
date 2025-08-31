package com.maksimowiczm.foodyou.feature.database.swissfoodcompositiondatabase.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.business.food.domain.SwissFoodCompositionDatabaseRepository.Language
import com.maksimowiczm.foodyou.feature.database.swissfoodcompositiondatabase.presentation.SwissFoodCompositionDatabaseUiState
import com.maksimowiczm.foodyou.feature.database.swissfoodcompositiondatabase.presentation.SwissFoodCompositionDatabaseViewModel
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.BackHandler
import com.maksimowiczm.foodyou.shared.ui.ext.add
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwissFoodCompositionDatabaseScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = koinInject<SwissFoodCompositionDatabaseViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SwissFoodCompositionDatabaseScreen(
        uiState = uiState,
        onBack = onBack,
        onImport = viewModel::import,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SwissFoodCompositionDatabaseScreen(
    uiState: SwissFoodCompositionDatabaseUiState,
    onBack: () -> Unit,
    onImport: (Set<Language>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutinesScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var languages by rememberSaveable { mutableStateOf(setOf<Language>()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val pleaseWaitMessage =
        stringResource(Res.string.description_please_wait_while_importing_products)

    LaunchedEffect(uiState) {
        when (uiState) {
            SwissFoodCompositionDatabaseUiState.Finished ->
                snackbarHostState.currentSnackbarData?.dismiss()

            is SwissFoodCompositionDatabaseUiState.Importing,
            SwissFoodCompositionDatabaseUiState.LanguagePick -> Unit
        }
    }

    BackHandler(
        enabled = uiState is SwissFoodCompositionDatabaseUiState.Importing,
        onBack = { coroutinesScope.launch { snackbarHostState.showSnackbar(pleaseWaitMessage) } },
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(Res.string.headline_swiss_food_composition_database))
                },
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = onBack,
                        enabled = uiState !is SwissFoodCompositionDatabaseUiState.Importing,
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            when (uiState) {
                SwissFoodCompositionDatabaseUiState.Finished ->
                    ImportingFinished(
                        modifier = Modifier.align(Alignment.Center).padding(paddingValues)
                    )

                is SwissFoodCompositionDatabaseUiState.Importing ->
                    ImportingProgress(
                        progress = uiState.progress,
                        modifier = Modifier.align(Alignment.Center).padding(paddingValues),
                    )

                SwissFoodCompositionDatabaseUiState.LanguagePick ->
                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding =
                            paddingValues
                                .add(bottom = 72.dp) // Button height + padding
                                .add(vertical = 8.dp),
                    ) {
                        item {
                            Text(
                                text =
                                    stringResource(
                                        Res.string.description2_swiss_food_composition_database
                                    ),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        item {
                            LanguagePicker(selected = languages, onLanguages = { languages = it })
                        }
                    }
            }

            AnimatedVisibility(
                visible =
                    uiState == SwissFoodCompositionDatabaseUiState.LanguagePick &&
                        languages.isNotEmpty(),
                modifier =
                    Modifier.zIndex(10f)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
                Button(
                    onClick = { onImport(languages) },
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.height(56.dp),
                ) {
                    Text(stringResource(Res.string.action_import))
                }
            }

            AnimatedVisibility(
                visible = uiState == SwissFoodCompositionDatabaseUiState.Finished,
                modifier =
                    Modifier.zIndex(10f)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                enter = slideInVertically { it } + fadeIn(),
            ) {
                Button(
                    onClick = onBack,
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.height(56.dp),
                ) {
                    Text(stringResource(Res.string.action_done))
                }
            }
        }
    }
}

@Composable
private fun LanguagePicker(
    selected: Set<Language>,
    onLanguages: (Set<Language>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LanguageButton(
            label = "English",
            selected = Language.ENGLISH in selected,
            onClick = {
                if (Language.ENGLISH in selected) {
                    onLanguages(selected - Language.ENGLISH)
                } else {
                    onLanguages(selected + Language.ENGLISH)
                }
            },
        )
        LanguageButton(
            label = "Deutsch",
            selected = Language.GERMAN in selected,
            onClick = {
                if (Language.GERMAN in selected) {
                    onLanguages(selected - Language.GERMAN)
                } else {
                    onLanguages(selected + Language.GERMAN)
                }
            },
        )
        LanguageButton(
            label = "Français",
            selected = Language.FRENCH in selected,
            onClick = {
                if (Language.FRENCH in selected) {
                    onLanguages(selected - Language.FRENCH)
                } else {
                    onLanguages(selected + Language.FRENCH)
                }
            },
        )
        LanguageButton(
            label = "Italiano",
            selected = Language.ITALIAN in selected,
            onClick = {
                if (Language.ITALIAN in selected) {
                    onLanguages(selected - Language.ITALIAN)
                } else {
                    onLanguages(selected + Language.ITALIAN)
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LanguageButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val isPressed by interactionSource.collectIsPressedAsState()

    val cornerRadius by
        animateDpAsState(
            targetValue = if (isPressed) 24.dp else 16.dp,
            animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        )

    val color by
        animateColorAsState(
            targetValue =
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
        )
    val contentColor by
        animateColorAsState(
            targetValue =
                if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
        )

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = color,
        contentColor = contentColor,
        interactionSource = interactionSource,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ImportingProgress(progress: Float, modifier: Modifier = Modifier) {
    val animatedProgress by
        animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        )

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularWavyProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(68.dp),
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.notification_importing_products),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.description_please_wait_while_importing_products),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ImportingFinished(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(68.dp),
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.notification_importing_products_success),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
