package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.About
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Colors
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.CreateProduct
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.CreateProfile
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.EditProduct
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.EditProfile
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDatabase
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Home
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.HomePersonalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Language
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.NutritionFactsPersonalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Personalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Privacy
import com.maksimowiczm.foodyou.app.ui.about.AboutScreen
import com.maksimowiczm.foodyou.app.ui.food.FoodDatabaseScreen
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsScreen
import com.maksimowiczm.foodyou.app.ui.home.HomePersonalizationScreen
import com.maksimowiczm.foodyou.app.ui.home.HomeScreen
import com.maksimowiczm.foodyou.app.ui.home.homeCardComposables
import com.maksimowiczm.foodyou.app.ui.language.LanguageScreen
import com.maksimowiczm.foodyou.app.ui.personalization.ColorsScreen
import com.maksimowiczm.foodyou.app.ui.personalization.PersonalizationScreen
import com.maksimowiczm.foodyou.app.ui.personalization.PersonalizeNutritionFactsScreen
import com.maksimowiczm.foodyou.app.ui.privacy.PrivacyScreen
import com.maksimowiczm.foodyou.app.ui.product.create.CreateProductScreen
import com.maksimowiczm.foodyou.app.ui.product.edit.EditProductScreen
import com.maksimowiczm.foodyou.app.ui.profile.add.AddProfileScreen
import com.maksimowiczm.foodyou.app.ui.profile.edit.EditProfileScreen
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import kotlinx.serialization.Serializable

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(modifier = modifier, navController = navController, startDestination = Home) {
        homeCardComposables.forEach { feature -> with(feature) { navigationGraph(navController) } }

        forwardBackwardComposable<Home> {
            HomeScreen(
                navController = navController,
                onFoodDatabase = { navController.navigateSingleTop(FoodDatabase(null)) },
                onPersonalization = { navController.navigateSingleTop(Personalization) },
                onDataBackupAndExport = { /* TODO */ },
                onLanguage = { navController.navigateSingleTop(Language) },
                onPrivacy = { navController.navigateSingleTop(Privacy) },
                onAbout = { navController.navigateSingleTop(About) },
                onAddProfile = { navController.navigateSingleTop(CreateProfile) },
                onEditProfile = { navController.navigateSingleTop(EditProfile.from(it)) },
            )
        }
        forwardBackwardComposable<HomePersonalization> {
            HomePersonalizationScreen(
                onBack = { navController.popBackStackInclusive<HomePersonalization>() },
                navController = navController,
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
                onHome = { navController.navigateSingleTop(HomePersonalization) },
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
                onBack = { navController.popBackStackInclusive<FoodDatabase>() },
                onCreateProduct = { navController.navigateSingleTop(CreateProduct) },
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
                    navController.navigateSingleTop(
                        EditProduct.from(route.identity as FoodProductIdentity.Local)
                    )
                },
            )
        }
        forwardBackwardComposable<Colors> {
            ColorsScreen(onBack = { navController.popBackStackInclusive<Colors>() })
        }
        forwardBackwardComposable<Privacy> {
            PrivacyScreen(onBack = { navController.popBackStackInclusive<Privacy>() })
        }
        forwardBackwardComposable<CreateProfile> {
            AddProfileScreen(
                onBack = { navController.popBackStackInclusive<CreateProfile>() },
                onCreate = { navController.popBackStackInclusive<CreateProfile>() },
            )
        }
        forwardBackwardComposable<EditProfile> {
            val route = it.toRoute<EditProfile>()

            EditProfileScreen(
                profileId = route.profileId,
                onBack = { navController.popBackStackInclusive<EditProfile>() },
                onEdit = { navController.popBackStackInclusive<EditProfile>() },
                onDelete = { navController.popBackStackInclusive<EditProfile>() },
            )
        }
        forwardBackwardComposable<CreateProduct> {
            CreateProductScreen(
                onBack = { navController.popBackStackInclusive<CreateProduct>() },
                onCreate = { id ->
                    navController.navigate(FoodDetails(id)) {
                        popUpTo<CreateProduct> { inclusive = true }
                    }
                },
            )
        }
        forwardBackwardComposable<EditProduct> {
            EditProductScreen(
                identity = it.toRoute<EditProduct>().identity,
                onBack = { navController.popBackStackInclusive<EditProduct>() },
                onEdit = { navController.popBackStackInclusive<EditProduct>() },
            )
        }
    }
}

sealed interface FoodYouNavHostRoute {

    @Serializable data object Home : FoodYouNavHostRoute

    @Serializable data object HomePersonalization : FoodYouNavHostRoute

    @Serializable data object About : FoodYouNavHostRoute

    @Serializable data object Language : FoodYouNavHostRoute

    @Serializable data object Personalization : FoodYouNavHostRoute

    @Serializable data object NutritionFactsPersonalization : FoodYouNavHostRoute

    @Serializable data class FoodDatabase(val query: String?) : FoodYouNavHostRoute

    @Serializable
    data class FoodDetails(val type: IdentityType, val extra: String, val extra1: String?) :
        FoodYouNavHostRoute {
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
            extra1 =
                when (identity) {
                    is FoodProductIdentity.Local -> identity.accountId.value
                    is FoodProductIdentity.OpenFoodFacts -> null
                    is FoodProductIdentity.FoodDataCentral -> null
                },
        )

        val identity: FoodProductIdentity
            get() =
                when (type) {
                    IdentityType.Local -> FoodProductIdentity.Local(extra, LocalAccountId(extra1!!))
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

    @Serializable data object Privacy : FoodYouNavHostRoute

    @Serializable data object CreateProfile : FoodYouNavHostRoute

    @Serializable
    data class EditProfile(val id: String) : FoodYouNavHostRoute {
        companion object {
            fun from(profileId: ProfileId): EditProfile = EditProfile(profileId.value)
        }

        val profileId: ProfileId
            get() = ProfileId(id)
    }

    @Serializable data object CreateProduct : FoodYouNavHostRoute

    @Serializable
    data class EditProduct(val id: String, val accountId: String) : FoodYouNavHostRoute {
        companion object {
            fun from(identity: FoodProductIdentity.Local): EditProduct =
                EditProduct(identity.id, identity.accountId.value)
        }

        val identity: FoodProductIdentity.Local
            get() = FoodProductIdentity.Local(id, LocalAccountId(accountId))
    }
}
