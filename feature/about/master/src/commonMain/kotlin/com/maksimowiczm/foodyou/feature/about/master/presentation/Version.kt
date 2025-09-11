package com.maksimowiczm.foodyou.feature.about.master.presentation

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.business.opensource.domain.changelog.Version
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun Version.stringResource(): String {
    val newFeaturesString = stringResource(Res.string.changelog_new_features)
    val changesString = stringResource(Res.string.changelog_changes)
    val bugFixesString = stringResource(Res.string.changelog_bug_fixes)
    val translationsString = stringResource(Res.string.changelog_translations)

    return remember(newFeaturesString, changesString, bugFixesString, translationsString) {
        buildString {
            if (newFeatures.isNotEmpty()) {
                append("$newFeaturesString:\n")
                newFeatures.forEach { append("- $it\n") }
            }
            if (changes.isNotEmpty()) {
                append("$changesString:\n")
                changes.forEach { append("- $it\n") }
            }
            if (bugFixes.isNotEmpty()) {
                append("$bugFixesString:\n")
                bugFixes.forEach { append("- $it\n") }
            }
            if (translations.isNotEmpty()) {
                append("$translationsString:\n")
                translations.forEach { append("- $it\n") }
            }
        }
    }
}
