package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.navigation.domain.AboutDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorMessagesDestination
import com.maksimowiczm.foodyou.navigation.domain.CreateProductDestination
import com.maksimowiczm.foodyou.navigation.domain.DumpDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.ExportCsvProductsDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiaryAddEntryDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiaryCreateProductDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiaryCreateQuickAdd
import com.maksimowiczm.foodyou.navigation.domain.FoodDiaryCreateRecipeDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiarySearchDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiaryUpdateQuickAdd
import com.maksimowiczm.foodyou.navigation.domain.GoalsCardSettingsDestination
import com.maksimowiczm.foodyou.navigation.domain.GoalsMasterDestination
import com.maksimowiczm.foodyou.navigation.domain.HomeDestination
import com.maksimowiczm.foodyou.navigation.domain.ImportCsvProductsDestination
import com.maksimowiczm.foodyou.navigation.domain.MealsCardsSettingsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsExternalDatabasesDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsGoalsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsHomeDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsLanguageDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsMealsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsNutritionFactsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsPersonalizationDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsSwissFoodCompositionDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.UpdateFoodDiaryEntryDestination
import com.maksimowiczm.foodyou.navigation.domain.UpdateProductDestination
import com.maksimowiczm.foodyou.navigation.domain.UpdateRecipeDestination
import com.maksimowiczm.foodyou.navigation.domain.UsdaApiKeyDestination
import com.maksimowiczm.foodyou.navigation.graph.about.aboutNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.food.foodNavigationGraphBuilder
import com.maksimowiczm.foodyou.navigation.graph.fooddiary.foodDiaryNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.goals.goalsNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.home.homeNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.settings.settingsDatabaseNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.settings.settingsNavigationGraph

@Composable
fun FoodYouNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeDestination,
        modifier = modifier,
    ) {
        homeNavigationGraph(
            masterOnSettings = { navController.navigateSingleTop(SettingsDestination) },
            masterOnTitle = { navController.navigateSingleTop(AboutDestination) },
            masterOnMealCardsSettings = {
                navController.navigateSingleTop(MealsCardsSettingsDestination)
            },
            masterOnFoodDiarySearch = { epochDay, mealId ->
                navController.navigateSingleTop(FoodDiarySearchDestination(epochDay, mealId))
            },
            masterOnFoodDiaryQuickAdd = { epochDay, mealId ->
                navController.navigateSingleTop(FoodDiaryCreateQuickAdd(epochDay, mealId))
            },
            masterOnGoalsCardSettings = {
                navController.navigateSingleTop(GoalsCardSettingsDestination)
            },
            masterOnGoals = { navController.navigateSingleTop(GoalsMasterDestination(it)) },
            masterOnEditDiaryEntry = { food, manual ->
                when {
                    food != null ->
                        navController.navigateSingleTop(UpdateFoodDiaryEntryDestination(food))

                    manual != null ->
                        navController.navigateSingleTop(FoodDiaryUpdateQuickAdd(manual))

                    else -> error("Either food or manual must be non-null")
                }
            },
            mealsCardsSettingsOnBack = {
                navController.popBackStack<MealsCardsSettingsDestination>(true)
            },
            mealsCardsSettingsOnMealSettings = {
                navController.navigate(SettingsMealsDestination) {
                    launchSingleTop = true
                    popUpTo<SettingsMealsDestination>()
                }
            },
            goalsCardSettingsOnBack = {
                navController.popBackStack<GoalsCardSettingsDestination>(true)
            },
            goalsCardSettingsOnGoalsSettings = {
                navController.navigateSingleTop(SettingsGoalsDestination)
            },
        )
        aboutNavigationGraph(
            masterOnBack = { navController.popBackStack<AboutDestination>(true) },
            masterOnSponsor = { navController.navigateSingleTop(AboutSponsorMessagesDestination) },
            sponsorMessagesOnBack = {
                navController.popBackStack<AboutSponsorMessagesDestination>(true)
            },
            sponsorMessagesOnSponsor = { navController.navigateSingleTop(AboutSponsorDestination) },
            sponsorOnBack = { navController.popBackStack<AboutSponsorDestination>(true) },
        )
        settingsNavigationGraph(
            masterOnBack = { navController.popBackStack<SettingsDestination>(true) },
            masterOnSponsor = { navController.navigateSingleTop(AboutSponsorMessagesDestination) },
            masterOnAbout = { navController.navigateSingleTop(AboutDestination) },
            masterOnMeals = { navController.navigateSingleTop(SettingsMealsDestination) },
            masterOnLanguage = { navController.navigateSingleTop(SettingsLanguageDestination) },
            masterOnGoals = { navController.navigateSingleTop(SettingsGoalsDestination) },
            masterOnPersonalization = {
                navController.navigateSingleTop(SettingsPersonalizationDestination)
            },
            masterOnDatabase = { navController.navigateSingleTop(SettingsDatabaseDestination) },
            mealsOnBack = { navController.popBackStack<SettingsMealsDestination>(true) },
            mealsOnMealsCardsSettings = {
                navController.navigate(MealsCardsSettingsDestination) {
                    launchSingleTop = true
                    popUpTo<MealsCardsSettingsDestination>()
                }
            },
            languageOnBack = { navController.popBackStack<SettingsLanguageDestination>(true) },
            goalsOnBack = { navController.popBackStack<SettingsGoalsDestination>(true) },
            goalsOnSave = { navController.popBackStack<SettingsGoalsDestination>(true) },
            personalizationOnBack = {
                navController.popBackStack<SettingsPersonalizationDestination>(true)
            },
            personalizationOnHome = { navController.navigateSingleTop(SettingsHomeDestination) },
            personalizationOnNutrition = {
                navController.navigateSingleTop(SettingsNutritionFactsDestination)
            },
            nutritionOnBack = {
                navController.popBackStack<SettingsNutritionFactsDestination>(true)
            },
            homeOnBack = { navController.popBackStack<SettingsHomeDestination>(true) },
            homeOnGoals = { navController.navigateSingleTop(GoalsCardSettingsDestination) },
            homeOnMeals = { navController.navigateSingleTop(MealsCardsSettingsDestination) },
        )
        settingsDatabaseNavigationGraph(
            masterOnBack = { navController.popBackStack<SettingsDatabaseDestination>(true) },
            masterOnExternalDatabases = {
                navController.navigateSingleTop(SettingsExternalDatabasesDestination)
            },
            masterOnImportCsvProducts = {
                navController.navigateSingleTop(ImportCsvProductsDestination)
            },
            masterOnExportCsvProducts = {
                navController.navigateSingleTop(ExportCsvProductsDestination)
            },
            masterOnDatabaseDump = { navController.navigateSingleTop(DumpDatabaseDestination) },
            externalDatabasesOnBack = {
                navController.popBackStack<SettingsExternalDatabasesDestination>(true)
            },
            externalDatabasesOnSwissFoodCompositionDatabase = {
                navController.navigateSingleTop(SettingsSwissFoodCompositionDatabaseDestination)
            },
            usdaApiKeyOnDismiss = { navController.popBackStack<UsdaApiKeyDestination>(true) },
            usdaApiKeyOnSave = { navController.popBackStack<UsdaApiKeyDestination>(true) },
            databaseDumpOnBack = { navController.popBackStack<DumpDatabaseDestination>(true) },
            databaseDumpOnSuccess = { navController.popBackStack<DumpDatabaseDestination>(true) },
            swissFoodCompositionDatabaseOnBack = {
                navController.popBackStack<SettingsSwissFoodCompositionDatabaseDestination>(true)
            },
            importCsvProductsOnBack = {
                navController.popBackStack<ImportCsvProductsDestination>(true)
            },
            importCsvProductsOnFinish = {
                navController.popBackStack<ImportCsvProductsDestination>(true)
            },
            exportCsvProductsOnBack = {
                navController.popBackStack<ExportCsvProductsDestination>(true)
            },
            exportCsvProductsOnFinish = {
                navController.popBackStack<ExportCsvProductsDestination>(true)
            },
        )
        goalsNavigationGraph(
            masterOnBack = { navController.popBackStack<GoalsMasterDestination>(true) }
        )
        foodDiaryNavigationGraph(
            searchOnBack = { navController.popBackStack<FoodDiarySearchDestination>(true) },
            searchOnCreateRecipe = { date, mealId ->
                navController.navigateSingleTop(FoodDiaryCreateRecipeDestination(mealId, date))
            },
            searchOnCreateProduct = { date, mealId ->
                navController.navigateSingleTop(FoodDiaryCreateProductDestination(mealId, date))
            },
            searchOnMeasure = { foodId, measurement, date, mealId ->
                navController.navigateSingleTop(
                    FoodDiaryAddEntryDestination(
                        foodId = foodId,
                        mealId = mealId,
                        date = date,
                        measurement = measurement,
                    )
                )
            },
            addOnBack = { navController.popBackStack<FoodDiaryAddEntryDestination>(true) },
            addOnAdded = { date, mealId ->
                while (true) {
                    if (!navController.popBackStack<FoodDiaryAddEntryDestination>(true)) {
                        break
                    }
                }
            },
            addOnEditFood = {
                when (it) {
                    is FoodId.Product -> UpdateProductDestination(it)
                    is FoodId.Recipe -> UpdateRecipeDestination(it)
                }.let(navController::navigateSingleTop)
            },
            addOnIngredient = { foodId, measurement, date, mealId ->
                navController.navigate(
                    FoodDiaryAddEntryDestination(
                        foodId = foodId,
                        mealId = mealId,
                        date = date,
                        measurement = measurement,
                    )
                )
            },
            createProductOnBack = {
                navController.popBackStack<FoodDiaryCreateProductDestination>(true)
            },
            createProductOnCreate = { foodId, date, mealId ->
                navController.navigate(
                    FoodDiaryAddEntryDestination(
                        foodId = foodId,
                        mealId = mealId,
                        date = date,
                        measurement = null,
                    )
                ) {
                    launchSingleTop = true
                    popUpTo<FoodDiaryCreateProductDestination> { inclusive = true }
                }
            },
            onUpdateUsdaApiKey = { navController.navigateSingleTop(UsdaApiKeyDestination) },
            createRecipeOnBack = {
                navController.popBackStack<FoodDiaryCreateRecipeDestination>(true)
            },
            createRecipeOnCreate = { foodId, date, mealId ->
                navController.navigate(
                    FoodDiaryAddEntryDestination(
                        foodId = foodId,
                        mealId = mealId,
                        date = date,
                        measurement = null,
                    )
                ) {
                    launchSingleTop = true
                    popUpTo<FoodDiaryCreateRecipeDestination> { inclusive = true }
                }
            },
            createOnEditFood = { foodId ->
                when (foodId) {
                    is FoodId.Product -> UpdateProductDestination(foodId)
                    is FoodId.Recipe -> UpdateRecipeDestination(foodId)
                }.let(navController::navigateSingleTop)
            },
            updateOnBack = { navController.popBackStack<UpdateFoodDiaryEntryDestination>(true) },
            updateOnSave = { navController.popBackStack<UpdateFoodDiaryEntryDestination>(true) },
            createQuickAddOnBack = { navController.popBackStack<FoodDiaryCreateQuickAdd>(true) },
            createQuickAddOnSave = { navController.popBackStack<FoodDiaryCreateQuickAdd>(true) },
            updateQuickAddOnBack = { navController.popBackStack<FoodDiaryUpdateQuickAdd>(true) },
            updateQuickAddOnSave = { navController.popBackStack<FoodDiaryUpdateQuickAdd>(true) },
        )
        foodNavigationGraphBuilder(
            updateProductOnBack = { navController.popBackStack<UpdateProductDestination>(true) },
            updateProductOnSave = { navController.popBackStack<UpdateProductDestination>(true) },
            updateRecipeOnBack = { navController.popBackStack<UpdateRecipeDestination>(true) },
            updateRecipeOnSave = { navController.popBackStack<UpdateRecipeDestination>(true) },
            updateRecipeOnEditFood = { foodId ->
                when (foodId) {
                    is FoodId.Product -> UpdateProductDestination(foodId)
                    is FoodId.Recipe -> UpdateRecipeDestination(foodId)
                }.let(navController::navigateSingleTop)
            },
            onUpdateUsdaApiKey = { navController.navigateSingleTop(UsdaApiKeyDestination) },
            createProductOnBack = { navController.popBackStack<CreateProductDestination>(true) },
            createProductOnCreate = { navController.popBackStack<CreateProductDestination>(true) },
            createOnUpdateUsdaApiKey = { navController.navigateSingleTop(UsdaApiKeyDestination) },
        )
    }
}

internal fun <T : Any> NavController.navigateSingleTop(route: T) =
    navigate(route) { launchSingleTop = true }
