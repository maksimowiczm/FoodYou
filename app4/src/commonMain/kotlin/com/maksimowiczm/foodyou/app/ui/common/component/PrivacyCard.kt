package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.description_food_data_central_usda
import foodyou.app.generated.resources.description_open_food_facts
import foodyou.app.generated.resources.headline_food_data_central_usda
import foodyou.app.generated.resources.headline_open_food_facts
import foodyou.app.generated.resources.openfoodfacts_logo
import foodyou.app.generated.resources.usda_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun PrivacyCard(
    title: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
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

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        content = inner,
    )
}

@Composable
fun OpenFoodFactsPrivacyCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    termsOfUseUri: String = koinInject<AppConfig>().openFoodFactsTermsOfUseUri,
    privacyPolicyUri: String = koinInject<AppConfig>().openFoodFactsPrivacyPolicyUri,
) {
    val uriHandler = LocalUriHandler.current

    PrivacyCard(
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
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        onClick = { onSelectedChange(!selected) },
    ) {
        Column {
            Text(
                text = stringResource(Res.string.description_open_food_facts),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TermsOfUseChip(onClick = { uriHandler.openUri(termsOfUseUri) })
                PrivacyPolicyChip(onClick = { uriHandler.openUri(privacyPolicyUri) })
            }
        }
    }
}

@Composable
fun UsdaPrivacyCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    privacyPolicyUri: String = koinInject<AppConfig>().foodDataCentralPrivacyPolicyUri,
) {
    val uriHandler = LocalUriHandler.current

    PrivacyCard(
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
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        onClick = { onSelectedChange(!selected) },
    ) {
        Column {
            Text(
                text = stringResource(Res.string.description_food_data_central_usda),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrivacyPolicyChip(onClick = { uriHandler.openUri(privacyPolicyUri) })
            }
        }
    }
}

@Preview
@Composable
private fun OpenFoodFactsPrivacyCardPreview() {
    PreviewFoodYouTheme {
        OpenFoodFactsPrivacyCard(
            selected = true,
            onSelectedChange = {},
            termsOfUseUri = "",
            privacyPolicyUri = "",
        )
    }
}

@Preview
@Composable
private fun UsdaPrivacyCardPreview() {
    PreviewFoodYouTheme {
        UsdaPrivacyCard(selected = false, onSelectedChange = {}, privacyPolicyUri = "")
    }
}
