package com.maksimowiczm.foodyou.app.ui.theme

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
import com.maksimowiczm.foodyou.common.compose.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ThemeScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: ThemeSettingsViewModel = koinViewModel()

    val themeSettings = viewModel.themeSettings.collectAsStateWithLifecycle().value
    val nutrientsColors = viewModel.nutrientsColors.collectAsStateWithLifecycle().value

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    if (themeSettings == null || nutrientsColors == null) {
        // TODO loading state
    } else {
        Scaffold(
            modifier = modifier,
            topBar = {
                LargeFlexibleTopAppBar(
                    title = { Text(stringResource(Res.string.headline_appearance)) },
                    navigationIcon = { ArrowBackIconButton(onBack) },
                    scrollBehavior = scrollBehavior,
                )
            },
            content = { paddingValues ->
                ThemeScreenContent(
                    themeSettings = themeSettings,
                    onThemeSettingsChange = viewModel::updateThemeSettings,
                    nutrientsColors = nutrientsColors,
                    onNutrientsColorsChange = viewModel::updateNutrientsColors,
                    contentPadding = paddingValues.add(vertical = 8.dp),
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                )
            },
        )
    }
}
