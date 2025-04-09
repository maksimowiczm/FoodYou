package com.maksimowiczm.foodyou.feature.openfoodfacts.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.util.Country
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun OpenFoodFactsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OpenFoodFactsSettingsViewModel = koinInject()
) {
    val settings by viewModel.openFoodFactsSettings.collectAsStateWithLifecycle()

    OpenFoodFactsSettingsScreen(
        settings = settings,
        onToggle = remember(viewModel) { viewModel::onOpenFoodFactsToggle },
        onCountrySelect = remember(viewModel) { viewModel::onOpenFoodFactsCountrySelected },
        onGlobalDatabase = remember(viewModel) { viewModel::onGlobalDatabase },
        onDeleteUnusedProducts = remember(viewModel) { viewModel::onDeleteUnusedProducts },
        onCacheClear = remember(viewModel) { viewModel::onCacheClear },
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OpenFoodFactsSettingsScreen(
    settings: OpenFoodFactsSettings,
    onToggle: (Boolean) -> Unit,
    onCountrySelect: (Country?) -> Unit,
    onGlobalDatabase: (Boolean) -> Unit,
    onDeleteUnusedProducts: () -> Unit,
    onCacheClear: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = settings is OpenFoodFactsSettings.Enabled

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.headline_remote_food_database))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.headline_open_food_facts),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            item {
                OpenFoodFactsDescription(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.action_use_open_food_facts)
                        )
                    },
                    modifier = Modifier.clickable { onToggle(!enabled) },
                    trailingContent = {
                        Switch(
                            checked = enabled,
                            onCheckedChange = onToggle
                        )
                    }
                )
            }

            item {
                if (settings is OpenFoodFactsSettings.Enabled) {
                    OpenFoodFactsContent(
                        onCountrySelect = onCountrySelect,
                        onGlobalDatabase = onGlobalDatabase,
                        settings = settings,
                        onDeleteUnusedProducts = onDeleteUnusedProducts,
                        onCacheClear = onCacheClear
                    )
                }
            }

            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    }
}

@Composable
private fun OpenFoodFactsDescription(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = rememberDescriptionString(),
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )

                Text(
                    text = rememberDisclaimerString(),
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun rememberDescriptionString(): AnnotatedString {
    val bodyMedium = MaterialTheme.typography.bodyMedium
    val headline = stringResource(Res.string.headline_open_food_facts)
    val description = stringResource(Res.string.description_open_food_facts)
    val link = stringResource(Res.string.link_open_food_facts)
    val linkColor = MaterialTheme.colorScheme.tertiary
    val readMore = stringResource(Res.string.action_read_more)

    return remember {
        buildAnnotatedString {
            withStyle(bodyMedium.toSpanStyle()) {
                withStyle(bodyMedium.copy(fontWeight = FontWeight.Bold).toSpanStyle()) {
                    append(headline)
                }
                append(" $description")
                withLink(
                    LinkAnnotation.Url(
                        url = link,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    )
                ) {
                    append(" $readMore")
                }
            }
        }
    }
}

@Composable
private fun rememberDisclaimerString(): AnnotatedString {
    val disclaimer = stringResource(Res.string.open_food_facts_disclaimer)
    val termsOfUse = stringResource(Res.string.link_open_food_facts_terms_of_use)
    val termsOfUseText = stringResource(Res.string.action_see_terms_of_use)
    val linkColor = MaterialTheme.colorScheme.tertiary

    return remember {
        buildAnnotatedString {
            append(disclaimer)
            withLink(
                LinkAnnotation.Url(
                    url = termsOfUse,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = linkColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                )
            ) {
                append(" $termsOfUseText")
            }
        }
    }
}

@Composable
private fun OpenFoodFactsContent(
    onCountrySelect: (Country) -> Unit,
    onGlobalDatabase: (Boolean) -> Unit,
    settings: OpenFoodFactsSettings.Enabled,
    onDeleteUnusedProducts: () -> Unit,
    onCacheClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCountryPicker by rememberSaveable { mutableStateOf(false) }

    if (showCountryPicker) {
        CountryPickerDialog(
            onDismissRequest = { showCountryPicker = false },
            availableCountries = settings.availableCountries,
            onCountrySelect = { country ->
                onCountrySelect(country)
                showCountryPicker = false
            }
        )
    }

    Column(
        modifier = modifier
    ) {
        ListItem(
            modifier = Modifier
                .requiredHeightIn(min = 64.dp)
                .clickable { showCountryPicker = true },
            headlineContent = {
                Text(
                    text = stringResource(Res.string.action_select_country)
                )
            },
            supportingContent = {
                if (settings.country == null) {
                    Text(
                        text = stringResource(Res.string.headline_filter_foods_by_country)
                    )
                } else {
                    Text(
                        text = settings.country.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            trailingContent = {
                if (settings.country != null) {
                    CountryFlag(
                        country = settings.country,
                        modifier = Modifier.width(52.dp)
                    )
                }
            }
        )
        Card(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )

                Text(
                    text = stringResource(Res.string.description_use_global_database),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify
                )
            }
        }
        ListItem(
            modifier = Modifier
                .requiredHeightIn(min = 64.dp)
                .clickable { onGlobalDatabase(settings.country != null) },
            headlineContent = {
                Text(stringResource(Res.string.headline_use_global_database))
            },
            trailingContent = {
                Switch(
                    checked = settings.country == null,
                    onCheckedChange = onGlobalDatabase
                )
            }
        )
        HorizontalDivider()
        DeleteUnusedProducts(
            onDelete = onDeleteUnusedProducts,
            modifier = Modifier.requiredHeightIn(min = 64.dp)
        )
        ClearCacheItem(
            onCacheClear = onCacheClear,
            modifier = Modifier.requiredHeightIn(min = 64.dp)
        )
    }
}

@Composable
private fun ClearCacheItem(onCacheClear: () -> Unit, modifier: Modifier = Modifier) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    if (showDialog) {
        ClearCacheDialog(
            onDismissRequest = { showDialog = false },
            onConfirm = {
                onCacheClear()
                showDialog = false
            }
        )
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(Res.string.headline_clear_cache)
            )
        },
        modifier = modifier.clickable { showDialog = true },
        supportingContent = {
            Text(
                text = stringResource(Res.string.description_open_food_facts_clear_query_cache)
            )
        }
    )
}

@Composable
private fun ClearCacheDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    var count by rememberSaveable { mutableIntStateOf(5) }

    LaunchedEffect(Unit) {
        while (count > 0) {
            delay(1000)
            count--
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(Res.string.headline_clear_cache)
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.description_open_food_facts_clear_cache)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = count == 0,
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = stringResource(Res.string.action_clear)
                )
                Spacer(Modifier.width(1.dp))
                Text(
                    text = if (count > 0) " ($count)" else ""
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = stringResource(Res.string.action_cancel)
                )
            }
        }
    )
}

@Composable
private fun DeleteUnusedProducts(onDelete: () -> Unit, modifier: Modifier = Modifier) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            },
            title = {
                Text(
                    text = stringResource(Res.string.action_delete_unused_products)
                )
            },
            text = {
                Text(
                    stringResource(Res.string.description_delete_unused_open_food_facts_products)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.action_delete)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        text = stringResource(Res.string.action_cancel)
                    )
                }
            }
        )
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(Res.string.action_delete_unused_products)
            )
        },
        modifier = modifier.clickable { showDialog = true },
        supportingContent = {
            Text(
                stringResource(Res.string.description_delete_unused_open_food_facts_products_short)
            )
        }
    )
}
