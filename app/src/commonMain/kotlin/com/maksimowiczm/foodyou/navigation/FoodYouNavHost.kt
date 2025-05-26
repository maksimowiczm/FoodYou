package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.feature.about.About
import com.maksimowiczm.foodyou.feature.about.aboutGraph
import com.maksimowiczm.foodyou.feature.addfood.AddFood
import com.maksimowiczm.foodyou.feature.addfood.addFoodGraph
import com.maksimowiczm.foodyou.feature.goals.GoalsScreen
import com.maksimowiczm.foodyou.feature.goals.GoalsSettings
import com.maksimowiczm.foodyou.feature.goals.goalsGraph
import com.maksimowiczm.foodyou.feature.importexport.ImportExport
import com.maksimowiczm.foodyou.feature.importexport.importExportGraph
import com.maksimowiczm.foodyou.feature.language.Language
import com.maksimowiczm.foodyou.feature.language.languageGraph
import com.maksimowiczm.foodyou.feature.meal.MealCardSettings
import com.maksimowiczm.foodyou.feature.meal.MealsSettings
import com.maksimowiczm.foodyou.feature.meal.mealGraph
import com.maksimowiczm.foodyou.feature.measurement.CreateMeasurement
import com.maksimowiczm.foodyou.feature.measurement.UpdateMeasurement
import com.maksimowiczm.foodyou.feature.measurement.measurementGraph
import com.maksimowiczm.foodyou.feature.product.CreateProduct
import com.maksimowiczm.foodyou.feature.product.UpdateProduct
import com.maksimowiczm.foodyou.feature.product.productGraph
import com.maksimowiczm.foodyou.feature.recipe.CreateRecipe
import com.maksimowiczm.foodyou.feature.recipe.UpdateRecipe
import com.maksimowiczm.foodyou.feature.recipe.recipeGraph
import com.maksimowiczm.foodyou.ui.home.Home
import com.maksimowiczm.foodyou.ui.home.homeGraph
import com.maksimowiczm.foodyou.ui.settings.HomeSettings
import com.maksimowiczm.foodyou.ui.settings.Settings
import com.maksimowiczm.foodyou.ui.settings.settingsGraph

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {
        homeGraph(
            onSettings = {
                navController.navigate(Settings) {
                    launchSingleTop = true
                }
            },
            onAbout = {
                navController.navigate(About) {
                    launchSingleTop = true
                }
            },
            onEditMeasurement = {
                navController.navigate(UpdateMeasurement(it)) {
                    launchSingleTop = true
                }
            },
            onMealCardLongClick = {
                navController.navigate(MealCardSettings) {
                    launchSingleTop = true
                }
            },
            onMealCardAddClick = { epochDay, mealId ->
                navController.navigate(
                    AddFood(
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true
                }
            },
            onCaloriesCardClick = { epochDay ->
                navController.navigate(
                    GoalsScreen(epochDay)
                ) {
                    launchSingleTop = true
                }
            }
        )
        settingsGraph(
            onBack = {
                navController.popBackStack<Settings>(inclusive = true)
            },
            onHomeSettings = {
                navController.navigate(HomeSettings) {
                    launchSingleTop = true
                }
            },
            onMealsSettings = {
                navController.navigate(MealsSettings) {
                    launchSingleTop = true
                }
            },
            onMealsCardSettings = {
                navController.navigate(MealCardSettings) {
                    launchSingleTop = true
                }
            },
            onGoalsSettings = {
                navController.navigate(GoalsSettings) {
                    launchSingleTop = true
                }
            },
            onAbout = {
                navController.navigate(About) {
                    launchSingleTop = true
                }
            },
            onLanguage = {
                navController.navigate(Language) {
                    launchSingleTop = true
                }
            },
            onImportExport = {
                navController.navigate(ImportExport) {
                    launchSingleTop = true
                }
            }
        )
        aboutGraph()
        addFoodGraph(
            onBack = {
                navController.popBackStack<AddFood>(inclusive = true)
            }
        )
        goalsGraph(
            onGoalsSettingsBack = {
                navController.popBackStack<GoalsSettings>(inclusive = true)
            }
        )
        languageGraph(
            onBack = {
                navController.popBackStack<Language>(inclusive = true)
            }
        )
        mealGraph(
            onMealsSettings = {
                navController.navigate(MealsSettings) {
                    launchSingleTop = true
                    popUpTo<MealsSettings>()
                }
            },
            onMealsSettingsBack = {
                navController.popBackStack<MealsSettings>(inclusive = true)
            },
            onMealsCardSettings = {
                navController.navigate(MealCardSettings) {
                    launchSingleTop = true
                    popUpTo<MealCardSettings>()
                }
            },
            onMealsCardSettingsBack = {
                navController.popBackStack<MealCardSettings>(inclusive = true)
            }
        )
        importExportGraph(
            onBack = {
                navController.popBackStack<ImportExport>(inclusive = true)
            }
        )
        recipeGraph(
            onCreateClose = {
                navController.popBackStack<CreateRecipe>(inclusive = true)
            },
            onCreate = {
                navController.popBackStack<CreateRecipe>(inclusive = true)
            },
            onUpdateClose = {
                navController.popBackStack<UpdateRecipe>(inclusive = true)
            },
            onUpdate = {
                navController.popBackStack<UpdateRecipe>(inclusive = true)
            }
        )
        productGraph(
            createOnBack = {
                navController.popBackStack<CreateProduct>(inclusive = true)
            },
            updateOnBack = {
                navController.popBackStack<UpdateProduct>(inclusive = true)
            },
            onCreateProduct = {
                navController.popBackStack<CreateProduct>(inclusive = true)
            },
            onUpdateProduct = {
                navController.popBackStack<UpdateProduct>(inclusive = true)
            }
        )
        measurementGraph(
            createOnBack = {
                navController.popBackStack<CreateMeasurement>(inclusive = true)
            },
            updateOnBack = {
                navController.popBackStack<UpdateMeasurement>(inclusive = true)
            },
            onEditFood = {
                val route = when (it) {
                    is FoodId.Product -> UpdateProduct(it.id)
                    is FoodId.Recipe -> UpdateRecipe(it.id)
                }

                navController.navigate(route) {
                    launchSingleTop = true
                }
            },
            createOnRecipeClone = { foodId, mealId, epochDay ->
                navController.navigate(
                    CreateMeasurement(
                        foodId = foodId,
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true

                    popUpTo<CreateMeasurement> {
                        inclusive = true
                    }
                }
            },
            updateOnRecipeClone = { foodId, mealId, epochDay ->
                navController.navigate(
                    CreateMeasurement(
                        foodId = foodId,
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true

                    popUpTo<UpdateMeasurement> {
                        inclusive = true
                    }
                }
            }
        )
    }
}
