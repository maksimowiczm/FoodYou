package com.maksimowiczm.foodyou.app.ui.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LargeFlexibleTopAppBar
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PrivacyScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: PrivacyViewModel = koinViewModel()
    val privacySettings by viewModel.privacySettings.collectAsStateWithLifecycle()
    val foodSearchPreferences by viewModel.foodSearchPreferences.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Privacy") },
                subtitle = { Text("Control your personal information") },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                OpenFoodFactsPrivacyCard(
                    selected = foodSearchPreferences.allowOpenFoodFacts,
                    onSelectedChange = {
                        viewModel.setFoodSearchPreferences(allowOpenFoodFacts = it)
                    },
                )
            }
            item {
                UsdaPrivacyCard(
                    selected = foodSearchPreferences.allowFoodDataCentralUSDA,
                    onSelectedChange = {
                        viewModel.setFoodSearchPreferences(allowFoodDataCentralUSDA = it)
                    },
                )
            }
        }
    }
}
