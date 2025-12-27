package com.maksimowiczm.foodyou.app.ui.database.externaldatabases

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
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.common.compose.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExternalDatabasesScreen(
    onBack: () -> Unit,
<<<<<<< Updated upstream
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

@Composable
private fun ExternalDatabasesScreen(
    onBack: () -> Unit,
    model: FoodPreferencesModel,
    onOpenFoodFactsChange: (Boolean) -> Unit,
    onUsdaChange: (Boolean) -> Unit,
    onSwissFoodCompositionDatabase: () -> Unit,
=======
    onTBCA: () -> Unit,
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
                OpenFoodFactsPrivacyCard(
                    selected = model.useOpenFoodFacts ?: false,
                    onSelectedChange = onOpenFoodFactsChange,
                )
            }
            item {
                UsdaPrivacyCard(selected = model.useUsda ?: false, onSelectedChange = onUsdaChange)
            }

            item {
                SwissFoodCompositionDatabase(
                    onClick = onSwissFoodCompositionDatabase,
=======
                TBCACard(
                    onClick = onTBCA,
>>>>>>> Stashed changes
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
private fun ManualImport(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Outlined.FileOpen, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(stringResource(Res.string.feature_manual_database_import))
    }
}
