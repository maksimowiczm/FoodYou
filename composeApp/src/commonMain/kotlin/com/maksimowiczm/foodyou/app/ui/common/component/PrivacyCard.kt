package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ChipColors
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import com.maksimowiczm.foodyou.app.ui.food.UpdateUsdaApiKeyDialog
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrivacyCard(
    selected: Boolean,
    title: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PrivacyCardDefaults.contentPadding,
    shape: Shape = PrivacyCardDefaults.shape,
    color: Color = PrivacyCardDefaults.color(selected),
    contentColor: Color = PrivacyCardDefaults.contentColor(selected),
    content: @Composable PrivacyCardScope.() -> Unit,
) {
    val scope = remember(selected) { PrivacyCardScope(selected) }

    val inner =
        @Composable {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                Column(Modifier.padding(contentPadding)) {
                    title()
                    Spacer(Modifier.height(8.dp))
                    content(scope)
                }
            }
        }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        content = inner,
    )
}

@Composable
fun PrivacyCard(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PrivacyCardDefaults.contentPadding,
    shape: Shape = PrivacyCardDefaults.shape,
    color: Color = PrivacyCardDefaults.color(true),
    contentColor: Color = PrivacyCardDefaults.contentColor(true),
    scope: PrivacyCardScope = remember { PrivacyCardScope(true) },
    content: @Composable PrivacyCardScope.() -> Unit,
) {
    val inner =
        @Composable {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                Column(Modifier.padding(contentPadding)) {
                    title()
                    Spacer(Modifier.height(8.dp))
                    content(scope)
                }
            }
        }

    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        content = inner,
    )
}

class PrivacyCardScope(val selected: Boolean) {
    @Composable
    fun Chip(
        onClick: () -> Unit,
        label: @Composable () -> Unit,
        leadingIcon: @Composable (() -> Unit)? = null,
        modifier: Modifier = Modifier,
        colors: ChipColors =
            AssistChipDefaults.assistChipColors(
                labelColor = PrivacyCardDefaults.contentColor(selected)
            ),
        border: BorderStroke? =
            AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor =
                    if (selected) MaterialTheme.colorScheme.inversePrimary
                    else MaterialTheme.colorScheme.outlineVariant,
            ),
    ) {
        AssistChip(
            onClick = onClick,
            modifier = modifier,
            leadingIcon = leadingIcon,
            label = label,
            colors = colors,
            border = border,
        )
    }
}

object PrivacyCardDefaults {
    val contentPadding = PaddingValues(16.dp)

    val shape: Shape
        @ReadOnlyComposable @Composable get() = MaterialTheme.shapes.medium

    @Composable
    fun color(selected: Boolean): Color =
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainer

    @Composable fun contentColor(selected: Boolean): Color = contentColorFor(color(selected))
}

@Composable
fun PrivacyCardScope.PrivacyPolicyChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Chip(
        onClick = onClick,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.PrivacyTip,
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize),
            )
        },
        label = { Text(stringResource(Res.string.headline_privacy_policy)) },
    )
}

@Composable
fun PrivacyCardScope.TermsOfUseChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Chip(
        onClick = onClick,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize),
            )
        },
        label = { Text(stringResource(Res.string.headline_terms_of_use)) },
    )
}

@Composable
fun OpenFoodFactsPrivacyCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val appConfig = LocalAppConfig.current

    PrivacyCard(
        selected = selected,
        title = {
            Row(
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
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        onClick = { onSelectedChange(!selected) },
    ) {
        Column {
            Text(text = stringResource(Res.string.description_open_food_facts))
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TermsOfUseChip(
                    onClick = { uriHandler.openUri(appConfig.openFoodFactsTermsOfUseUri) }
                )
                PrivacyPolicyChip(
                    onClick = { uriHandler.openUri(appConfig.openFoodFactsPrivacyPolicyUri) }
                )
            }
        }
    }
}

@Composable
fun FoodDataCentralPrivacyCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val appConfig = LocalAppConfig.current

    var showApiKeyDialog by rememberSaveable { mutableStateOf(false) }
    if (showApiKeyDialog) {
        UpdateUsdaApiKeyDialog(
            onDismissRequest = { showApiKeyDialog = false },
            onSave = { showApiKeyDialog = false },
            autoFocus = true,
        )
    }

    PrivacyCard(
        selected = selected,
        title = {
            Row(
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
                    text = stringResource(Res.string.headline_fooddata_central),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Checkbox(checked = selected, onCheckedChange = null)
                }
            }
        },
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        onClick = { onSelectedChange(!selected) },
    ) {
        Column {
            Text(text = stringResource(Res.string.description_fooddata_central))
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrivacyPolicyChip(
                    onClick = { uriHandler.openUri(appConfig.foodDataCentralPrivacyPolicyUri) }
                )
                Chip(
                    onClick = { showApiKeyDialog = true },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Key,
                            contentDescription = null,
                            modifier = Modifier.size(AssistChipDefaults.IconSize),
                        )
                    },
                    label = { Text(stringResource(Res.string.headline_api_key)) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun OpenFoodFactsPrivacyCardPreview() {
    PreviewFoodYouTheme { OpenFoodFactsPrivacyCard(selected = true, onSelectedChange = {}) }
}

@Preview
@Composable
private fun FoodDataCentralPrivacyCardPreview() {
    PreviewFoodYouTheme { FoodDataCentralPrivacyCard(selected = false, onSelectedChange = {}) }
}
