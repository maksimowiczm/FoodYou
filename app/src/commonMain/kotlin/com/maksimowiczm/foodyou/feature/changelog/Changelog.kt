@file:Suppress("ktlint:standard:max-line-length")

package com.maksimowiczm.foodyou.feature.changelog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.ext.now
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlin.String
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

object Changelog {
    val versions
        get() = listOf(
            v2_3_2,
            v2_3_1,
            v2_3_0,
            v2_2_0,
            v2_1_1,
            v2_1_0,
            v2_0_0
        )

    val next = Version.next(
        newFeatures = listOf(
            "Added experimental support for importing and exporting food products via CSV file"
        ),
        changes = listOf(
            "Updated home settings top bar",
            "Updated meals cards settings layout picker"
        ),
        bugFixes = listOf(),
        translations = listOf(),
        notes = null
    )

    val v2_3_2 = Version(
        version = "2.3.2",
        date = LocalDate(2025, 5, 19),
        newFeatures = listOf(
            "Sort food by name and brand in the meal screen"
        ),
        bugFixes = listOf(
            "Fix food search sorting by name and brand. Stop taking letter case into account."
        ),
        translations = listOf(
            "Added Hungarian",
            "Updated German"
        )
    )

    val v2_3_1 = Version(
        version = "2.3.1",
        date = LocalDate(2025, 5, 17),
        changes = listOf(
            "Sort food by name and brand in the food search screen"
        ),
        bugFixes = listOf(
            "App won't crash when the user attempts to paste with an empty clipboard",
            "Fix crashes when creating a new recipe",
            "Display the correct suffix for calories in the product form",
            "Move focus to the next field in barcode field in the product form"
        ),
        translations = listOf(
            "Updated Italian"
        ),
        notes = """
            This is a hotfix release that addresses some issues with the previous version.
        """.trimIndent()
    )

    val v2_3_0 = Version(
        version = "2.3.0",
        date = LocalDate(2025, 5, 15),
        newFeatures = listOf(
            "Add new nutrition facts, such as caffeine, vitamins, minerals, and more",
            "Share Open Food Facts product URL to add it to the app"
        ),
        changes = listOf(
            "Redesign the download product screen to be generic"
        ),
        translations = listOf(
            """Fix \' strings. It now displays correctly without escaping.""",
            "Added French",
            "Added Ukrainian"
        ),
        notes = "You can suggest new external databases to download products from on GitHub"
    )

    val v2_2_0 = Version(
        version = "2.2.0",
        date = LocalDate(2025, 5, 10),
        newFeatures = listOf(
            "Home page customization, edit the home page to your liking",
            "Meals cards customization, use vertical or horizontal layout"
        ),
        changes = listOf(
            "Small visual adjustments made to the meal screen",
            "Meals time-based ordering \"include all-day meals\" option changed to \"ignore all-day meals\". All-day meals are now included in the meal list by default."
        )
    )

    val v2_1_1 = Version(
        version = "2.1.1",
        date = LocalDate(2025, 4, 24),
        translations = listOf(
            "Updated Arabic",
            "Updated Danish",
            "Updated German",
            "Updated Italian",
            "Updated Polish",
            "Updated Portuguese (Brazilian)",
            "Updated Russian",
            "Updated Turkish"
        )
    )

    val v2_1_0 = Version(
        version = "2.1.0",
        date = LocalDate(2025, 4, 22),
        newFeatures = listOf(
            "Changelog",
            "Add Open Food Facts product manually"
        ),
        changes = listOf(
            "Remove Open Food Facts in-app search",
            "Remove all unused Open Food Facts products"
        ),
        bugFixes = listOf(
            "Product barcode can be edited in the product form",
            "Display valid meal summary on the meal screen and cards",
            "Don't crash on meal screen when there is more than one measurement with the same product in the meal"
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
            "Recipes",
            "Open food facts global search",
            "Delete unused open food facts products",
            "Show remote database error details",
            "Show warning dialog for incomplete translations when changing language"
        ),
        changes = listOf(
            "Calorie summary won't display empty meals in filter chips"
        ),
        bugFixes = listOf(
            "Product form no longer crashes when requesting the next field on \"fats\" if the \"sugars\" field was hidden"
        ),
        translations = listOf(
            "Added Portuguese (Brazilian)",
            "Added Russian",
            "Added Arabic"
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
            newFeatures: List<String>,
            changes: List<String>,
            bugFixes: List<String>,
            translations: List<String>,
            notes: String?
        ) = Version(
            version = "next",
            date = LocalDate.now(),
            newFeatures = newFeatures,
            changes = changes,
            bugFixes = bugFixes,
            translations = translations,
            notes = notes
        )
    }
}
