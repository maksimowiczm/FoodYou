package com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.data.model.Country
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module

@Composable
fun OpenFoodFactsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OpenFoodFactsSettingsViewModel = koinInject()
) {
    val settings by viewModel.openFoodFactsSettings.collectAsStateWithLifecycle()

    OpenFoodFactsSettingsScreen(
        settings = settings,
        onToggle = viewModel::onOpenFoodFactsToggle,
        onCountrySelect = viewModel::onOpenFoodFactsCountrySelected,
        onCacheClear = viewModel::onCacheClear,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OpenFoodFactsSettingsScreen(
    settings: OpenFoodFactsSettings,
    onToggle: (Boolean) -> Unit,
    onCountrySelect: (Country) -> Unit,
    onCacheClear: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = settings is OpenFoodFactsSettings.Enabled

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        .exclude(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
        .exclude(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

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
        },
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {
            item {
                Text(
                    text = stringResource(Res.string.headline_open_food_facts),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .horizontalDisplayCutoutPadding(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            item {
                OpenFoodFactsDescription(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalDisplayCutoutPadding()
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
                    modifier = Modifier
                        .clickable { onToggle(!enabled) }
                        .horizontalDisplayCutoutPadding(),
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
                        settings = settings,
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
    val linkColor = MaterialTheme.colorScheme.tertiary

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(stringResource(Res.string.headline_open_food_facts))
        }
        append(" " + stringResource(Res.string.description_open_food_facts))
        withLink(
            LinkAnnotation.Url(
                url = stringResource(Res.string.link_open_food_facts),
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = linkColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            )
        ) {
            append(" " + stringResource(Res.string.action_read_more))
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = annotatedString,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )

                val disclaimer = buildAnnotatedString {
                    append(stringResource(Res.string.open_food_facts_disclaimer))
                    withLink(
                        LinkAnnotation.Url(
                            url = stringResource(Res.string.link_open_food_facts_terms_of_use),
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = linkColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        )
                    ) {
                        append(" " + stringResource(Res.string.action_see_terms_of_use))
                    }
                }

                Text(
                    text = disclaimer,
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun OpenFoodFactsContent(
    onCountrySelect: (Country) -> Unit,
    settings: OpenFoodFactsSettings.Enabled,
    onCacheClear: () -> Unit,
    modifier: Modifier = Modifier,
    countryFlag: CountryFlag = koinInject()
) {
    var showCountryPicker by rememberSaveable { mutableStateOf(false) }

    if (showCountryPicker) {
        CountryPickerDialog(
            onDismissRequest = { showCountryPicker = false },
            availableCountries = settings.availableCountries,
            countryFlag = countryFlag,
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
                .clickable { showCountryPicker = true }
                .horizontalDisplayCutoutPadding(),
            headlineContent = {
                Text(
                    text = stringResource(Res.string.action_select_country)
                )
            },
            supportingContent = {
                Text(
                    text = settings.country.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingContent = {
                countryFlag(
                    country = settings.country,
                    modifier = Modifier.width(52.dp)
                )
            }
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
        modifier = modifier
            .clickable { showDialog = true }
            .horizontalDisplayCutoutPadding(),
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

@Preview
@Composable
private fun OpenFoodFactsSettingsDisabledPreview() {
    FoodYouTheme {
        OpenFoodFactsSettingsScreen(
            settings = OpenFoodFactsSettings.Disabled,
            onToggle = {},
            onCountrySelect = {},
            onCacheClear = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
private fun OpenFoodFactsSettingsPreview() {
    val countryFlag = CountryFlag { c, modifier ->
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = c.code)
        }
    }

    KoinApplication(
        application = { modules(module { single { countryFlag } }) }
    ) {
        FoodYouTheme {
            OpenFoodFactsSettingsScreen(
                settings = OpenFoodFactsSettings.Enabled(
                    country = Country.Poland,
                    availableCountries = listOf(Country.Poland, Country.UnitedStates)
                ),
                onToggle = {},
                onCountrySelect = {},
                onCacheClear = {},
                onBack = {}
            )
        }
    }
}

@Preview
@Composable
private fun ClearCacheDialogPreview() {
    FoodYouTheme {
        ClearCacheDialog(
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}
