package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.About
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Colors
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.CreateProduct
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.CreateProfile
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.CreateRecipe
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.EditProfile
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.EditRecipe
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.EditUserProduct
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDataCentralProductDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Home
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.HomePersonalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Language
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.NutritionFactsPersonalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.OpenFoodFactsProductDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Personalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Privacy
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.SwitchProfile
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.UserProductDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.UserRecipeDetails
import com.maksimowiczm.foodyou.app.ui.about.AboutScreen
import com.maksimowiczm.foodyou.app.ui.food.details.fooddatacentral.FoodDataCentralDetailsScreen
import com.maksimowiczm.foodyou.app.ui.food.details.openfoodfacts.OpenFoodFactsDetailsScreen
import com.maksimowiczm.foodyou.app.ui.food.details.userproduct.UserProductDetailsScreen
import com.maksimowiczm.foodyou.app.ui.food.details.userrecipe.UserRecipeDetailsScreen
import com.maksimowiczm.foodyou.app.ui.food.recipe.create.CreateRecipeScreen
import com.maksimowiczm.foodyou.app.ui.food.recipe.edit.EditRecipeScreen
import com.maksimowiczm.foodyou.app.ui.home.HomePersonalizationScreen
import com.maksimowiczm.foodyou.app.ui.home.HomeScreen
import com.maksimowiczm.foodyou.app.ui.home.SettingsScreen
import com.maksimowiczm.foodyou.app.ui.language.LanguageScreen
import com.maksimowiczm.foodyou.app.ui.personalization.ColorsScreen
import com.maksimowiczm.foodyou.app.ui.personalization.PersonalizationScreen
import com.maksimowiczm.foodyou.app.ui.personalization.PersonalizeNutritionFactsScreen
import com.maksimowiczm.foodyou.app.ui.privacy.PrivacyScreen
import com.maksimowiczm.foodyou.app.ui.profile.add.AddProfileScreen
import com.maksimowiczm.foodyou.app.ui.profile.edit.EditProfileScreen
import com.maksimowiczm.foodyou.app.ui.userproduct.create.CreateProductScreen
import com.maksimowiczm.foodyou.app.ui.userproduct.edit.EditProductScreen
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.extension.removeLastIf
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun FoodYouNavDisplay(backStack: NavBackStack<NavKey>, modifier: Modifier = Modifier) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
                rememberPredictiveBackRoundedCornersDecorator(),
            ),
        transitionSpec = {
            ContentTransform(
                ForwardBackwardTransition.enterTransition(),
                ForwardBackwardTransition.exitTransition(),
            )
        },
        popTransitionSpec = {
            ContentTransform(
                ForwardBackwardTransition.popEnterTransition(),
                ForwardBackwardTransition.popExitTransition(),
            )
        },
        predictivePopTransitionSpec = {
            ContentTransform(
                ForwardBackwardTransition.popEnterTransition(),
                ForwardBackwardTransition.popExitTransition(),
            )
        },
        entryProvider =
            entryProvider {
                entry<About> { AboutScreen(onBack = { backStack.removeLastIf<About>() }) }
                entry<Colors> { ColorsScreen(onBack = { backStack.removeLastIf<Colors>() }) }
                entry<CreateProduct> {
                    CreateProductScreen(
                        onBack = { backStack.removeLastIf<CreateProduct>() },
                        onCreate = { id ->
                            backStack.removeLastIf<CreateProduct>()
                            backStack.add(UserProductDetails(id))
                        },
                    )
                }
                entry<CreateProfile> {
                    AddProfileScreen(
                        onBack = { backStack.removeLastIf<CreateProfile>() },
                        onCreate = { backStack.removeLastIf<CreateProfile>() },
                    )
                }
                entry<EditUserProduct> {
                    EditProductScreen(
                        identity = it.identity,
                        onBack = { backStack.removeLastIf<EditUserProduct>() },
                        onEdit = { backStack.removeLastIf<EditUserProduct>() },
                    )
                }
                entry<EditProfile> {
                    EditProfileScreen(
                        profileId = it.profileId,
                        onBack = { backStack.removeLastIf<EditProfile>() },
                        onEdit = { backStack.removeLastIf<EditProfile>() },
                        onDelete = { backStack.removeLastIf<EditProfile>() },
                    )
                }
                entry<UserProductDetails> {
                    UserProductDetailsScreen(
                        identity = it.identity,
                        onEdit = { backStack.add(EditUserProduct(it.identity)) },
                        onBack = { backStack.removeLastIf<UserProductDetails>() },
                        onDelete = { backStack.removeLastIf<UserProductDetails>() },
                    )
                }
                entry<UserRecipeDetails> {
                    UserRecipeDetailsScreen(
                        identity = it.identity,
                        onEdit = { backStack.add(EditRecipe(it.identity)) },
                        onBack = { backStack.removeLastIf<UserRecipeDetails>() },
                        onDelete = { backStack.removeLastIf<UserRecipeDetails>() },
                        onNavigateToIngredient = { identity ->
                            val route =
                                when (identity) {
                                    is FoodCompositionComponentIdentity.UserProduct ->
                                        UserProductDetails(UserProductIdentity(identity.id))

                                    is FoodCompositionComponentIdentity.OpenFoodFacts ->
                                        OpenFoodFactsProductDetails(
                                            OpenFoodFactsProductIdentity(identity.barcode)
                                        )

                                    is FoodCompositionComponentIdentity.FoodDataCentral ->
                                        FoodDataCentralProductDetails(
                                            FoodDataCentralProductIdentity(identity.fdcId)
                                        )

                                    is FoodCompositionComponentIdentity.Recipe ->
                                        UserRecipeDetails(UserRecipeIdentity(identity.id))
                                }
                            backStack.add(route)
                        },
                    )
                }
                entry<OpenFoodFactsProductDetails> {
                    OpenFoodFactsDetailsScreen(
                        identity = it.identity,
                        onBack = { backStack.removeLastIf<OpenFoodFactsProductDetails>() },
                    )
                }
                entry<FoodDataCentralProductDetails> {
                    FoodDataCentralDetailsScreen(
                        identity = it.identity,
                        onBack = { backStack.removeLastIf<FoodDataCentralProductDetails>() },
                    )
                }
                entry<Home> {
                    HomeScreen(
                        onAvatar = { backStack.add(SwitchProfile) },
                        onFoodDataCentralProduct = { id ->
                            backStack.add(FoodDataCentralProductDetails(id))
                        },
                        onOpenFoodFactsProduct = { id ->
                            backStack.add(OpenFoodFactsProductDetails(id))
                        },
                        onUserProduct = { id -> backStack.add(UserProductDetails(id)) },
                        onUserRecipe = { id -> backStack.add(UserRecipeDetails(id)) },
                        onCreateProduct = { backStack.add(CreateProduct) },
                        onCreateRecipe = { backStack.add(CreateRecipe) },
                        initialQuery = it.initialQuery,
                    )
                }
                entry<SwitchProfile> {
                    SettingsScreen(
                        onBack = { backStack.removeLastIf<SwitchProfile>() },
                        onPersonalization = { backStack.add(Personalization) },
                        onLanguage = { backStack.add(Language) },
                        onPrivacy = { backStack.add(Privacy) },
                        onAbout = { backStack.add(About) },
                        onAddProfile = { backStack.add(CreateProfile) },
                        onEditProfile = { backStack.add(EditProfile(it)) },
                    )
                }
                entry<HomePersonalization> {
                    HomePersonalizationScreen(
                        onBack = { backStack.removeLastIf<HomePersonalization>() }
                    )
                }
                entry<Language> { LanguageScreen(onBack = { backStack.removeLastIf<Language>() }) }
                entry<NutritionFactsPersonalization> {
                    PersonalizeNutritionFactsScreen(
                        onBack = { backStack.removeLastIf<NutritionFactsPersonalization>() }
                    )
                }
                entry<Personalization> {
                    PersonalizationScreen(
                        onBack = { backStack.removeLastIf<Personalization>() },
                        onHome = { backStack.add(HomePersonalization) },
                        onNutritionFacts = { backStack.add(NutritionFactsPersonalization) },
                        onColors = { backStack.add(Colors) },
                    )
                }
                entry<Privacy> { PrivacyScreen(onBack = { backStack.removeLastIf<Privacy>() }) }
                entry<CreateRecipe> {
                    CreateRecipeScreen(
                        onBack = { backStack.removeLastIf<CreateRecipe>() },
                        onCreate = { id ->
                            backStack.removeLastIf<CreateRecipe>()
                            backStack.add(UserRecipeDetails(id))
                        },
                        onEditUserProduct = { backStack.add(EditUserProduct(it)) },
                    )
                }
                entry<EditRecipe> {
                    EditRecipeScreen(
                        identity = it.identity,
                        onBack = { backStack.removeLastIf<EditRecipe>() },
                        onEditUserProduct = { id -> backStack.add(EditUserProduct(id)) },
                    )
                }
            },
    )
}

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun rememberFoodYouNavBackStack(
    vararg elements: FoodYouNavHostRoute = arrayOf(Home(null))
): NavBackStack<NavKey> {
    val config = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(NavKey::class) { subclassesOfSealed<FoodYouNavHostRoute>() }
        }
    }

    return rememberNavBackStack(config, *elements)
}

@Serializable
sealed interface FoodYouNavHostRoute : NavKey {

    @Serializable data object About : FoodYouNavHostRoute

    @Serializable data object Colors : FoodYouNavHostRoute

    @Serializable data object CreateProduct : FoodYouNavHostRoute

    @Serializable data object CreateProfile : FoodYouNavHostRoute

    @Serializable
    data class EditUserProduct(val identity: UserProductIdentity) : FoodYouNavHostRoute

    @Serializable data class EditProfile(val profileId: ProfileId) : FoodYouNavHostRoute

    @Serializable
    data class UserProductDetails(val identity: UserProductIdentity) : FoodYouNavHostRoute

    @Serializable
    data class UserRecipeDetails(val identity: UserRecipeIdentity) : FoodYouNavHostRoute

    @Serializable
    data class OpenFoodFactsProductDetails(val identity: OpenFoodFactsProductIdentity) :
        FoodYouNavHostRoute

    @Serializable
    data class FoodDataCentralProductDetails(val identity: FoodDataCentralProductIdentity) :
        FoodYouNavHostRoute

    @Serializable data class Home(val initialQuery: String?) : FoodYouNavHostRoute

    @Serializable data object SwitchProfile : FoodYouNavHostRoute

    @Serializable data object HomePersonalization : FoodYouNavHostRoute

    @Serializable data object Language : FoodYouNavHostRoute

    @Serializable data object NutritionFactsPersonalization : FoodYouNavHostRoute

    @Serializable data object Personalization : FoodYouNavHostRoute

    @Serializable data object Privacy : FoodYouNavHostRoute

    @Serializable data object CreateRecipe : FoodYouNavHostRoute

    @Serializable data class EditRecipe(val identity: UserRecipeIdentity) : FoodYouNavHostRoute
}
