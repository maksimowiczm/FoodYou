package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.common.compose.extension.plus
import com.maksimowiczm.foodyou.theme.NutrientsColors
import com.maksimowiczm.foodyou.theme.ThemeSettings
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ThemeScreenContent(
    themeSettings: ThemeSettings,
    onThemeSettingsChange: (ThemeSettings) -> Unit,
    nutrientsColors: NutrientsColors,
    onNutrientsColorsChange: (NutrientsColors) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
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
            ThemePicker(
                themeOption = themeSettings.themeOption,
                onThemeOptionChange = {
                    onThemeSettingsChange(themeSettings.copy(themeOption = it))
                },
                Modifier.fillMaxWidth(),
            )
        }
        item {
            Box(
                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                ThemePreviewImage(Modifier.sizeIn(maxWidth = 400.dp, maxHeight = 350.dp))
            }
        }
        item {
            PalettePicker(
                isDark = themeSettings.isDark(),
                selectedTheme = themeSettings.theme,
                onThemeChange = { newTheme ->
                    onThemeSettingsChange(themeSettings.copy(theme = newTheme))
                },
            )
        }
        item {
            AdditionalSettings(
                themeSettings = themeSettings,
                onThemeSettingsChange = onThemeSettingsChange,
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
                    onNutrientsColorsChange(nutrientsColors.copy(proteins = newColor.value))
                },
                carbsColor = carbsColor,
                onCarbsColorChange = { newColor ->
                    onNutrientsColorsChange(nutrientsColors.copy(carbohydrates = newColor.value))
                },
                fatsColor = fatsColor,
                onFatsColorChange = { newColor ->
                    onNutrientsColorsChange(nutrientsColors.copy(fats = newColor.value))
                },
                onReset = {
                    onNutrientsColorsChange(
                        NutrientsColors(proteins = null, carbohydrates = null, fats = null)
                    )
                },
                contentPadding = PaddingValues(top = 16.dp) + PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
