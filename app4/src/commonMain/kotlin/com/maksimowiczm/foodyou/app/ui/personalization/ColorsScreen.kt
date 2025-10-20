package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TonalToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.ReadYouImage
import com.maksimowiczm.foodyou.app.ui.common.extension.plus
import com.maksimowiczm.foodyou.app.ui.common.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.app.ui.common.theme.isDark
import com.maksimowiczm.foodyou.device.domain.ThemeOption
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ColorsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val viewModel: ColorsViewModel = koinViewModel()

    val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
    val nutrientsColors by viewModel.nutrientsColors.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_colors)) },
                subtitle = { Text(stringResource(Res.string.description_colors)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.headline_theme),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                ThemeOptionPicker(
                    themeOption = themeSettings.themeOption,
                    onThemeOptionChange = viewModel::updateThemeOption,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            item {
                Box(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    ReadYouImage(Modifier.sizeIn(maxWidth = 400.dp, maxHeight = 350.dp))
                }
            }
            item {
                PalettePicker(
                    isDark = themeSettings.isDark(),
                    selectedTheme = themeSettings.theme,
                    onThemeChange = viewModel::updateTheme,
                )
            }
            item {
                AdditionalSettings(
                    themeSettings = themeSettings,
                    onRandomizeTheme = viewModel::setRandomizeTheme,
                    onUpdateTheme = viewModel::updateTheme,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                val nutrientsPalette = LocalNutrientsPalette.current

                val proteinsColor =
                    nutrientsColors.proteins?.let(::Color)
                        ?: nutrientsPalette.proteinsOnSurfaceContainer
                val carbsColor =
                    nutrientsColors.carbohydrates?.let(::Color)
                        ?: nutrientsPalette.carbohydratesOnSurfaceContainer
                val fatsColor =
                    nutrientsColors.fats?.let(::Color) ?: nutrientsPalette.fatsOnSurfaceContainer

                NutrientsColors(
                    proteinsColor = proteinsColor,
                    onProteinsColorChange = { newColor ->
                        viewModel.updateNutrientsColors(proteinsColor = newColor.value)
                    },
                    carbsColor = carbsColor,
                    onCarbsColorChange = { newColor ->
                        viewModel.updateNutrientsColors(carbohydratesColor = newColor.value)
                    },
                    fatsColor = fatsColor,
                    onFatsColorChange = { newColor ->
                        viewModel.updateNutrientsColors(fatsColor = newColor.value)
                    },
                    onReset = { viewModel.resetNutrientsColors() },
                    contentPadding = PaddingValues(top = 16.dp) + PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionPicker(
    themeOption: ThemeOption,
    onThemeOptionChange: (ThemeOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement =
            Arrangement.spacedBy(
                ButtonGroupDefaults.ConnectedSpaceBetween,
                Alignment.CenterHorizontally,
            ),
    ) {
        TonalToggleButton(
            checked = themeOption == ThemeOption.System,
            onCheckedChange = { onThemeOptionChange(ThemeOption.System) },
            modifier = Modifier.semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
            content = { Text(stringResource(Res.string.headline_system)) },
        )
        TonalToggleButton(
            checked = themeOption == ThemeOption.Light,
            onCheckedChange = { onThemeOptionChange(ThemeOption.Light) },
            modifier = Modifier.semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
            content = { Text(stringResource(Res.string.headline_light)) },
        )
        TonalToggleButton(
            checked = themeOption == ThemeOption.Dark,
            onCheckedChange = { onThemeOptionChange(ThemeOption.Dark) },
            modifier = Modifier.semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
            content = { Text(stringResource(Res.string.headline_dark)) },
        )
    }
}
