package com.maksimowiczm.foodyou.app.ui.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyCard
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyCardDefaults
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyPolicyChip
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.toggle
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import com.maksimowiczm.foodyou.app.ui.fooddatacentral.FoodDataCentralPrivacyCard
import com.maksimowiczm.foodyou.app.ui.openfoodfacts.OpenFoodFactsPrivacyCard
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PrivacyScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val hapticFeedback = LocalHapticFeedback.current

    val viewModel: PrivacyViewModel = koinViewModel()
    val foodSearchPreferences by viewModel.foodSearchPreferences.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_privacy)) },
                subtitle = { Text(stringResource(Res.string.description_privacy)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            item { FoodYouPrivacyCard(shape = PrivacyCardDefaults.shape(0, 3, true)) }
            item {
                OpenFoodFactsPrivacyCard(
                    selected = foodSearchPreferences.allowOpenFoodFacts,
                    onSelectedChange = {
                        viewModel.setFoodSearchPreferences(allowOpenFoodFacts = it)
                        hapticFeedback.toggle(it)
                    },
                    shapes =
                        PrivacyCardDefaults.shapes(1, 3, foodSearchPreferences.allowOpenFoodFacts),
                )
            }
            item {
                FoodDataCentralPrivacyCard(
                    selected = foodSearchPreferences.allowFoodDataCentralUSDA,
                    onSelectedChange = {
                        viewModel.setFoodSearchPreferences(allowFoodDataCentralUSDA = it)
                        hapticFeedback.toggle(it)
                    },
                    shapes =
                        PrivacyCardDefaults.shapes(
                            2,
                            3,
                            foodSearchPreferences.allowFoodDataCentralUSDA,
                        ),
                )
            }
        }
    }
}

@Composable
private fun FoodYouPrivacyCard(
    shape: Shape,
    modifier: Modifier = Modifier,
    privacyPolicyUri: String = LocalAppConfig.current.privacyPolicyUri,
) {
    val uriHandler = LocalUriHandler.current

    PrivacyCard(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_logo),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                }
                Text(
                    text = stringResource(Res.string.app_name),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
            }
        },
        shape = shape,
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
    ) {
        Column {
            Text(stringResource(Res.string.onboarding_privacy_tip))
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrivacyPolicyChip(onClick = { uriHandler.openUri(privacyPolicyUri) })
            }
        }
    }
}
