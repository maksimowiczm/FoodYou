package com.maksimowiczm.foodyou.app.ui.auth0

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.InteractiveLogo
import com.maksimowiczm.foodyou.app.ui.common.theme.brandTypography
import com.maksimowiczm.foodyou.common.auth.SessionRepository
import com.maksimowiczm.foodyou.common.compose.extension.add
import com.maksimowiczm.foodyou.common.config.AppConfig
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun Auth0LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sessionRepository: SessionRepository = koinInject()
    val appConfig: AppConfig = koinInject()
    val uriHandler = LocalUriHandler.current

    val session by sessionRepository.observeSession().collectAsStateWithLifecycle(null)

    if (session != null) {
        Text(text = session.toString(), modifier = Modifier.safeContentPadding())
        return
    }

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }
    val onLogin =
        rememberAuth0OnLogin(
            onLoginSuccess = {
                isLoading = false
                isError = false
                onLoginSuccess()
            },
            onLoginError = {
                isError = true
                isLoading = false
            },
            onCancel = { isLoading = false },
        )

    Auth0LoginScreen(
        isLoading = isLoading,
        isError = isError,
        onBack = onBack,
        onLogin = {
            if (!isLoading) {
                isLoading = true
                isError = false
                onLogin()
            }
        },
        onTermsOfService = { uriHandler.openUri(appConfig.termsOfServiceUrl) },
        onPrivacyPolicy = { uriHandler.openUri(appConfig.privacyPolicyUrl) },
        modifier = modifier,
    )
}

@Composable
internal expect fun rememberAuth0OnLogin(
    onLoginSuccess: () -> Unit,
    onLoginError: (Throwable) -> Unit,
    onCancel: () -> Unit,
): () -> Unit

@Composable
private fun Auth0LoginScreen(
    isLoading: Boolean,
    isError: Boolean,
    onBack: () -> Unit,
    onLogin: () -> Unit,
    onTermsOfService: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fabHeight = remember { ButtonDefaults.LargeContainerHeight }

    Scaffold(
        modifier = modifier,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Button(
                onClick = { if (!isLoading) onLogin() },
                enabled = !isLoading,
                shapes = ButtonDefaults.shapesFor(fabHeight),
                contentPadding = ButtonDefaults.contentPaddingFor(fabHeight),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Login,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.iconSizeFor(fabHeight)),
                )
                Spacer(Modifier.width(ButtonDefaults.iconSpacingFor(fabHeight)))
                Text(
                    text = stringResource(Res.string.action_sign_in),
                    style = ButtonDefaults.textStyleFor(fabHeight),
                )
            }
        },
    ) { paddingValues ->
        // Padding according to the Material Design App bars guidelines
        // https://m3.material.io/components/app-bars/specs
        val insets = TopAppBarDefaults.windowInsets
        val padding = PaddingValues(top = 8.dp, start = 4.dp)

        Box(
            modifier =
                Modifier.windowInsetsPadding(insets)
                    .consumeWindowInsets(insets)
                    .padding(padding)
                    .zIndex(100f)
        ) {
            FilledIconButton(
                onClick = onBack,
                colors =
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.action_go_back),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues.add(bottom = 16.dp + fabHeight),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { InteractiveLogo() }

            item {
                Text(text = stringResource(Res.string.app_name), style = brandTypography.brandName)
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    FlowRow(Modifier.padding(horizontal = 32.dp)) {
                        AssistChip(
                            onClick = onTermsOfService,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                                )
                            },
                            label = { Text(stringResource(Res.string.headline_terms_of_service)) },
                        )
                        Spacer(Modifier.width(8.dp))
                        AssistChip(
                            onClick = onPrivacyPolicy,
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

                    Spacer(Modifier.height(8.dp))

                    if (isLoading) {
                        LinearWavyProgressIndicator(Modifier.fillMaxWidth())
                        Spacer(Modifier.height(16.dp))
                    } else {
                        Spacer(Modifier.height(26.dp))
                    }

                    Text(
                        text = stringResource(Res.string.description_sign_in_auth0),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )

                    AnimatedVisibility(
                        visible = isError,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    ) {
                        Column {
                            Spacer(Modifier.height(8.dp))
                            Card(
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                    )
                                    Text(
                                        text =
                                            stringResource(
                                                Res.string.error_there_was_an_error_while_signing_in
                                            ),
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
