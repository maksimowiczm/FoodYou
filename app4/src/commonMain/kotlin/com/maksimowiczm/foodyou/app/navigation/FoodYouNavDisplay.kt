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
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.About
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Colors
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.CreateProduct
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.CreateProfile
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.EditProduct
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.EditProfile
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDataCentralProductDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDatabase
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Home
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.HomePersonalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Language
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.NutritionFactsPersonalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.OpenFoodFactsProductDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Personalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Privacy
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.UserFoodDetails
import com.maksimowiczm.foodyou.app.ui.about.AboutScreen
import com.maksimowiczm.foodyou.app.ui.food.FoodDatabaseScreen
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsScreen
import com.maksimowiczm.foodyou.app.ui.home.HomePersonalizationScreen
import com.maksimowiczm.foodyou.app.ui.home.HomeScreen
import com.maksimowiczm.foodyou.app.ui.language.LanguageScreen
import com.maksimowiczm.foodyou.app.ui.personalization.ColorsScreen
import com.maksimowiczm.foodyou.app.ui.personalization.PersonalizationScreen
import com.maksimowiczm.foodyou.app.ui.personalization.PersonalizeNutritionFactsScreen
import com.maksimowiczm.foodyou.app.ui.privacy.PrivacyScreen
import com.maksimowiczm.foodyou.app.ui.profile.add.AddProfileScreen
import com.maksimowiczm.foodyou.app.ui.profile.edit.EditProfileScreen
import com.maksimowiczm.foodyou.app.ui.userfood.create.CreateProductScreen
import com.maksimowiczm.foodyou.app.ui.userfood.edit.EditProductScreen
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.extension.removeLastIf
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Composable
fun FoodYouNavDisplay(
    backStack: NavBackStack<NavKey> = rememberFoodYouNavBackStack(),
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
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
                            backStack.add(UserFoodDetails(id))
                        },
                    )
                }
                entry<CreateProfile> {
                    AddProfileScreen(
                        onBack = { backStack.removeLastIf<CreateProfile>() },
                        onCreate = { backStack.removeLastIf<CreateProfile>() },
                    )
                }
                entry<EditProduct> {
                    EditProductScreen(
                        identity = it.identity,
                        onBack = { backStack.removeLastIf<EditProduct>() },
                        onEdit = { backStack.removeLastIf<EditProduct>() },
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
                entry<FoodDatabase> {
                    FoodDatabaseScreen(
                        onBack = { backStack.removeLastIf<FoodDatabase>() },
                        onCreateProduct = { backStack.add(CreateProduct) },
                        onFoodDataCentralProduct = {},
                        onOpenFoodFactsProduct = { id ->
                            backStack.add(OpenFoodFactsProductDetails.from(id))
                        },
                        onUserFood = { id -> backStack.add(UserFoodDetails(id)) },
                        query = it.query,
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                    )
                }
                entry<UserFoodDetails> {
                    FoodDetailsScreen(
                        identity = it.identity,
                        onBack = { backStack.removeLastIf<UserFoodDetails>() },
                        onEdit = { id -> backStack.add(EditProduct.from(id)) },
                    )
                }
                entry<OpenFoodFactsProductDetails> {
                    FoodDetailsScreen(
                        identity = it.identity,
                        onBack = { backStack.removeLastIf<OpenFoodFactsProductDetails>() },
                        onEdit = { error("Not possible") },
                    )
                }
                entry<FoodDataCentralProductDetails> {
                    FoodDetailsScreen(
                        identity = it.identity,
                        onBack = { backStack.removeLastIf<FoodDataCentralProductDetails>() },
                        onEdit = { error("Not possible") },
                    )
                }
                entry<Home> {
                    HomeScreen(
                        onFoodDatabase = { backStack.add(FoodDatabase(null)) },
                        onPersonalization = { backStack.add(Personalization) },
                        onDataBackupAndExport = { /* TODO */ },
                        onLanguage = { backStack.add(Language) },
                        onPrivacy = { backStack.add(Privacy) },
                        onAbout = { backStack.add(About) },
                        onAddProfile = { backStack.add(CreateProfile) },
                        onEditProfile = { backStack.add(EditProfile.from(it)) },
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
            },
    )
}

@Suppress("UNCHECKED_CAST")
@Composable
fun rememberFoodYouNavBackStack(): NavBackStack<NavKey> {
    val config = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(About.serializer())
                subclass(Colors.serializer())
                subclass(CreateProduct.serializer())
                subclass(CreateProfile.serializer())
                subclass(EditProduct.serializer())
                subclass(EditProfile.serializer())
                subclass(FoodDatabase.serializer())
                subclass(UserFoodDetails.serializer())
                subclass(OpenFoodFactsProductDetails.serializer())
                subclass(FoodDataCentralProductDetails.serializer())
                subclass(Home.serializer())
                subclass(HomePersonalization.serializer())
                subclass(Language.serializer())
                subclass(NutritionFactsPersonalization.serializer())
                subclass(Personalization.serializer())
                subclass(Privacy.serializer())
            }
        }
    }

    return rememberNavBackStack(config, Home)
}

@Serializable
sealed interface FoodYouNavHostRoute : NavKey {

    @Serializable data object About : FoodYouNavHostRoute

    @Serializable data object Colors : FoodYouNavHostRoute

    @Serializable data object CreateProduct : FoodYouNavHostRoute

    @Serializable data object CreateProfile : FoodYouNavHostRoute

    @Serializable
    data class EditProduct(val id: String, val accountId: String) : FoodYouNavHostRoute {
        companion object {
            fun from(identity: UserFoodProductIdentity): EditProduct =
                EditProduct(identity.id, identity.accountId.value)
        }

        val identity: UserFoodProductIdentity
            get() = UserFoodProductIdentity(id, LocalAccountId(accountId))
    }

    @Serializable
    data class EditProfile(val id: String) : FoodYouNavHostRoute {
        companion object {
            fun from(profileId: ProfileId): EditProfile = EditProfile(profileId.value)
        }

        val profileId: ProfileId
            get() = ProfileId(id)
    }

    @Serializable data class FoodDatabase(val query: String?) : FoodYouNavHostRoute

    @Serializable
    data class UserFoodDetails(val id: String, val accountId: String) : FoodYouNavHostRoute {
        val identity: UserFoodProductIdentity
            get() = UserFoodProductIdentity(id, LocalAccountId(accountId))

        constructor(identity: UserFoodProductIdentity) : this(identity.id, identity.accountId.value)
    }

    @Serializable
    data class OpenFoodFactsProductDetails(val barcode: String) : FoodYouNavHostRoute {
        val identity: OpenFoodFactsProductIdentity
            get() = OpenFoodFactsProductIdentity(barcode)

        companion object {
            fun from(identity: OpenFoodFactsProductIdentity) =
                OpenFoodFactsProductDetails(identity.barcode)
        }
    }

    @Serializable
    data class FoodDataCentralProductDetails(val fdcId: Int) : FoodYouNavHostRoute {
        val identity: FoodDataCentralProductIdentity
            get() = FoodDataCentralProductIdentity(fdcId)

        companion object {
            fun from(identity: FoodDataCentralProductIdentity) =
                FoodDataCentralProductDetails(identity.fdcId)
        }
    }

    @Serializable data object Home : FoodYouNavHostRoute

    @Serializable data object HomePersonalization : FoodYouNavHostRoute

    @Serializable data object Language : FoodYouNavHostRoute

    @Serializable data object NutritionFactsPersonalization : FoodYouNavHostRoute

    @Serializable data object Personalization : FoodYouNavHostRoute

    @Serializable data object Privacy : FoodYouNavHostRoute
}
