package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.app.ui.common.component.InteractiveLogo
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyPolicyChip
import com.maksimowiczm.foodyou.app.ui.common.component.TermsOfUseChip
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.theme.brandTypography
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun BeforeYouStartScreen(
    viewModel: OnboardingViewModel,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val appConfig: AppConfig = koinInject()
    val uriHandler = LocalUriHandler.current

    BeforeYouStartScreen(
        uiState = uiState,
        onSetAllowFoodYouServices = { viewModel.setAllowFoodYouServices(it) },
        onContinue = onContinue,
        onTermsOfUse = { uriHandler.openUri(appConfig.termsOfUseUri) },
        onPrivacyPolicy = { uriHandler.openUri(appConfig.privacyPolicyUri) },
        modifier = modifier,
    )
}

@Composable
private fun BeforeYouStartScreen(
    uiState: OnboardingUiState,
    onSetAllowFoodYouServices: (Boolean) -> Unit,
    onContinue: () -> Unit,
    onTermsOfUse: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val fabHeight = ButtonDefaults.LargeContainerHeight

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.headline_before_you_start),
                        style = MaterialTheme.typography.displaySmall,
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Button(
                onClick = onContinue,
                shapes = ButtonDefaults.shapesFor(fabHeight),
                contentPadding = ButtonDefaults.contentPaddingFor(fabHeight),
            ) {
                Text(
                    text = stringResource(Res.string.action_agree_and_continue),
                    style = ButtonDefaults.textStyleFor(fabHeight),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(bottom = fabHeight + 16.dp).add(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                ) {
                    InteractiveLogo(
                        modifier =
                            Modifier.padding(horizontal = 64.dp)
                                .widthIn(max = 350.dp)
                                .aspectRatio(1f)
                                .fillMaxSize()
                    )
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = brandTypography.brandName,
                    )
                }
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TermsOfUseChip(onTermsOfUse)
                    PrivacyPolicyChip(onPrivacyPolicy)
                }
            }
            item {
                Text(
                    text = stringResource(Res.string.onboarding_privacy_tip),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item {
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .clickable { onSetAllowFoodYouServices(!uiState.allowFoodYouServices) }
                            .padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = uiState.allowFoodYouServices, onCheckedChange = null)
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = stringResource(Res.string.onboarding_allow_food_you_services),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun BeforeYouStartScreenPreview() {
    PreviewFoodYouTheme {
        BeforeYouStartScreen(
            uiState = OnboardingUiState(allowFoodYouServices = false),
            onSetAllowFoodYouServices = {},
            onContinue = {},
            onTermsOfUse = {},
            onPrivacyPolicy = {},
        )
    }
}
