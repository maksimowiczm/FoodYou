package com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.presentation.ExternalDatabasesViewModel
import com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.presentation.FoodPreferencesModel
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.ext.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExternalDatabasesScreen(
    onBack: () -> Unit,
    onSwissFoodCompositionDatabase: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ExternalDatabasesViewModel = koinViewModel()
    val model by viewModel.foodPreferences.collectAsStateWithLifecycle()

    ExternalDatabasesScreen(
        onBack = onBack,
        model = model,
        onOpenFoodFactsChange = viewModel::toggleOpenFoodFacts,
        onUsdaChange = viewModel::toggleUsda,
        onSwissFoodCompositionDatabase = onSwissFoodCompositionDatabase,
        modifier = modifier,
    )
}

private data class ExternalDatabasesState(val useOpenFoodFacts: Boolean, val useUsda: Boolean)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExternalDatabasesScreen(
    onBack: () -> Unit,
    model: FoodPreferencesModel,
    onOpenFoodFactsChange: (Boolean) -> Unit,
    onUsdaChange: (Boolean) -> Unit,
    onSwissFoodCompositionDatabase: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_food_database)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
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
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.description_food_database),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                OpenFoodFactsCard(
                    selected = model.useOpenFoodFacts,
                    onSelectedChange = onOpenFoodFactsChange,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                UsdaCard(
                    selected = model.useUsda,
                    onSelectedChange = onUsdaChange,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                SwissFoodCompositionDatabase(
                    onClick = onSwissFoodCompositionDatabase,
                    modifier = Modifier.fillMaxWidth(),
                )
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
    selected: Boolean?,
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
                    TriStateCheckbox(
                        state = remember(selected) { selected.toToggleableState() },
                        onClick = null,
                    )
                }
            }
        },
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 16.dp),
        onClick = {
            val selected = selected ?: false
            onSelectedChange(!selected)
        },
    ) {
        Column {
            Text(
                text = stringResource(Res.string.description_open_food_facts),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = tos, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            FeaturesContainer {
                ShareURL()
                InAppDownload()
                InAppSearch()
            }
        }
    }
}

@Composable
private fun UsdaCard(
    selected: Boolean?,
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
                    TriStateCheckbox(
                        state = remember(selected) { selected.toToggleableState() },
                        onClick = null,
                    )
                }
            }
        },
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 16.dp),
        onClick = {
            val selected = selected ?: false
            onSelectedChange(!selected)
        },
    ) {
        Column {
            Text(
                text = stringResource(Res.string.description_food_data_central_usda),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = tos, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            FeaturesContainer {
                ShareURL()
                InAppDownload()
                InAppSearch()
            }
        }
    }
}

@Composable
private fun SwissFoodCompositionDatabase(onClick: () -> Unit, modifier: Modifier = Modifier) {
    DatabaseCard(
        title = {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.headline_swiss_food_composition_database),
                    style = MaterialTheme.typography.titleSmall,
                )
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
        },
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 16.dp),
        onClick = onClick,
    ) {
        Text(
            text = stringResource(Res.string.description_swiss_food_composition_database_short),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        FeaturesContainer { ManualImport() }
    }
}

private fun CharIterator.readUntil(delimiter: Char): String = buildString {
    while (hasNext()) {
        val ch = nextChar()
        if (ch == delimiter) break
        append(ch)
    }
}

@Composable
private fun FeaturesContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(Res.string.headline_features),
            style = MaterialTheme.typography.labelLarge,
        )

        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall) {
            content()
        }
    }
}

@Composable
private fun ShareURL(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Outlined.Share, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(stringResource(Res.string.feature_share_url_with_food_you))
    }
}

@Composable
private fun InAppDownload(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Outlined.Download, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(stringResource(Res.string.feature_in_app_download))
    }
}

@Composable
private fun InAppSearch(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(Res.drawable.ic_database_search), contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(stringResource(Res.string.feature_in_app_search))
    }
}

@Composable
private fun ManualImport(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Outlined.FileOpen, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(stringResource(Res.string.feature_manual_database_import))
    }
}

private fun Boolean?.toToggleableState(): ToggleableState =
    when (this) {
        true -> ToggleableState.On
        false -> ToggleableState.Off
        null -> ToggleableState.Indeterminate
    }
