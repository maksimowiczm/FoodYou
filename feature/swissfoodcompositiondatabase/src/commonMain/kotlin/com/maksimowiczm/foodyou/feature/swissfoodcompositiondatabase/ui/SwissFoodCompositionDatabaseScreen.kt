package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwissFoodCompositionDatabaseScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<SwissFoodCompositionDatabaseViewModel>()

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var selectedLanguage by rememberSaveable { mutableStateOf<Language?>(null) }

    if (selectedLanguage != null) {
        AddProductsDialog(
            onDismissRequest = { selectedLanguage = null },
            onAdd = {
                viewModel.onLanguageSelected(selectedLanguage!!)
                selectedLanguage = null
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    ArrowBackIconButton(onBack)
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.headline_swiss_food_composition_database),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Text(
                    text = stringResource(Res.string.description2_swiss_food_composition_database),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                val sourceStr = stringResource(Res.string.headline_source)
                val link = stringResource(Res.string.link_swiss_food_composition_database)
                val style = MaterialTheme.typography.bodyMedium
                val linkStyle = style.copy(color = MaterialTheme.colorScheme.primary)

                val text = remember(sourceStr, link, style, linkStyle) {
                    buildAnnotatedString {
                        withStyle(style.toSpanStyle()) {
                            append(sourceStr)
                            append(" ")
                            withLink(
                                LinkAnnotation.Url(link, TextLinkStyles(linkStyle.toSpanStyle()))
                            ) {
                                append(link)
                            }
                        }
                    }
                }

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                val transition = updateTransition(uiState)

                transition.AnimatedContent(
                    contentKey = { it::class }
                ) {
                    when (it) {
                        SwissFoodCompositionDatabaseUiState.Finished -> ImportingFinished(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 100.dp)
                        )

                        is SwissFoodCompositionDatabaseUiState.Importing -> ImportingProgress(
                            progress = it.progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 100.dp)
                        )

                        SwissFoodCompositionDatabaseUiState.LanguagePick -> LanguagePicker(
                            onLanguage = { selectedLanguage = it },
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguagePicker(onLanguage: (Language) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LanguageButton(
            label = "English",
            onClick = { onLanguage(Language.ENGLISH) }
        )
        LanguageButton(
            label = "Deutsch",
            onClick = { onLanguage(Language.GERMAN) }
        )
        LanguageButton(
            label = "Français",
            onClick = { onLanguage(Language.FRENCH) }
        )
        LanguageButton(
            label = "Italiano",
            onClick = { onLanguage(Language.ITALIAN) }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LanguageButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }

    val isPressed by interactionSource.collectIsPressedAsState()

    val cornerRadius by animateDpAsState(
        targetValue = if (isPressed) 24.dp else 16.dp,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainer,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ImportingProgress(progress: Float, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularWavyProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(68.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.notification_importing_products),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.description_please_wait_while_importing_products),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ImportingFinished(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(68.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.notification_importing_products_success),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AddProductsDialog(
    onDismissRequest: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(onAdd) {
                Text(stringResource(Res.string.positive_ok))
            }
        },
        dismissButton = {
            TextButton(onDismissRequest) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = {
            Text(stringResource(Res.string.description3_swiss_food_composition_database))
        }
    )
}
