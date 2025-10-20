package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.About
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Colors
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDatabase
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Home
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Language
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.NutritionFactsPersonalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Personalization
import com.maksimowiczm.foodyou.app.ui.about.AboutScreen
import com.maksimowiczm.foodyou.app.ui.food.FoodDatabaseScreen
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsScreen
import com.maksimowiczm.foodyou.app.ui.home.HomeScreen
import com.maksimowiczm.foodyou.app.ui.language.LanguageScreen
import com.maksimowiczm.foodyou.app.ui.personalization.ColorsScreen
import com.maksimowiczm.foodyou.app.ui.personalization.PersonalizationScreen
import com.maksimowiczm.foodyou.app.ui.personalization.PersonalizeNutritionFactsScreen
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import kotlinx.serialization.Serializable

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(modifier = modifier, navController = navController, startDestination = Home) {
        forwardBackwardComposable<Home> {
            HomeScreen(
                onFoodDatabase = { navController.navigateSingleTop(FoodDatabase(null)) },
                onPersonalization = { navController.navigateSingleTop(Personalization) },
                onDataBackupAndExport = { /* TODO */ },
                onLanguage = { navController.navigateSingleTop(Language) },
                onPrivacy = { /* TODO */ },
                onAbout = { navController.navigateSingleTop(About) },
                onAddProfile = { /* TODO */ },
                onEditProfile = { /* TODO */ },
            )
        }
        forwardBackwardComposable<About> {
            AboutScreen(onBack = { navController.popBackStackInclusive<About>() })
        }
        forwardBackwardComposable<Language> {
            LanguageScreen(onBack = { navController.popBackStackInclusive<Language>() })
        }
        forwardBackwardComposable<Personalization> {
            PersonalizationScreen(
                onBack = { navController.popBackStackInclusive<Personalization>() },
                onHome = { /* TODO */ },
                onNutritionFacts = {
                    navController.navigateSingleTop(NutritionFactsPersonalization)
                },
                onColors = { navController.navigateSingleTop(Colors) },
            )
        }
        forwardBackwardComposable<NutritionFactsPersonalization> {
            PersonalizeNutritionFactsScreen(
                onBack = { navController.popBackStackInclusive<NutritionFactsPersonalization>() }
            )
        }
        forwardBackwardComposable<FoodDatabase> {
            val (query) = it.toRoute<FoodDatabase>()

            FoodDatabaseScreen(
                onBack = {
                    navController.navigate(Home) { popUpTo<FoodDatabase> { inclusive = true } }
                },
                onCreateProduct = {
                    // TODO
                },
                onCreateRecipe = {
                    // TODO
                },
                onUpdateUsdaApiKey = {
                    // TODO
                },
                onFood = { identity -> navController.navigateSingleTop(FoodDetails(identity)) },
                query = query,
                animatedVisibilityScope = this,
            )
        }
        forwardBackwardComposable<FoodDetails> {
            val route = it.toRoute<FoodDetails>()

            FoodDetailsScreen(
                identity = route.identity,
                onBack = { navController.popBackStackInclusive<FoodDetails>() },
                onEdit = {
                    // TODO
                },
            )
        }
        forwardBackwardComposable<Colors> {
            ColorsScreen(onBack = { navController.popBackStackInclusive<Colors>() })
        }
    }
}

sealed interface FoodYouNavHostRoute {

    @Serializable data object Home : FoodYouNavHostRoute

    @Serializable data object About : FoodYouNavHostRoute

    @Serializable data object Language : FoodYouNavHostRoute

    @Serializable data object Personalization : FoodYouNavHostRoute

    @Serializable data object NutritionFactsPersonalization : FoodYouNavHostRoute

    @Serializable data class FoodDatabase(val query: String?) : FoodYouNavHostRoute

    @Serializable
    data class FoodDetails(val type: IdentityType, val extra: String) : FoodYouNavHostRoute {
        constructor(
            identity: FoodProductIdentity
        ) : this(
            type =
                when (identity) {
                    is FoodProductIdentity.Local -> IdentityType.Local
                    is FoodProductIdentity.OpenFoodFacts -> IdentityType.OpenFoodFacts
                    is FoodProductIdentity.FoodDataCentral -> IdentityType.FoodDataCentral
                },
            extra =
                when (identity) {
                    is FoodProductIdentity.Local -> identity.id
                    is FoodProductIdentity.OpenFoodFacts -> identity.barcode
                    is FoodProductIdentity.FoodDataCentral -> identity.fdcId.toString()
                },
        )

        val identity: FoodProductIdentity
            get() =
                when (type) {
                    IdentityType.Local -> FoodProductIdentity.Local(extra)
                    IdentityType.OpenFoodFacts -> FoodProductIdentity.OpenFoodFacts(extra)
                    IdentityType.FoodDataCentral ->
                        FoodProductIdentity.FoodDataCentral(extra.toInt())
                }

        enum class IdentityType {
            Local,
            OpenFoodFacts,
            FoodDataCentral,
        }
    }

    @Serializable data object Colors : FoodYouNavHostRoute
}
