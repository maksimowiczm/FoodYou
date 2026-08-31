package com.maksimowiczm.foodyou.features.home.ui

import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.AnonymousFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class HomeUiStateProvider {
    private val profileId = ProfileId()
    private val activeMealId = MealId()

    val uiState =
        HomeUiState(
            profiles =
                listOf(
                    Profile(
                        id = profileId,
                        name = "Mateusz",
                        avatar = Profile.Avatar.Predefined.Variant.Person.toAvatar(),
                    )
                ),
            selectedProfileId = profileId,
            date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            meals =
                listOf(
                    HomeMealState.Linked(
                        id = activeMealId,
                        name = "Breakfast",
                        foods =
                            listOf(
                                HomeFoodState(
                                    id = FoodDiaryEntryId(Uuid.random()),
                                    time = LocalTime(hour = 7, minute = 45),
                                    snapshot =
                                        MeasuredFoodSnapshot(
                                            snapshot =
                                                AnonymousFoodSnapshot(
                                                    id = FoodSnapshotId.Anonymous(Uuid.random()),
                                                    name =
                                                        FoodName(
                                                            fallback = "Oatmeal with Blueberries"
                                                        ),
                                                    brand = null,
                                                    image = null,
                                                    nutritionFacts =
                                                        NutritionFacts(
                                                            proteins = 4.8.grams.toNutrientValue(),
                                                            carbohydrates =
                                                                19.2.grams.toNutrientValue(),
                                                            fats = 2.8.grams.toNutrientValue(),
                                                            energy =
                                                                116.kilocalories.toNutrientValue(),
                                                        ),
                                                ),
                                            quantity =
                                                FoodSnapshotQuantity.Serving(
                                                    servings = 1.0,
                                                    servingWeight = 250.grams,
                                                    packageWeight = null,
                                                ),
                                        ),
                                )
                            ),
                    ),
                    HomeMealState.Linked(
                        id = MealId(Uuid.random()),
                        name = "Lunch",
                        foods =
                            listOf(
                                HomeFoodState(
                                    id = FoodDiaryEntryId(Uuid.random()),
                                    time = LocalTime(hour = 12, minute = 30),
                                    snapshot =
                                        MeasuredFoodSnapshot(
                                            snapshot =
                                                AnonymousFoodSnapshot(
                                                    id = FoodSnapshotId.Anonymous(Uuid.random()),
                                                    name =
                                                        FoodName(
                                                            fallback = "Grilled Chicken Breast"
                                                        ),
                                                    brand = null,
                                                    image = null,
                                                    nutritionFacts =
                                                        NutritionFacts(
                                                            proteins = 23.3.grams.toNutrientValue(),
                                                            carbohydrates =
                                                                0.grams.toNutrientValue(),
                                                            fats = 2.8.grams.toNutrientValue(),
                                                            energy =
                                                                122.kilocalories.toNutrientValue(),
                                                        ),
                                                ),
                                            quantity =
                                                FoodSnapshotQuantity.Weight(
                                                    servingWeight = null,
                                                    packageWeight = null,
                                                    absoluteWeight = 180.grams,
                                                ),
                                        ),
                                )
                            ),
                    ),
                    HomeMealState.Unlinked(
                        listOf(
                            HomeFoodState(
                                id = FoodDiaryEntryId(Uuid.random()),
                                time = LocalTime(hour = 12, minute = 30),
                                snapshot =
                                    MeasuredFoodSnapshot(
                                        snapshot =
                                            AnonymousFoodSnapshot(
                                                id = FoodSnapshotId.Anonymous(Uuid.random()),
                                                name =
                                                    FoodName(fallback = "Grilled Chicken Breast"),
                                                brand = null,
                                                image = null,
                                                nutritionFacts =
                                                    NutritionFacts(
                                                        proteins = 23.3.grams.toNutrientValue(),
                                                        carbohydrates = 0.grams.toNutrientValue(),
                                                        fats = 2.8.grams.toNutrientValue(),
                                                        energy = 122.kilocalories.toNutrientValue(),
                                                    ),
                                            ),
                                        quantity =
                                            FoodSnapshotQuantity.Weight(
                                                servingWeight = null,
                                                packageWeight = null,
                                                absoluteWeight = 180.grams,
                                            ),
                                    ),
                            )
                        )
                    ),
                ),
            activeMealId = activeMealId,
        )
}
