package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.language.ui.LanguageScreen
import kotlinx.serialization.Serializable

@Serializable
data object Language

fun NavGraphBuilder.languageGraph(onBack: () -> Unit) {
    forwardBackwardComposable<Language> {
        LanguageScreen(
            onBack = onBack
        )
    }
}
