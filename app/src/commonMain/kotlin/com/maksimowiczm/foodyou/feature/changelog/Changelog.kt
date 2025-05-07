@file:Suppress("ktlint:standard:max-line-length")

package com.maksimowiczm.foodyou.feature.changelog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.ext.now
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

object Changelog {
    val versions
        get() = listOf(
            next,
            v2_1_1,
            v2_1_0,
            v2_0_0
        )

    val next = Version.next(
        version = "next",
        date = LocalDate.now(),
        newFeatures = listOf(),
        changes = listOf(
            "small UI tweaks in the meal screen"
        ),
        bugFixes = listOf(),
        translations = listOf(),
        notes = null
    )

    val v2_1_1 = Version(
        version = "2.1.1",
        date = LocalDate(2025, 4, 24),
        translations = listOf(
            "updated Arabic",
            "updated Danish",
            "updated German",
            "updated Italian",
            "updated Polish",
            "updated Portuguese (Brazilian)",
            "updated Russian",
            "updated Turkish"
        )
    )

    val v2_1_0 = Version(
        version = "2.1.0",
        date = LocalDate(2025, 4, 22),
        newFeatures = listOf(
            "changelog",
            "add Open Food Facts product manually"
        ),
        changes = listOf(
            "remove Open Food Facts in-app search",
            "remove all unused Open Food Facts products"
        ),
        bugFixes = listOf(
            "product barcode can be edited in the product form",
            "display valid meal summary on the meal screen and cards",
            "don't crash on meal screen when there is more than one measurement with the same product in the meal"
        ),
        notes = """
            Why was the Open Food Facts search removed?
            It was removed because it wasn't working as expected. The search often caused confusion among users, as it frequently returned inaccurate or irrelevant results. This led to my decision to remove the in-app search feature altogether and replace it with a manual entry option. This isn't a rant against Open Food Facts, as it's a great and free project. To be fair, the app used the deprecated V1 API, which seems inadequate for a modern app.
        """.trimIndent()
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

    companion object {
        fun next(
            version: String,
            date: LocalDate,
            newFeatures: List<String>,
            changes: List<String>,
            bugFixes: List<String>,
            translations: List<String>,
            notes: String?
        ) = Version(
            version = version,
            date = date,
            newFeatures = newFeatures,
            changes = changes,
            bugFixes = bugFixes,
            translations = translations,
            notes = notes
        )
    }
}
