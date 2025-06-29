@file:Suppress("ktlint:standard:max-line-length")

package com.maksimowiczm.foodyou.data

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
            v2_10_0,
            v2_9_0,
            v2_8_0,
            v2_7_2,
            v2_7_1,
            v2_7_0,
            v2_6_0,
            v2_5_0,
            v2_4_0,
            v2_3_2,
            v2_3_1,
            v2_3_0,
            v2_2_0,
            v2_1_1,
            v2_1_0,
            v2_0_0
        )

    val v2_10_0 = Version(
        version = "2.10.0",
        date = LocalDate(2025, 6, 29),
        newFeatures = listOf(
            "Added nutrition facts list size preference. You can now choose between compact and full sizes on the measurement screen.",
            "Added food note. You can now add a note to a food, which will be displayed on the food measurement screen."
        ),
        changes = listOf(
            "Added background to the status bar on the food search screen.",
            "The product form discard dialog will now appear only if the form has been modified (i.e., when the user makes a change)."
        ),
        bugFixes = listOf(
            "Fixed an issue where the energy value was overridden when opening the product update form.",
            "The app will now show the default measurement for serving and package if the food has no defined measurements."
        ),
        translations = listOf(
            "Added Catalan.",
            "Added Spanish."
        ),
        notes = null
    )

    val v2_9_0 = Version(
        version = "2.9.0",
        date = LocalDate(2025, 6, 18),
        newFeatures = listOf(
            "Added donation screen. You can now support the app development by donating.",
            "Added liquid food. You can now create and track liquid products and recipes.",
            "Added personalization settings",
            "Added nutrition facts personalization. You can now choose which nutrition facts to display and in what order within the app."
        ),
        changes = listOf(
            "Updated about screen",
            "Move home settings to the personalize settings",
            "Fixed monochrome icon"
        ),
        bugFixes = listOf(),
        translations = listOf(),
        notes = null
    )

    val v2_8_0 = Version(
        version = "2.8.0",
        date = LocalDate(2025, 6, 13),
        newFeatures = listOf(
            "Added recipe unpacking. You can now unpack recipe measurement into individual ingredients."
        ),
        bugFixes = listOf(
            "Fixed an app crash when creating a new meal"
        ),
        translations = listOf(
            "Added Chinese Simplified"
        )
    )

    val v2_7_2 = Version(
        version = "2.7.2",
        date = LocalDate(2025, 6, 11),
        changes = listOf(
            "Select latest measurement on the create measurement screen. Most of the time, this will be the same measurement as shown on the food search screen."
        ),
        bugFixes = listOf(
            "Use proper measurement date when updating product measurement",
            "Stop displaying deleted measurements"
        )
    )

    val v2_7_1 = Version(
        version = "2.7.1",
        date = LocalDate(2025, 6, 10),
        newFeatures = listOf(
            "App now supports Android 7.0 and above"
        ),
        bugFixes = listOf(
            "Fixed products export header, it will now write a valid CSV file",
            "Fixed app crashes on some older Android version when migrating database to the new version"
        )
    )

    val v2_7_0 = Version(
        version = "2.7.0",
        date = LocalDate(2025, 6, 10),
        newFeatures = listOf(
            "Added new mineral - Chromium",
            "Added FoodData Central (USDA) support. You can now download product data from new remote database.",
            "Added Swiss Food Composition Database support",
            "Added external databases settings screen to manage remote databases"
        ),
        bugFixes = listOf(
            "Stop overriding calories value when updating product or downloading product data from remote databases"
        ),
        notes = "Even the best food tracking app is useless without a good database of food products. This update adds support for two new databases: FoodData Central (USDA) and the Swiss Food Composition Database."
    )

    val v2_6_0 = Version(
        version = "2.6.0",
        date = LocalDate(2025, 6, 5),
        newFeatures = listOf(
            "Recipe can now contain other recipes. Go easy on the recursion. 😉"
        ),
        changes = listOf(
            "Limit food search results to 100 items. If you need more results, please refine your search query.",
            "Removed recipe clone feature",
            "Updated toggle button in the food search screen"
        ),
        notes = "This update introduces a significant change to the recipe system, allowing recipes to include other recipes."
    )

    val v2_5_0 = Version(
        version = "2.5.0",
        date = LocalDate(2025, 5, 29),
        newFeatures = listOf(
            "Added a new screen for food measurement",
            "Added crash handler to display a crash report screen when the app crashes",
            "Added goals card settings"
        ),
        changes = listOf(
            "Display the brand name after the product name",
            "Updated barcode scanner icon",
            "Updated recipe icon",
            "Removed meal screen; all meal items are now displayed directly on the home screen",
            "Renamed calories card to goals card",
            "Reset home screen cards order. Default order is now Calendar, Goals, Meals",
            "Reset meal cards layout; default is now vertical",
            "Updated food search screen"
        ),
        bugFixes = listOf(
            "Fixed empty recipes, app won't crash when the recipe has no ingredients",
            "Fixed home settings back navigation; it now returns to settings instead of home screen"
        ),
        notes = null
    )

    val v2_4_0 = Version(
        version = "2.4.0",
        date = LocalDate(2025, 5, 23),
        newFeatures = listOf(
            "Added experimental support for importing and exporting food products via CSV file",
            "Added copying recipe into product"
        ),
        changes = listOf(
            "Updated meals cards settings layout picker",
            "Updated settings screen",
            "Use custom tabs for Open Food Facts links if available"
        ),
        bugFixes = listOf(
            "Open valid recipe when editing entry in the meal screen"
        ),
        translations = listOf(
            "Added Dutch (Thanks to GrizzleNL)"
        ),
        notes = "Since recipes can't contain other recipes for now, copying a recipe serves as a workaround. This is currently limited to the search screen and is expected to be replaced with proper recipe support in the future."
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
