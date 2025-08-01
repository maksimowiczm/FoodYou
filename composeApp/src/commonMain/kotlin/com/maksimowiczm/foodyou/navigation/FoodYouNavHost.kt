package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.feature.about.About
import com.maksimowiczm.foodyou.feature.about.Sponsor
import com.maksimowiczm.foodyou.feature.about.SponsorMessages
import com.maksimowiczm.foodyou.feature.about.aboutGraph
import com.maksimowiczm.foodyou.feature.food.domain.FoodId

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DailyGoals,
        modifier = modifier
    ) {
        appGraph(
            homeOnAbout = {
                navController.navigate(About) {
                    launchSingleTop = true
                }
            }, homeOnSettings = {
                navController.navigate(Settings) {
                    launchSingleTop = true
                }
            }, homeMealCardOnAdd = { epochDay, mealId ->
                navController.navigate(
                    FoodSearch(
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true
                }
            }, homeMealCardOnEditMeasurement = {
                navController.navigate(UpdateProductMeasurement(it)) {
                    launchSingleTop = true
                }
            }, homeMealCardOnLongClick = {
                navController.navigate(MealsCardsSettings) {
                    launchSingleTop = true
                }
            }, settingsOnPersonalization = {
                navController.navigate(Personalization) {
                    launchSingleTop = true
                }
            }, settingsOnMeals = {
                navController.navigate(MealSettings) {
                    launchSingleTop = true
                }
            }, settingsOnLanguage = {
                navController.navigate(Language) {
                    launchSingleTop = true
                }
            }, settingsOnSponsor = {
                navController.navigate(SponsorMessages) {
                    launchSingleTop = true
                }
            }, settingsOnAbout = {
                navController.navigate(About) {
                    launchSingleTop = true
                }
            }, settingsOnDatabase = {
                navController.navigate(Database) {
                    launchSingleTop = true
                }
            }, settingsOnBack = {
                navController.popBackStack<Settings>(true)
            }, settingsOnDailyGoals = {
                navController.navigate(DailyGoals) {
                    launchSingleTop = true
                }
            }, personalizationOnBack = {
                navController.popBackStack<Personalization>(true)
            }, personalizationOnHomePersonalization = {
                navController.navigate(HomePersonalization) {
                    launchSingleTop = true
                }
            }, personalizationOnNutritionFactsPersonalization = {
                navController.navigate(NutritionFactsPersonalization) {
                    launchSingleTop = true
                }
            }, homePersonalizationOnBack = {
                navController.popBackStack<HomePersonalization>(true)
            }, homePersonalizationOnMealsSettings = {
                navController.navigate(MealsCardsSettings) {
                    launchSingleTop = true
                    popUpTo<MealsCardsSettings>()
                }
            }, nutritionFactsPersonalizationOnBack = {
                navController.popBackStack<NutritionFactsPersonalization>(true)
            }, externalDatabasesOnBack = {
                navController.popBackStack<ExternalDatabases>(true)
            }, externalDatabasesOnSwissFoodCompositionDatabase = {
                navController.navigate(SwissFoodCompositionDatabase) {
                    launchSingleTop = true
                }
            }, databaseOnBack = {
                navController.popBackStack<Database>(true)
            }, databaseOnExternalDatabases = {
                navController.navigate(ExternalDatabases) {
                    launchSingleTop = true
                }
            }
        )
        foodDiaryGraph(
            foodSearchOnBack = {
                navController.popBackStack<FoodSearch>(true)
            },
            foodSearchOnCreateProduct = { mealId, date ->
                navController.navigate(
                    CreateProduct(
                        mealId = mealId,
                        date = date
                    )
                ) {
                    launchSingleTop = true
                }
            },
            foodSearchOnCreateRecipe = { mealId, date ->
                navController.navigate(
                    CreateRecipe(
                        mealId = mealId,
                        date = date
                    )
                ) {
                    launchSingleTop = true
                }
            },
            foodSearchOnFood = { id, measurement, mealId, date ->
                navController.navigate(
                    CreateMeasurement(
                        foodId = id,
                        mealId = mealId,
                        date = date,
                        measurement = measurement
                    )
                ) {
                    launchSingleTop = true
                }
            },
            createProductOnBack = {
                navController.popBackStack<CreateProduct>(true)
            },
            createProductOnCreate = { id, mealId, date ->
                navController.navigate(
                    CreateMeasurement(
                        foodId = id,
                        mealId = mealId,
                        date = date,
                        measurement = null
                    )
                ) {
                    launchSingleTop = true

                    popUpTo<CreateProduct> {
                        inclusive = true
                    }
                }
            },
            createRecipeOnBack = {
                navController.popBackStack<CreateRecipe>(true)
            },
            createRecipeOnCreate = { id, mealId, date ->
                navController.navigate(
                    CreateMeasurement(
                        foodId = id,
                        mealId = mealId,
                        date = date,
                        measurement = null
                    )
                ) {
                    launchSingleTop = true

                    popUpTo<CreateRecipe> {
                        inclusive = true
                    }
                }
            },
            updateProductOnBack = {
                navController.popBackStack<UpdateProduct>(true)
            },
            updateProductOnUpdate = {
                navController.popBackStack<UpdateProduct>(true)
            },
            updateRecipeOnBack = {
                navController.popBackStack<UpdateRecipe>(true)
            },
            updateRecipeOnUpdate = {
                navController.popBackStack<UpdateRecipe>(true)
            },
            createMeasurementOnBack = {
                navController.popBackStack<CreateMeasurement>(true)
            },
            createMeasurementOnEditFood = {
                when (it) {
                    is FoodId.Product -> navController.navigate(UpdateProduct.from(it)) {
                        launchSingleTop = true
                    }

                    is FoodId.Recipe -> navController.navigate(UpdateRecipe.from(it)) {
                        launchSingleTop = true
                    }
                }
            },
            createMeasurementOnDeleteFood = {
                navController.popBackStack<CreateMeasurement>(true)
            },
            createMeasurementOnCreateMeasurement = {
                navController.popBackStack<CreateMeasurement>(true)
            },
            updateMeasurementOnBack = {
                navController.popBackStack<UpdateProductMeasurement>(true)
            },
            updateMeasurementOnEdit = {
                when (it) {
                    is FoodId.Product -> navController.navigate(UpdateProduct.from(it)) {
                        launchSingleTop = true
                    }

                    is FoodId.Recipe -> navController.navigate(UpdateRecipe.from(it)) {
                        launchSingleTop = true
                    }
                }
            },
            updateMeasurementOnDelete = {
                navController.popBackStack<UpdateProductMeasurement>(true)
            },
            updateMeasurementOnUpdate = {
                navController.popBackStack<UpdateProductMeasurement>(true)
            },
            mealSettingsOnBack = {
                navController.popBackStack<MealSettings>(true)
            },
            mealSettingsOnMealsCardsSettings = {
                navController.navigate(MealsCardsSettings) {
                    launchSingleTop = true
                    popUpTo<MealsCardsSettings>()
                }
            },
            mealsCardsSettingsOnBack = {
                navController.popBackStack<MealsCardsSettings>(true)
            },
            mealsCardsOnMealSettings = {
                navController.navigate(MealSettings) {
                    launchSingleTop = true
                    popUpTo<MealSettings>()
                }
            },
            dailyGoalsOnBack = {
                navController.popBackStack<DailyGoals>(true)
            }
        )
        aboutGraph(
            aboutOnBack = {
                navController.popBackStack<About>(true)
            },
            aboutOnSponsor = {
                navController.navigate(SponsorMessages) {
                    launchSingleTop = true
                }
            },
            sponsorMessagesOnBack = {
                navController.popBackStack<SponsorMessages>(true)
            },
            sponsorMessagesOnSponsor = {
                navController.navigate(Sponsor) {
                    launchSingleTop = true
                }
            },
            sponsorOnBack = {
                navController.popBackStack<Sponsor>(true)
            }
        )
        languageGraph(
            onBack = {
                navController.popBackStack<Language>(true)
            }
        )
        swissFoodCompositionDatabaseGraph(
            swissFoodCompositionDatabaseOnBack = {
                navController.popBackStack<SwissFoodCompositionDatabase>(true)
            }
        )
    }
}
