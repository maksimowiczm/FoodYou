package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.OpenFoodFactsPrivacyCard
import com.maksimowiczm.foodyou.app.ui.common.component.UsdaPrivacyCard
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FoodDatabaseScreen(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    FoodDatabaseScreen(
        uiState = uiState,
        onAllowOpenFoodFactsChange = viewModel::setAllowOpenFoodFacts,
        onAllowFoodDataCentralChange = viewModel::setAllowFoodDataCentral,
        onBack = onBack,
        onContinue = onContinue,
        modifier = modifier,
    )
}

@Composable
private fun FoodDatabaseScreen(
    uiState: OnboardingUiState,
    onAllowOpenFoodFactsChange: (Boolean) -> Unit,
    onAllowFoodDataCentralChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val fabHeight = 56.dp

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_food_database)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
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
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp).add(bottom = fabHeight + 16.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.description_food_database),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                OpenFoodFactsPrivacyCard(
                    selected = uiState.allowOpenFoodFacts,
                    onSelectedChange = onAllowOpenFoodFactsChange,
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                UsdaPrivacyCard(
                    selected = uiState.allowFoodDataCentral,
                    onSelectedChange = onAllowFoodDataCentralChange,
                )
            }
        }
    }
}

@Preview
@Composable
private fun FoodDatabaseScreenPreview() {
    PreviewFoodYouTheme {
        FoodDatabaseScreen(
            uiState = OnboardingUiState(),
            onAllowOpenFoodFactsChange = {},
            onAllowFoodDataCentralChange = {},
            onBack = {},
            onContinue = {},
        )
    }
}
