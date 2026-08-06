package com.maksimowiczm.foodyou.features.personalization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TonalToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.device.domain.NutrientsColors
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeOption
import com.maksimowiczm.foodyou.device.domain.ThemeSettings
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.component.ReadYouImage
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.extension.confirm
import com.maksimowiczm.foodyou.shared.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.shared.ui.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.shared.ui.theme.isDark
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ColorsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: ColorsViewModel = koinViewModel()

    val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
    val nutrientsColors by viewModel.nutrientsColors.collectAsStateWithLifecycle()

    ColorsScreen(
        themeSettings = themeSettings,
        nutrientsColors = nutrientsColors,
        onBack = onBack,
        onThemeOptionChange = viewModel::updateThemeOption,
        onThemeChange = viewModel::updateTheme,
        onRandomizeTheme = viewModel::setRandomizeTheme,
        onUpdateNutrientsColors = viewModel::updateNutrientsColors,
        onResetNutrientsColors = viewModel::resetNutrientsColors,
        modifier = modifier,
    )
}

@Composable
fun ColorsScreen(
    themeSettings: ThemeSettings,
    nutrientsColors: NutrientsColors,
    onBack: () -> Unit,
    onThemeOptionChange: (ThemeOption) -> Unit,
    onThemeChange: (Theme) -> Unit,
    onRandomizeTheme: (Boolean) -> Unit,
    onUpdateNutrientsColors: (proteins: ULong?, carbohydrates: ULong?, fats: ULong?) -> Unit,
    onResetNutrientsColors: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
            contentPadding = paddingValues.add(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ThemeOptionPicker(
                    themeOption = themeSettings.themeOption,
                    onThemeOptionChange = onThemeOptionChange,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Box(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    ReadYouImage(Modifier.sizeIn(maxWidth = 400.dp, maxHeight = 350.dp))
                }
            }
            item {
                PalettePicker(
                    isDark = themeSettings.isDark(),
                    selectedTheme = themeSettings.theme,
                    onThemeChange = onThemeChange,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                AdditionalSettings(
                    themeSettings = themeSettings,
                    onRandomizeTheme = onRandomizeTheme,
                    onUpdateTheme = onThemeChange,
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
                        onUpdateNutrientsColors(newColor.value, null, null)
                    },
                    carbsColor = carbsColor,
                    onCarbsColorChange = { newColor ->
                        onUpdateNutrientsColors(null, newColor.value, null)
                    },
                    fatsColor = fatsColor,
                    onFatsColorChange = { newColor ->
                        onUpdateNutrientsColors(null, null, newColor.value)
                    },
                    onReset = onResetNutrientsColors,
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
    val hapticFeedback = LocalHapticFeedback.current

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
            onCheckedChange = {
                onThemeOptionChange(ThemeOption.System)
                hapticFeedback.confirm()
            },
            modifier = Modifier.semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
            content = { Text(stringResource(Res.string.headline_system)) },
        )
        TonalToggleButton(
            checked = themeOption == ThemeOption.Light,
            onCheckedChange = {
                onThemeOptionChange(ThemeOption.Light)
                hapticFeedback.confirm()
            },
            modifier = Modifier.semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
            content = { Text(stringResource(Res.string.headline_light)) },
        )
        TonalToggleButton(
            checked = themeOption == ThemeOption.Dark,
            onCheckedChange = {
                onThemeOptionChange(ThemeOption.Dark)
                hapticFeedback.confirm()
            },
            modifier = Modifier.semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
            content = { Text(stringResource(Res.string.headline_dark)) },
        )
    }
}

@Preview
@Composable
private fun ColorsScreenPreview() {
    PreviewFoodYouTheme {
        ColorsScreen(
            themeSettings =
                ThemeSettings(
                    randomizeOnLaunch = false,
                    themeOption = ThemeOption.System,
                    theme = Theme.Dynamic,
                ),
            nutrientsColors = NutrientsColors(proteins = null, carbohydrates = null, fats = null),
            onBack = {},
            onThemeOptionChange = {},
            onThemeChange = {},
            onRandomizeTheme = {},
            onUpdateNutrientsColors = { _, _, _ -> },
            onResetNutrientsColors = {},
        )
    }
}
