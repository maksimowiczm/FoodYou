package com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.openfoodfacts.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.core.feature.system.data.model.Country
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoodDatabaseSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FoodDatabaseSettingsViewModel = koinViewModel()
) {
    val openFoodFactsSettings by viewModel.openFoodFactsSettings.collectAsStateWithLifecycle()

    FoodDatabaseSettingsScreen(
        onBack = onBack,
        openFoodFactsSettings = openFoodFactsSettings,
        openFoodFactsToggle = viewModel::onOpenFoodFactsToggle,
        openFoodFactsCountrySelected = viewModel::onOpenFoodFactsCountrySelected,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodDatabaseSettingsScreen(
    onBack: () -> Unit,
    openFoodFactsSettings: OpenFoodFactsSettings,
    openFoodFactsToggle: (Boolean) -> Unit,
    openFoodFactsCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.headline_food_database))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
            LazyColumn(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                item {
                    OpenFoodFactsSettings(
                        settings = openFoodFactsSettings,
                        onToggle = openFoodFactsToggle,
                        onCountrySelected = openFoodFactsCountrySelected
                    )
                }
                item {
                    Spacer(
                        Modifier.windowInsetsBottomHeight(
                            WindowInsets.systemBars.union(WindowInsets.displayCutout)
                        )
                    )
                }
            }
        }
    }
}
