package com.maksimowiczm.foodyou.feature.changelog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.BuildConfig
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

object Changelog {
    val versions
        get() = listOf(
            v2_0_0
        )

    val v2_0_0 = Version(
        version = "2.0.0",
        date = LocalDate(2025, 4, 14),
        newFeatures = listOf(
            "recipes",
            "open food facts global search",
            "delete unused open food facts products",
            "show remote database error details",
            "show warning dialog for incomplete translations when changing language"
        ),
        changes = listOf(
            "calorie summary won't display empty meals in filter chips"
        ),
        bugFixes = listOf(
            "product form no longer crashes when requesting the next field on \"fats\" if the \"sugars\" field was hidden"
        ),
        translations = listOf(
            "added Portuguese (Brazilian)",
            "added Russian",
            "added Arabic"
        ),
        notes = """
            This release is marked as 2.0.0 because of significant source code changes that affect the overall structure of the app. The internal codebase has been heavily updated. The major version bump reflects these foundational changes.

            Possible other unintended changes. If you notice something odd happening, please report it
        """.trimIndent()
    )
}

data class Version(
    val version: String,
    val date: LocalDate,
    val newFeatures: List<String> = emptyList(),
    val changes: List<String> = emptyList(),
    val bugFixes: List<String> = emptyList(),
    val translations: List<String> = emptyList(),
    val notes: String? = null
) {
    val isCurrentVersion: Boolean
        get() = this.version == BuildConfig.VERSION_NAME

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
}
