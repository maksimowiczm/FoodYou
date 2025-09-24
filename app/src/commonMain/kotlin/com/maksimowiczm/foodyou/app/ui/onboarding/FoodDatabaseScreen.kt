package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.common.compose.extension.add
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain.SwissFoodCompositionDatabaseRepository.Language
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodDatabaseScreen(
    onBack: () -> Unit,
    onAgree: () -> Unit,
    onSkip: () -> Unit,
    state: OnboardingState,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_food_database)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = { TextButton(onSkip) { Text(stringResource(Res.string.action_skip)) } },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding =
                    paddingValues
                        .add(bottom = 72.dp) // Button height + padding
                        .add(vertical = 8.dp),
            ) {
                item {
                    Text(
                        text = stringResource(Res.string.description_food_database),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                item {
                    OpenFoodFactsCard(
                        selected = state.useOpenFoodFacts,
                        onSelectedChange = { state.useOpenFoodFacts = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item {
                    UsdaCard(
                        selected = state.useUsda,
                        onSelectedChange = { state.useUsda = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item {
                    SwissFoodCompositionDatabase(
                        languages = state.swissLanguages,
                        onLanguageChange = { state.swissLanguages = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Button(
                onClick = onAgree,
                shapes = ButtonDefaults.shapes(),
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .height(56.dp),
            ) {
                Text(stringResource(Res.string.action_agree_and_continue))
            }
        }
    }
}

@Composable
private fun DatabaseCard(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val inner =
        @Composable {
            Column(Modifier.padding(contentPadding)) {
                title()
                Spacer(Modifier.height(8.dp))
                content()
            }
        }

    if (onClick == null) {
        Surface(
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            content = inner,
        )
    } else {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            content = inner,
        )
    }
}

@Composable
private fun OpenFoodFactsCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val iterator =
        stringResource(Res.string.open_food_facts_terms_of_use_and_privacy_policy).iterator()
    val tos =
        remember(iterator) {
            buildAnnotatedString {
                while (iterator.hasNext()) {
                    val char = iterator.nextChar()

                    if (char != '{') {
                        append(char)
                        continue
                    }

                    val label = iterator.readUntil(':')
                    val link = iterator.readUntil('}')

                    withLink(LinkAnnotation.Url(link)) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(label) }
                    }
                }
            }
        }

    DatabaseCard(
        title = {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.openfoodfacts_logo),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                }
                Text(
                    text = stringResource(Res.string.headline_open_food_facts),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Checkbox(checked = selected, onCheckedChange = null)
                }
            }
        },
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        onClick = { onSelectedChange(!selected) },
    ) {
        Column {
            Text(
                text = stringResource(Res.string.description_open_food_facts),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = tos, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun UsdaCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val iterator = stringResource(Res.string.usda_privacy_policy).iterator()
    val tos =
        remember(iterator) {
            buildAnnotatedString {
                while (iterator.hasNext()) {
                    val char = iterator.nextChar()

                    if (char != '{') {
                        append(char)
                        continue
                    }

                    val label = iterator.readUntil(':')
                    val link = iterator.readUntil('}')

                    withLink(LinkAnnotation.Url(link)) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(label) }
                    }
                }
            }
        }

    DatabaseCard(
        title = {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.usda_logo),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                }
                Text(
                    text = stringResource(Res.string.headline_food_data_central_usda),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Checkbox(checked = selected, onCheckedChange = null)
                }
            }
        },
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        onClick = { onSelectedChange(!selected) },
    ) {
        Column {
            Text(
                text = stringResource(Res.string.description_food_data_central_usda),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = tos, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SwissFoodCompositionDatabase(
    languages: Set<Language>,
    onLanguageChange: (Set<Language>) -> Unit,
    modifier: Modifier = Modifier,
) {
    DatabaseCard(
        title = {
            Text(
                text = stringResource(Res.string.headline_swiss_food_composition_database),
                style = MaterialTheme.typography.titleSmall,
            )
        },
        modifier = modifier,
    ) {
        Column {
            Text(
                text = stringResource(Res.string.description2_swiss_food_composition_database),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            LanguageButton(
                label = "English",
                onClick = {
                    onLanguageChange(
                        if (languages.contains(Language.ENGLISH)) {
                            languages - Language.ENGLISH
                        } else {
                            languages + Language.ENGLISH
                        }
                    )
                },
                selected = Language.ENGLISH in languages,
            )
            LanguageButton(
                label = "Deutsch",
                onClick = {
                    onLanguageChange(
                        if (languages.contains(Language.GERMAN)) {
                            languages - Language.GERMAN
                        } else {
                            languages + Language.GERMAN
                        }
                    )
                },
                selected = Language.GERMAN in languages,
            )
            LanguageButton(
                label = "Français",
                onClick = {
                    onLanguageChange(
                        if (languages.contains(Language.FRENCH)) {
                            languages - Language.FRENCH
                        } else {
                            languages + Language.FRENCH
                        }
                    )
                },
                selected = Language.FRENCH in languages,
            )
            LanguageButton(
                label = "Italiano",
                onClick = {
                    onLanguageChange(
                        if (languages.contains(Language.ITALIAN)) {
                            languages - Language.ITALIAN
                        } else {
                            languages + Language.ITALIAN
                        }
                    )
                },
                selected = Language.ITALIAN in languages,
            )
        }
    }
}

@Composable
private fun LanguageButton(
    label: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cornerRadius by
        animateDpAsState(
            targetValue = if (isPressed) 4.dp else 16.dp,
            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        )

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color =
            if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        contentColor =
            if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        interactionSource = interactionSource,
    ) {
        Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun CharIterator.readUntil(delimiter: Char): String = buildString {
    while (hasNext()) {
        val ch = nextChar()
        if (ch == delimiter) break
        append(ch)
    }
}
