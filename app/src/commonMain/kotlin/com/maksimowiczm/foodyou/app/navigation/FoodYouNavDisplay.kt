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
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.LocalNavAnimatedContentScope
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
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDataCentralAddDiaryEntry
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDataCentralApiKeyDialog
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDataCentralProductDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Home
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Language
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.MealSchedule
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.NutritionFactsPersonalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.OpenFoodFactsAddDiaryEntry
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.OpenFoodFactsLoginDialog
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.OpenFoodFactsProductDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Personalization
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.Privacy
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.SwitchProfile
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.UpdateDiaryEntry
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.UserProductAddDiaryEntry
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.UserProductDetails
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.UserRecipeAddDiaryEntry
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.UserRecipeDetails
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.extension.removeLastIf
import com.maksimowiczm.foodyou.common.extension.removeWhile
import com.maksimowiczm.foodyou.features.about.AboutScreen
import com.maksimowiczm.foodyou.features.diary.AddFoodDataCentralDiaryEntryScreen
import com.maksimowiczm.foodyou.features.diary.AddOpenFoodFactsDiaryEntryScreen
import com.maksimowiczm.foodyou.features.diary.AddUserProductDiaryEntryScreen
import com.maksimowiczm.foodyou.features.diary.AddUserRecipeDiaryEntryScreen
import com.maksimowiczm.foodyou.features.diary.UpdateFoodDiaryEntryScreenDispatcher
import com.maksimowiczm.foodyou.features.fooddatacentral.UpdateFoodDataCentralApiKeyDialog
import com.maksimowiczm.foodyou.features.fooddetails.FoodDataCentralDetailsScreen
import com.maksimowiczm.foodyou.features.fooddetails.OpenFoodFactsDetailsScreen
import com.maksimowiczm.foodyou.features.fooddetails.UserProductDetailsScreen
import com.maksimowiczm.foodyou.features.fooddetails.UserRecipeDetailsScreen
import com.maksimowiczm.foodyou.features.home.ui.HomeScreen
import com.maksimowiczm.foodyou.features.language.LanguageScreen
import com.maksimowiczm.foodyou.features.meal.MealScheduleScreen
import com.maksimowiczm.foodyou.features.openfoodfacts.OpenFoodFactsLoginDialog
import com.maksimowiczm.foodyou.features.personalization.ColorsScreen
import com.maksimowiczm.foodyou.features.personalization.PersonalizationScreen
import com.maksimowiczm.foodyou.features.personalization.PersonalizeNutritionFactsScreen
import com.maksimowiczm.foodyou.features.privacy.PrivacyScreen
import com.maksimowiczm.foodyou.features.profile.add.AddProfileScreen
import com.maksimowiczm.foodyou.features.profile.edit.EditProfileScreen
import com.maksimowiczm.foodyou.features.settings.SettingsScreen
import com.maksimowiczm.foodyou.features.userproduct.create.CreateProductScreen
import com.maksimowiczm.foodyou.features.userproduct.edit.EditProductScreen
import com.maksimowiczm.foodyou.features.userrecipe.create.CreateRecipeScreen
import com.maksimowiczm.foodyou.features.userrecipe.edit.EditRecipeScreen
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun FoodYouNavDisplay(backStack: NavBackStack<NavKey>, modifier: Modifier = Modifier) {
    val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
                rememberPredictiveBackRoundedCornersDecorator(),
            ),
        sceneStrategies = listOf(dialogStrategy),
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
                entry<CreateProduct> { route ->
                    CreateProductScreen(
                        onBack = { backStack.removeLastIf<CreateProduct>() },
                        onCreate = { id ->
                            backStack.removeLastIf<CreateProduct>()
                            val mealId = route.mealId
                            if (mealId != null) {
                                backStack.add(
                                    UserProductAddDiaryEntry(
                                        id = id,
                                        quantity = null,
                                        mealId = mealId,
                                        date = route.date,
                                    )
                                )
                            } else {
                                backStack.add(UserProductDetails(id, null))
                            }
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
                        id = it.id,
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
                        id = it.id,
                        initialQuantity = it.quantity,
                        onEdit = { backStack.add(EditUserProduct(it.id)) },
                        onBack = { backStack.removeLastIf<UserProductDetails>() },
                        onDelete = { backStack.removeLastIf<UserProductDetails>() },
                    )
                }
                entry<UserRecipeDetails> {
                    UserRecipeDetailsScreen(
                        id = it.id,
                        initialQuantity = it.quantity,
                        onEdit = { backStack.add(EditRecipe(it.id)) },
                        onBack = { backStack.removeLastIf<UserRecipeDetails>() },
                        onDelete = { backStack.removeLastIf<UserRecipeDetails>() },
                        onNavigateToIngredient = { id, quantity ->
                            val route =
                                when (id) {
                                    is FoodSnapshotId.UserProduct ->
                                        UserProductDetails(
                                            UserProductId(id.id),
                                            quantity,
                                        )

                                    is FoodSnapshotId.OpenFoodFacts ->
                                        OpenFoodFactsProductDetails(
                                            OpenFoodFactsProductId(id.barcode),
                                            quantity,
                                        )

                                    is FoodSnapshotId.FoodDataCentral ->
                                        FoodDataCentralProductDetails(
                                            FoodDataCentralProductId(id.fdcId),
                                            quantity,
                                        )

                                    is FoodSnapshotId.UserRecipe ->
                                        UserRecipeDetails(UserRecipeId(id.id), quantity)

                                    is FoodSnapshotId.Anonymous -> TODO()
                                }
                            backStack.add(route)
                        },
                    )
                }
                entry<OpenFoodFactsProductDetails> {
                    OpenFoodFactsDetailsScreen(
                        id = it.id,
                        initialQuantity = it.quantity,
                        onBack = { backStack.removeLastIf<OpenFoodFactsProductDetails>() },
                    )
                }
                entry<FoodDataCentralProductDetails> {
                    FoodDataCentralDetailsScreen(
                        id = it.id,
                        initialQuantity = it.quantity,
                        onBack = { backStack.removeLastIf<FoodDataCentralProductDetails>() },
                    )
                }
                entry<Home> {
                    HomeScreen(
                        onAvatar = { backStack.add(SwitchProfile) },
                        onFoodDataCentralProduct = { id, quantity, mealId, date ->
                            if (mealId == null)
                                backStack.add(FoodDataCentralProductDetails(id, quantity))
                            else
                                backStack.add(
                                    FoodDataCentralAddDiaryEntry(id, quantity, mealId, date)
                                )
                        },
                        onOpenFoodFactsProduct = { id, quantity, mealId, date ->
                            if (mealId == null)
                                backStack.add(OpenFoodFactsProductDetails(id, quantity))
                            else
                                backStack.add(
                                    OpenFoodFactsAddDiaryEntry(id, quantity, mealId, date)
                                )
                        },
                        onUserProduct = { id, quantity, mealId, date ->
                            if (mealId == null) backStack.add(UserProductDetails(id, quantity))
                            else backStack.add(UserProductAddDiaryEntry(id, quantity, mealId, date))
                        },
                        onUserRecipe = { id, quantity, mealId, date ->
                            if (mealId == null) backStack.add(UserRecipeDetails(id, quantity))
                            else backStack.add(UserRecipeAddDiaryEntry(id, quantity, mealId, date))
                        },
                        onCreateProduct = { mealId, date ->
                            backStack.add(CreateProduct(mealId, date))
                        },
                        onCreateRecipe = { mealId, date ->
                            backStack.add(CreateRecipe(mealId, date))
                        },
                        onEntry = { backStack.add(UpdateDiaryEntry(it)) },
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
                entry<Language> { LanguageScreen(onBack = { backStack.removeLastIf<Language>() }) }
                entry<NutritionFactsPersonalization> {
                    PersonalizeNutritionFactsScreen(
                        onBack = { backStack.removeLastIf<NutritionFactsPersonalization>() }
                    )
                }
                entry<Personalization> {
                    PersonalizationScreen(
                        onBack = { backStack.removeLastIf<Personalization>() },
                        onNutritionFacts = { backStack.add(NutritionFactsPersonalization) },
                        onMeals = { backStack.add(MealSchedule) },
                        onColors = { backStack.add(Colors) },
                    )
                }
                entry<Privacy> {
                    PrivacyScreen(
                        onBack = { backStack.removeLastIf<Privacy>() },
                        onFoodDataCentralApiKey = { backStack.add(FoodDataCentralApiKeyDialog) },
                        onOpenFoodFactsLogin = { backStack.add(OpenFoodFactsLoginDialog) },
                    )
                }
                entry<CreateRecipe> { route ->
                    CreateRecipeScreen(
                        onBack = { backStack.removeLastIf<CreateRecipe>() },
                        onCreate = { id ->
                            backStack.removeLastIf<CreateRecipe>()
                            val mealId = route.mealId
                            if (mealId != null) {
                                backStack.add(
                                    UserRecipeAddDiaryEntry(
                                        id = id,
                                        quantity = null,
                                        mealId = mealId,
                                        date = route.date,
                                    )
                                )
                            } else {
                                backStack.add(UserRecipeDetails(id, null))
                            }
                        },
                        onEditUserProduct = { backStack.add(EditUserProduct(it)) },
                        onEditUserRecipe = { backStack.add(EditRecipe(it)) },
                    )
                }
                entry<EditRecipe> {
                    EditRecipeScreen(
                        id = it.id,
                        onBack = { backStack.removeLastIf<EditRecipe>() },
                        onEditUserProduct = { id -> backStack.add(EditUserProduct(id)) },
                        onEditUserRecipe = { id -> backStack.add(EditRecipe(id)) },
                    )
                }
                entry<MealSchedule> {
                    MealScheduleScreen(onBack = { backStack.removeLastIf<MealSchedule>() })
                }
                entry<FoodDataCentralApiKeyDialog>(metadata = DialogSceneStrategy.dialog()) {
                    UpdateFoodDataCentralApiKeyDialog(
                        onDismissRequest = {
                            backStack.removeLastIf<FoodDataCentralApiKeyDialog>()
                        },
                        onSave = { backStack.removeLastIf<FoodDataCentralApiKeyDialog>() },
                    )
                }
                entry<OpenFoodFactsLoginDialog>(metadata = DialogSceneStrategy.dialog()) {
                    OpenFoodFactsLoginDialog(
                        onDismissRequest = { backStack.removeLastIf<OpenFoodFactsLoginDialog>() },
                        onSave = { backStack.removeLastIf<OpenFoodFactsLoginDialog>() },
                    )
                }
                entry<FoodDataCentralAddDiaryEntry> { route ->
                    context(LocalNavAnimatedContentScope.current) {
                        AddFoodDataCentralDiaryEntryScreen(
                            onBack = { backStack.removeLastIf<FoodDataCentralAddDiaryEntry>() },
                            onAdd = {
                                if (route.popToHome) backStack.removeWhile { it !is Home }
                                else backStack.removeLastIf<FoodDataCentralAddDiaryEntry>()
                            },
                            mealId = route.mealId,
                            id = route.id,
                            initialQuantity = route.quantity,
                            date = route.date,
                        )
                    }
                }
                entry<OpenFoodFactsAddDiaryEntry> {
                    context(LocalNavAnimatedContentScope.current) {
                        AddOpenFoodFactsDiaryEntryScreen(
                            onBack = { backStack.removeLastIf<OpenFoodFactsAddDiaryEntry>() },
                            onAdd = {
                                if (it.popToHome) backStack.removeWhile { route -> route !is Home }
                                else backStack.removeLastIf<OpenFoodFactsAddDiaryEntry>()
                            },
                            mealId = it.mealId,
                            id = it.id,
                            initialQuantity = it.quantity,
                            date = it.date,
                        )
                    }
                }
                entry<UserProductAddDiaryEntry> {
                    context(LocalNavAnimatedContentScope.current) {
                        AddUserProductDiaryEntryScreen(
                            onBack = { backStack.removeLastIf<UserProductAddDiaryEntry>() },
                            onAdd = {
                                if (it.popToHome) backStack.removeWhile { route -> route !is Home }
                                else backStack.removeLastIf<UserProductAddDiaryEntry>()
                            },
                            onEdit = { backStack.add(EditUserProduct(it.id)) },
                            onDelete = { backStack.removeLastIf<UserProductAddDiaryEntry>() },
                            mealId = it.mealId,
                            id = it.id,
                            initialQuantity = it.quantity,
                            date = it.date,
                        )
                    }
                }
                entry<UserRecipeAddDiaryEntry> {
                    context(LocalNavAnimatedContentScope.current) {
                        AddUserRecipeDiaryEntryScreen(
                            onBack = { backStack.removeLastIf<UserRecipeAddDiaryEntry>() },
                            onAdd = {
                                if (it.popToHome) backStack.removeWhile { route -> route !is Home }
                                else backStack.removeLastIf<UserRecipeAddDiaryEntry>()
                            },
                            onEdit = { backStack.add(EditRecipe(it.id)) },
                            onDelete = { backStack.removeLastIf<UserRecipeAddDiaryEntry>() },
                            onNavigateToIngredient = { id, quantity ->
                                val ingredientRoute =
                                    when (id) {
                                        is FoodSnapshotId.UserProduct ->
                                            UserProductAddDiaryEntry(
                                                UserProductId(id.id),
                                                quantity,
                                                it.mealId,
                                                popToHome = true,
                                                date = it.date,
                                            )

                                        is FoodSnapshotId.OpenFoodFacts ->
                                            OpenFoodFactsAddDiaryEntry(
                                                OpenFoodFactsProductId(id.barcode),
                                                quantity,
                                                it.mealId,
                                                popToHome = true,
                                                date = it.date,
                                            )

                                        is FoodSnapshotId.FoodDataCentral ->
                                            FoodDataCentralAddDiaryEntry(
                                                FoodDataCentralProductId(id.fdcId),
                                                quantity,
                                                it.mealId,
                                                popToHome = true,
                                                date = it.date,
                                            )

                                        is FoodSnapshotId.UserRecipe ->
                                            UserRecipeAddDiaryEntry(
                                                UserRecipeId(id.id),
                                                quantity,
                                                it.mealId,
                                                popToHome = true,
                                                date = it.date,
                                            )

                                        is FoodSnapshotId.Anonymous -> TODO()
                                    }
                                backStack.add(ingredientRoute)
                            },
                            mealId = it.mealId,
                            id = it.id,
                            initialQuantity = it.quantity,
                            date = it.date,
                        )
                    }
                }
                entry<UpdateDiaryEntry> {
                    context(LocalNavAnimatedContentScope.current) {
                        UpdateFoodDiaryEntryScreenDispatcher(
                            onBack = { backStack.removeLastIf<UpdateDiaryEntry>() },
                            onUpdate = { backStack.removeLastIf<UpdateDiaryEntry>() },
                            onEditUserProduct = { id -> backStack.add(EditUserProduct(id)) },
                            onDeleteUserProduct = { backStack.removeLastIf<UpdateDiaryEntry>() },
                            onEditUserRecipe = { id -> backStack.add(EditRecipe(id)) },
                            onDeleteUserRecipe = { backStack.removeLastIf<UpdateDiaryEntry>() },
                            onNavigateToIngredient = { id, quantity, _, _ ->
                                val ingredientRoute =
                                    when (id) {
                                        is FoodSnapshotId.FoodDataCentral ->
                                            FoodDataCentralProductDetails(
                                                FoodDataCentralProductId(id.fdcId),
                                                quantity,
                                            )

                                        is FoodSnapshotId.OpenFoodFacts ->
                                            OpenFoodFactsProductDetails(
                                                OpenFoodFactsProductId(id.barcode),
                                                quantity,
                                            )

                                        is FoodSnapshotId.UserProduct ->
                                            UserProductDetails(
                                                UserProductId(id.id),
                                                quantity,
                                            )

                                        is FoodSnapshotId.UserRecipe ->
                                            UserRecipeDetails(
                                                UserRecipeId(id.id),
                                                quantity,
                                            )

                                        is FoodSnapshotId.Anonymous -> TODO()
                                    }
                                backStack.add(ingredientRoute)
                            },
                            entryIdentity = it.id,
                        )
                    }
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

    @Serializable
    data class CreateProduct(val mealId: MealId? = null, val date: LocalDate? = null) :
        FoodYouNavHostRoute

    @Serializable data object CreateProfile : FoodYouNavHostRoute

    @Serializable data class EditUserProduct(val id: UserProductId) : FoodYouNavHostRoute

    @Serializable data class EditProfile(val profileId: ProfileId) : FoodYouNavHostRoute

    @Serializable
    data class UserProductDetails(val id: UserProductId, val quantity: Quantity?) :
        FoodYouNavHostRoute

    @Serializable
    data class UserRecipeDetails(val id: UserRecipeId, val quantity: Quantity?) :
        FoodYouNavHostRoute

    @Serializable
    data class OpenFoodFactsProductDetails(
        val id: OpenFoodFactsProductId,
        val quantity: Quantity?,
    ) : FoodYouNavHostRoute

    @Serializable
    data class FoodDataCentralProductDetails(
        val id: FoodDataCentralProductId,
        val quantity: Quantity?,
    ) : FoodYouNavHostRoute

    @Serializable data class Home(val initialQuery: String?) : FoodYouNavHostRoute

    @Serializable data object SwitchProfile : FoodYouNavHostRoute

    @Serializable data object Language : FoodYouNavHostRoute

    @Serializable data object NutritionFactsPersonalization : FoodYouNavHostRoute

    @Serializable data object Personalization : FoodYouNavHostRoute

    @Serializable data object Privacy : FoodYouNavHostRoute

    @Serializable
    data class CreateRecipe(val mealId: MealId? = null, val date: LocalDate? = null) :
        FoodYouNavHostRoute

    @Serializable data class EditRecipe(val id: UserRecipeId) : FoodYouNavHostRoute

    @Serializable data object MealSchedule : FoodYouNavHostRoute

    @Serializable data object FoodDataCentralApiKeyDialog : FoodYouNavHostRoute

    @Serializable data object OpenFoodFactsLoginDialog : FoodYouNavHostRoute

    @Serializable
    data class FoodDataCentralAddDiaryEntry(
        val id: FoodDataCentralProductId,
        val quantity: Quantity?,
        val mealId: MealId,
        val date: LocalDate?,
        val popToHome: Boolean = false,
    ) : FoodYouNavHostRoute

    @Serializable
    data class OpenFoodFactsAddDiaryEntry(
        val id: OpenFoodFactsProductId,
        val quantity: Quantity?,
        val mealId: MealId,
        val date: LocalDate?,
        val popToHome: Boolean = false,
    ) : FoodYouNavHostRoute

    @Serializable
    data class UserProductAddDiaryEntry(
        val id: UserProductId,
        val quantity: Quantity?,
        val mealId: MealId,
        val date: LocalDate?,
        val popToHome: Boolean = false,
    ) : FoodYouNavHostRoute

    @Serializable
    data class UserRecipeAddDiaryEntry(
        val id: UserRecipeId,
        val quantity: Quantity?,
        val mealId: MealId,
        val date: LocalDate?,
        val popToHome: Boolean = false,
    ) : FoodYouNavHostRoute

    @Serializable data class UpdateDiaryEntry(val id: FoodDiaryEntryId) : FoodYouNavHostRoute
}
