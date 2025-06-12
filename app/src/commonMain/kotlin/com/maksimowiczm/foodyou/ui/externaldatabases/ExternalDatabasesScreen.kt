package com.maksimowiczm.foodyou.ui.externaldatabases

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun ExternalDatabasesScreen(
    onBack: () -> Unit,
    onSwissFoodCompositionDatabase: () -> Unit,
    modifier: Modifier = Modifier
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExternalDatabasesScreen(
    onBack: () -> Unit,
    onOpenFoodFacts: () -> Unit,
    onFoodDataCentral: () -> Unit,
    onSwissFoodCompositionDatabase: () -> Unit,
    onSuggestDatabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_external_databases)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = paddingValues.add(bottom = 8.dp)
        ) {
            item {
                OpenFoodFacts(
                    onClick = onOpenFoodFacts
                )
            }

            item {
                FoodDataCentral(
                    onClick = onFoodDataCentral
                )
            }

            item {
                SwissFoodCompositionDatabase(
                    onClick = onSwissFoodCompositionDatabase
                )
            }

            item {
                SuggestDatabase(
                    onClick = onSuggestDatabase
                )
            }
        }
    }
}

@Composable
private fun OpenFoodFacts(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.headline_open_food_facts),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(Res.string.description_open_food_facts),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                            append(stringResource(Res.string.headline_source))
                            append(" ")
                            withStyle(
                                MaterialTheme.typography.bodyMedium
                                    .merge(color = MaterialTheme.colorScheme.primary)
                                    .toSpanStyle()
                            ) {
                                append(stringResource(Res.string.link_open_food_facts))
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            FeaturesContainer {
                ShareURL()
                InAppDownload()
            }
        }
    }
}

@Composable
private fun FoodDataCentral(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.headline_food_data_central_usda),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(Res.string.description_food_data_central_usda),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                            append(stringResource(Res.string.headline_source))
                            append(" ")
                            withStyle(
                                MaterialTheme.typography.bodyMedium
                                    .merge(color = MaterialTheme.colorScheme.primary)
                                    .toSpanStyle()
                            ) {
                                append(stringResource(Res.string.link_usda))
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            FeaturesContainer {
                ShareURL()
                InAppDownload()
            }
        }
    }
}

@Composable
private fun SwissFoodCompositionDatabase(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.headline_swiss_food_composition_database),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(Res.string.description_swiss_food_composition_database),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                            append(stringResource(Res.string.headline_source))
                            append(" ")
                            withStyle(
                                MaterialTheme.typography.bodyMedium
                                    .merge(color = MaterialTheme.colorScheme.primary)
                                    .toSpanStyle()
                            ) {
                                append(
                                    stringResource(Res.string.link_swiss_food_composition_database)
                                )
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            FeaturesContainer {
                ManualImport()
            }
        }
    }
}

@Composable
private fun FeaturesContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.headline_features),
            style = MaterialTheme.typography.labelLarge
        )

        content()
    }
}

@Composable
private fun ShareURL(modifier: Modifier = Modifier) {
    Row(modifier) {
        Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = null
        )
        Spacer(Modifier.width(16.dp))
        Text(stringResource(Res.string.feature_share_url_with_food_you))
    }
}

@Composable
private fun InAppDownload(modifier: Modifier = Modifier) {
    Row(modifier) {
        Icon(
            imageVector = Icons.Outlined.Download,
            contentDescription = null
        )
        Spacer(Modifier.width(16.dp))
        Text(stringResource(Res.string.feature_in_app_download))
    }
}

@Composable
private fun ManualImport(modifier: Modifier = Modifier) {
    Row(modifier) {
        Icon(
            imageVector = Icons.Outlined.FileOpen,
            contentDescription = null
        )
        Spacer(Modifier.width(16.dp))
        Text(stringResource(Res.string.feature_manual_database_import))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SuggestDatabase(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }

    val isPressed by interactionSource.collectIsPressedAsState()

    val cornerRadius by animateDpAsState(
        targetValue = if (isPressed) 28.dp else 12.dp,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    Surface(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            clip = true
            shape = RoundedCornerShape(cornerRadius)
        },
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null
            )
            Column {
                Text(
                    text = stringResource(Res.string.action_suggest_external_database),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(Res.string.description_suggest_external_database),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
