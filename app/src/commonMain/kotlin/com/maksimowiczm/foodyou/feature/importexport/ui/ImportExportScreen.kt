package com.maksimowiczm.foodyou.feature.importexport.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.component.ExperimentalFeatureCard
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal expect fun ImportExportScreen(onBack: () -> Unit, modifier: Modifier = Modifier)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ImportExportScreenImpl(
    onBack: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(Res.string.headline_import_and_export)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = paddingValues
        ) {
            item {
                ExperimentalFeatureCard()
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    SettingsListItem(
                        headlineContent = {
                            Text(stringResource(Res.string.action_import_food_products))
                        },
                        supportingContent = {
                            Text(stringResource(Res.string.action_import_food_products_from_csv))
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.FileOpen,
                                contentDescription = null
                            )
                        },
                        onClick = onImport,
                        shape = MaterialTheme.shapes.medium.copy(
                            bottomEnd = CornerSize(0),
                            bottomStart = CornerSize(0)
                        )
                    )
                    SettingsListItem(
                        headlineContent = {
                            Text(stringResource(Res.string.action_export_food_products))
                        },
                        supportingContent = {
                            Text(stringResource(Res.string.action_export_food_products_to_csv))
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.FilePresent,
                                contentDescription = null
                            )
                        },
                        onClick = onExport,
                        shape = MaterialTheme.shapes.medium.copy(
                            topEnd = CornerSize(0),
                            topStart = CornerSize(0)
                        )
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null
                        )

                        Text(
                            text = stringResource(Res.string.description_import_and_export_hint),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsListItem(
    headlineContent: @Composable () -> Unit,
    supportingContent: (@Composable () -> Unit)?,
    leadingContent: (@Composable () -> Unit)?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingContent?.invoke()

            Column {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyLarge
                ) {
                    headlineContent()
                }

                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    supportingContent?.invoke()
                }
            }
        }
    }
}
