package com.maksimowiczm.foodyou.features.privacy

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
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.shared.ui.InteractionShapes
import com.maksimowiczm.foodyou.shared.ui.utility.LocalAppConfig
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OpenFoodFactsPrivacyCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    signedIn: Boolean,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    shapes: InteractionShapes,
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
        shapes = shapes,
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
                Chip(
                    onClick = {
                        if (signedIn) onLogout() else onLogin()
                    },
                    leadingIcon = {
                        if (signedIn)
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize),
                            )
                        else
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Login,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize),
                            )
                    },
                    label = {
                        if (signedIn) Text(stringResource(Res.string.action_sign_out))
                        else Text(stringResource(Res.string.action_sign_in))
                    },
                )
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
            signedIn = false,
            onLogin = {},
            onLogout = {},
            shapes = PrivacyCardDefaults.shapes(0, 1, true),
            onSelectedChange = {},
        )
    }
}

@Preview
@Composable
private fun OpenFoodFactsPrivacyCardSignedInPreview() {
    PreviewFoodYouTheme {
        OpenFoodFactsPrivacyCard(
            selected = true,
            signedIn = true,
            onLogin = {},
            onLogout = {},
            shapes = PrivacyCardDefaults.shapes(0, 1, true),
            onSelectedChange = {},
        )
    }
}
