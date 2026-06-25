package com.maksimowiczm.foodyou.app.ui.openfoodfacts

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyCard
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyPolicyChip
import com.maksimowiczm.foodyou.app.ui.common.component.TermsOfUseChip
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsLoginService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.hasCredentials
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun OpenFoodFactsPrivacyCard(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    repository: OpenFoodFactsSettingsRepository = koinInject(),
    service: OpenFoodFactsLoginService = koinInject(),
) {
    val uriHandler = LocalUriHandler.current
    val appConfig = LocalAppConfig.current

    val hasCredentials =
        remember(repository) { repository.hasCredentials() }.collectAsStateWithLifecycle(null).value

    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
    if (showLoginDialog) {
        OpenFoodFactsLoginDialog(
            onDismissRequest = { showLoginDialog = false },
            onSave = { login, password ->
                runBlocking {
                    repository.update { it.copy(login = login, password = password) }
                    showLoginDialog = false
                }
            },
            service = service,
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
                Chip(
                    onClick = {
                        if (hasCredentials == true)
                            runBlocking {
                                repository.update { it.copy(login = null, password = null) }
                            }
                        else showLoginDialog = true
                    },
                    enabled = hasCredentials != null,
                    leadingIcon = {
                        if (hasCredentials == true)
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
                        if (hasCredentials == true) Text(stringResource(Res.string.action_sign_out))
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
            onSelectedChange = {},
            repository =
                object : OpenFoodFactsSettingsRepository {
                    override fun observe(): Flow<OpenFoodFactsSettings> =
                        flowOf(OpenFoodFactsSettings())

                    override suspend fun save(settings: OpenFoodFactsSettings) = Unit

                    override suspend fun update(
                        transform: (OpenFoodFactsSettings) -> OpenFoodFactsSettings
                    ) = Unit
                },
            service = OpenFoodFactsLoginService { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun OpenFoodFactsPrivacyCardSignedInPreview() {
    PreviewFoodYouTheme {
        OpenFoodFactsPrivacyCard(
            selected = true,
            onSelectedChange = {},
            repository =
                object : OpenFoodFactsSettingsRepository {
                    override fun observe(): Flow<OpenFoodFactsSettings> =
                        flowOf(OpenFoodFactsSettings(login = "login", password = "password"))

                    override suspend fun save(settings: OpenFoodFactsSettings) = Unit

                    override suspend fun update(
                        transform: (OpenFoodFactsSettings) -> OpenFoodFactsSettings
                    ) = Unit
                },
            service = OpenFoodFactsLoginService { _, _ -> },
        )
    }
}
