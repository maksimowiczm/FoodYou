package com.maksimowiczm.foodyou.feature.about.master.presentation

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.shared.ui.ext.now
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Immutable
internal data class Version(
    val version: String,
    val date: LocalDate,
    val newFeatures: List<String> = emptyList(),
    val changes: List<String> = emptyList(),
    val bugFixes: List<String> = emptyList(),
    val translations: List<String> = emptyList(),
    val notes: String? = null,
    val isPreview: Boolean = false,
) {
    @Composable
    fun stringResource(): String {
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

    companion object {
        fun next(
            newFeatures: List<String>,
            changes: List<String>,
            bugFixes: List<String>,
            translations: List<String>,
            notes: String?,
        ) =
            Version(
                version = "next",
                date = LocalDate.now(),
                newFeatures = newFeatures,
                changes = changes,
                bugFixes = bugFixes,
                translations = translations,
                notes = notes,
                isPreview = true,
            )
    }
}
