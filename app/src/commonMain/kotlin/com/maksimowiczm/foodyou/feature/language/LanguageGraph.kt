package com.maksimowiczm.foodyou.feature.language

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.maksimowiczm.foodyou.feature.language.ui.LanguageScreen
import kotlinx.serialization.Serializable

@Serializable
data object Language

fun NavGraphBuilder.languageGraph(onBack: () -> Unit) {
    composable<Language> {
        LanguageScreen(
            onBack = onBack
        )
    }
}
