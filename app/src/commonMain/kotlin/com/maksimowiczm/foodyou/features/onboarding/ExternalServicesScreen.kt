package com.maksimowiczm.foodyou.features.onboarding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.features.privacy.FoodDataCentralPrivacyCard
import com.maksimowiczm.foodyou.features.privacy.OpenFoodFactsPrivacyCard
import com.maksimowiczm.foodyou.features.privacy.PrivacyCardDefaults
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ExternalServicesScreen(
    state: OnboardingState,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onFoodDataCentralApiKey: () -> Unit,
    isOpenFoodFactsSignedIn: Boolean,
    onOpenFoodFactsLogin: () -> Unit,
    onOpenFoodFactsLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val fabHeight = 56.dp

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_external_services)) },
                subtitle = {
                    Text(stringResource(Res.string.description_external_services))
                },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Button(
                onClick = onContinue,
                shapes = ButtonDefaults.shapesFor(fabHeight),
                modifier = Modifier.widthIn(min = 250.dp),
                contentPadding = ButtonDefaults.contentPaddingFor(fabHeight),
            ) {
                Text(
                    text =
                        if (state.allowOpenFoodFacts || state.allowFoodDataCentral)
                            stringResource(Res.string.action_agree_and_continue)
                        else stringResource(Res.string.action_continue),
                    style = ButtonDefaults.textStyleFor(fabHeight),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp).add(bottom = fabHeight + 16.dp),
        ) {
            item { Spacer(Modifier.height(8.dp)) }
            item {
                OpenFoodFactsPrivacyCard(
                    selected = state.allowOpenFoodFacts,
                    onSelectedChange = { state.allowOpenFoodFacts = it },
                    signedIn = isOpenFoodFactsSignedIn,
                    onLogin = onOpenFoodFactsLogin,
                    onLogout = onOpenFoodFactsLogout,
                    shapes = PrivacyCardDefaults.shapes(0, 2, state.allowOpenFoodFacts),
                )
            }
            item { Spacer(Modifier.height(2.dp)) }
            item {
                FoodDataCentralPrivacyCard(
                    selected = state.allowFoodDataCentral,
                    onSelectedChange = { state.allowFoodDataCentral = it },
                    onApiKey = onFoodDataCentralApiKey,
                    shapes = PrivacyCardDefaults.shapes(1, 2, state.allowFoodDataCentral),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ExternalServicesScreenPreview() {
    PreviewFoodYouTheme {
        ExternalServicesScreen(
            state = rememberOnboardingState(),
            onBack = {},
            onContinue = {},
            onFoodDataCentralApiKey = {},
            isOpenFoodFactsSignedIn = false,
            onOpenFoodFactsLogin = {},
            onOpenFoodFactsLogout = {},
        )
    }
}
