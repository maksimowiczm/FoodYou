package com.maksimowiczm.foodyou.app.ui.fooddatacentral

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyCard
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyCardShapes
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyPolicyChip
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FoodDataCentralPrivacyCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    shapes: PrivacyCardShapes,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val appConfig = LocalAppConfig.current

    var showApiKeyDialog by rememberSaveable { mutableStateOf(false) }
    if (showApiKeyDialog) {
        UpdateFoodDataCentralApiKeyDialog(
            onDismissRequest = { showApiKeyDialog = false },
            onSave = { showApiKeyDialog = false },
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
        shapes = shapes,
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
