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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import com.maksimowiczm.foodyou.app.ui.database.externaldatabases.OpenFoodFactsLoginDialog
import com.maksimowiczm.foodyou.app.ui.database.externaldatabases.UpdateUsdaApiKeyDialog
import com.maksimowiczm.foodyou.food.domain.repository.OpenFoodFactsCredentialsRepository
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                Column(Modifier.padding(contentPadding)) {
                    title()
                    Spacer(Modifier.height(8.dp))
                    content()
                }
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
    termsOfUseUri: String = LocalAppConfig.current.openFoodFactsTermsOfUseUri,
    privacyPolicyUri: String = LocalAppConfig.current.openFoodFactsPrivacyPolicyUri,
) {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val credentialsRepository: OpenFoodFactsCredentialsRepository = koinInject()

    val hasCredentials by
        remember { credentialsRepository.hasCredentials() }.collectAsStateWithLifecycle(false)

    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
    if (showLoginDialog) {
        OpenFoodFactsLoginDialog(
            onDismissRequest = { showLoginDialog = false },
            onSave = { showLoginDialog = false },
        )
    }

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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TermsOfUseChip(onClick = { uriHandler.openUri(termsOfUseUri) })
                PrivacyPolicyChip(onClick = { uriHandler.openUri(privacyPolicyUri) })
                AssistChip(
                    onClick = {
                        if (hasCredentials) scope.launch { credentialsRepository.clear() }
                        else showLoginDialog = true
                    },
                    leadingIcon = {
                        if (hasCredentials) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize),
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Login,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize),
                            )
                        }
                    },
                    label = {
                        if (hasCredentials) {
                            Text(stringResource(Res.string.action_sign_out))
                        } else {
                            Text(stringResource(Res.string.action_sign_in))
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun UsdaPrivacyCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    privacyPolicyUri: String = LocalAppConfig.current.foodDataCentralPrivacyPolicyUri,
) {
    val uriHandler = LocalUriHandler.current
    var showApiKeyDialog by rememberSaveable { mutableStateOf(false) }
    if (showApiKeyDialog) {
        UpdateUsdaApiKeyDialog(
            onDismissRequest = { showApiKeyDialog = false },
            onSave = { showApiKeyDialog = false },
        )
    }

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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrivacyPolicyChip(onClick = { uriHandler.openUri(privacyPolicyUri) })
                AssistChip(
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
