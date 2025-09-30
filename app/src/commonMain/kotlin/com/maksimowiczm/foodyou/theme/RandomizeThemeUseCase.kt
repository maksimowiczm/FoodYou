package com.maksimowiczm.foodyou.theme

import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository

class RandomizeThemeUseCase(
    private val repository: UserPreferencesRepository<ThemeSettings>,
    private val randomColorProvider: RandomColorProvider,
) {
    suspend fun randomize() {
        val color = randomColorProvider.randomColor()
        val style = possibleStyles.random()
        val contrast = possibleContrast.random()

        repository.update {
            val isAmoled =
                when (theme) {
                    is Theme.Custom -> theme.isAmoled
                    else -> false
                }

            copy(
                theme =
                    Theme.Custom(
                        seedColor = color,
                        style = style,
                        contrast = contrast,
                        isAmoled = isAmoled,
                    )
            )
        }
    }

    private companion object {
        val possibleStyles =
            arrayOf(
                ThemeStyle.TonalSpot,
                ThemeStyle.Neutral,
                ThemeStyle.Vibrant,
                ThemeStyle.Expressive,
                ThemeStyle.Rainbow,
                ThemeStyle.FruitSalad,
                ThemeStyle.Fidelity,
                ThemeStyle.Content,
            )

        val possibleContrast = arrayOf(ThemeContrast.Default)
    }
}
