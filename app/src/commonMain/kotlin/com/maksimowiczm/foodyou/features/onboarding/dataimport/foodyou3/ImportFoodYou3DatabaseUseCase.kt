package com.maksimowiczm.foodyou.features.onboarding.dataimport.foodyou3

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.driver.bundled.SQLITE_OPEN_READONLY
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.AnonymousCompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.AnonymousLeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.toAbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.totalWeight
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.domain.ounces
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.application.QuickFoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.mealplan.domain.MealPlanCommand
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductBarcode
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCommand
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userproduct.domain.toSnapshot
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCommand
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import com.maksimowiczm.foodyou.userrecipe.domain.toSnapshot
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.copyTo
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.path
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class ImportFoodYou3DatabaseUseCase(
    private val mealPlanService: MealPlanService,
    private val foodDiaryService: FoodDiaryService,
    private val quickFoodDiaryService: QuickFoodDiaryService,
    private val userProductService: UserProductService,
    private val userRecipeService: UserRecipeService,
) {
    fun import(state: FoodYou3ImportState, profileId: ProfileId): Flow<Float> = flow {
        emit(0f)
        val path = requireNotNull(state.path) { "Database path is required" }

        val tempFile =
            FileKit.cacheDir / "foodyou3_import_${Clock.System.now().toEpochMilliseconds()}.db"

        try {
            PlatformFile(path).copyTo(tempFile)
            emit(0.05f)

            tempFile.verifyFoodYou3IdentityHash().expect("Given file is a Food You 3.4.9 database")
            emit(0.1f)

            BundledSQLiteDriver().open(tempFile.path, SQLITE_OPEN_READONLY).use {
                val f3Meals = if (state.mealPlan) importMealPlan(it) else emptyList()
                val products = mutableMapOf<Long, UserProduct>()
                emitAll(importProducts(it, state, products).scaled(0f..0.5f))
                emitAll(importRecipes(it, state, products).scaled(0.5f..0.6f))
                emitAll(importDiary(it, state, f3Meals, profileId).scaled(0.6f..1f))
            }
        } finally {
            tempFile.delete(mustExist = false)
        }
    }
        .flowOn(Dispatchers.IO)

    private suspend fun importMealPlan(conn: SQLiteConnection): List<Pair<Long, Meal>> {
        val f3Meals = conn.readMeals().toList()
        val meals = f3Meals.map { it.second }
        mealPlanService.handle(MealPlanCommand.ImportFoodYou3(meals = meals))
        return f3Meals
    }

    private fun importProducts(
        conn: SQLiteConnection,
        state: FoodYou3ImportState,
        imported: MutableMap<Long, UserProduct>,
    ): Flow<Float> = flow {
        val filter = state.toProductFilter()
        val progress = Progress(conn.count("SELECT COUNT(*) FROM Product ${filter.whereClause}"))

        for ((f3Id, product) in conn.readProducts(filter)) {
            userProductService.handle(
                id = product.id,
                command = UserProductCommand.ImportFoodYou3(product = product),
            )
            imported[f3Id] = product
            emit(progress.advance())
        }
        emit(1f)
    }

    private fun importRecipes(
        conn: SQLiteConnection,
        state: FoodYou3ImportState,
        products: Map<Long, UserProduct>,
    ): Flow<Float> {
        if (!state.yourRecipes) return flowOf(1f)

        return flow {
            val progress = Progress(conn.count("SELECT COUNT(*) FROM Recipe"))

            for (recipe in conn.readRecipes(products)) {
                userRecipeService.handle(recipe.recipe.id, recipe)
                emit(progress.advance())
            }
            emit(1f)
        }
    }

    private fun importDiary(
        conn: SQLiteConnection,
        state: FoodYou3ImportState,
        f3Meals: List<Pair<Long, Meal>>,
        profileId: ProfileId,
    ): Flow<Float> {
        if (!state.diaryEntries) return flowOf(1f)

        return flow {
            val mealIds = f3Meals.associate { (f3Id, meal) -> f3Id to meal.id }
            val progress =
                Progress(
                    conn.count("SELECT COUNT(*) FROM Measurement") +
                        conn.count("SELECT COUNT(*) FROM ManualDiaryEntry")
                )

            for (entry in conn.readMeasurements(mealIds, profileId)) {
                foodDiaryService.handle(entry.id, entry)
                emit(progress.advance())
            }
            for (entry in conn.readManualDiaryEntries(mealIds, profileId)) {
                quickFoodDiaryService.handle(entry.id, entry)
                emit(progress.advance())
            }
            emit(1f)
        }
    }
}

private fun Flow<Float>.scaled(range: ClosedFloatingPointRange<Float>): Flow<Float> = map {
    range.start + (range.endInclusive - range.start) * it
}

private class Progress(count: Int) {
    private val total = count.coerceAtLeast(1)
    private var done = 0

    fun advance(): Float = (++done / total.toFloat()).coerceAtMost(1f)
}

private object FoodSourceTypeSQLConstants {
    const val USER = 0
    const val OPEN_FOOD_FACTS = 1
    const val USDA = 2
}

private data class ProductFilter(
    val sourceTypes: Set<Int>,
    val skipUnusedRemote: Boolean,
    val includeRecipeIngredients: Boolean,
) {
    val isEmpty: Boolean
        get() = sourceTypes.isEmpty() && !includeRecipeIngredients

    val whereClause: String
        get() {
            val sources = sourceTypes.joinToString(",").ifEmpty { "-1" }
            val skipUnused =
                if (skipUnusedRemote)
                    "AND (sourceType = ${FoodSourceTypeSQLConstants.USER} OR EXISTS " +
                        "(SELECT 1 FROM MeasurementSuggestion ms WHERE ms.productId = Product.id))"
                else ""
            val recipeIngredients =
                if (includeRecipeIngredients)
                    "OR EXISTS (SELECT 1 FROM RecipeIngredient ri WHERE ri.ingredientProductId = Product.id)"
                else ""
            return "WHERE (sourceType IN ($sources) $skipUnused) $recipeIngredients"
        }
}

private fun FoodYou3ImportState.toProductFilter() =
    ProductFilter(
        sourceTypes =
            buildSet {
                if (yourProducts || yourRecipes) add(FoodSourceTypeSQLConstants.USER)
                if (openFoodFacts) add(FoodSourceTypeSQLConstants.OPEN_FOOD_FACTS)
                if (foodDataCentral) add(FoodSourceTypeSQLConstants.USDA)
            },
        skipUnusedRemote = skipUnusedRemote,
        includeRecipeIngredients = yourRecipes,
    )

private const val NUTRIENT_COLUMNS =
    "energy, proteins, fats, saturatedFats, transFats, monounsaturatedFats, polyunsaturatedFats, " +
        "omega3, omega6, carbohydrates, sugars, addedSugars, dietaryFiber, solubleFiber, insolubleFiber, " +
        "salt, cholesterolMilli, caffeineMilli, vitaminAMicro, vitaminB1Milli, vitaminB2Milli, vitaminB3Milli, " +
        "vitaminB5Milli, vitaminB6Milli, vitaminB7Micro, vitaminB9Micro, vitaminB12Micro, vitaminCMilli, " +
        "vitaminDMicro, vitaminEMilli, vitaminKMicro, manganeseMilli, magnesiumMilli, potassiumMilli, " +
        "calciumMilli, copperMilli, zincMilli, sodiumMilli, ironMilli, phosphorusMilli, seleniumMicro, " +
        "iodineMicro, chromiumMicro"

private fun SQLiteConnection.readMeals(): Sequence<Pair<Long, Meal>> =
    query("SELECT id, name, fromHour, fromMinute, toHour, toMinute FROM Meal ORDER BY rank ASC") {
        val id = long("id")
        val name = text("name")
        check(name.isNotBlank()) { "Meal name cannot be blank, id: $id" }

        val from = LocalTime(int("fromHour"), int("fromMinute"))
        val to = LocalTime(int("toHour"), int("toMinute"))
        val timeWindow = if (from == to) Meal.TimeWindow.AllDay else Meal.TimeWindow.Range(from, to)

        id to Meal(name = name, timeWindow = timeWindow)
    }

private fun SQLiteConnection.readProducts(
    filter: ProductFilter
): Sequence<Pair<Long, UserProduct>> {
    if (filter.isEmpty) return emptySequence()

    return query(
        """
        SELECT id, name, brand, barcode, packageWeight, servingWeight, note, sourceType, sourceUrl,
               isLiquid, $NUTRIENT_COLUMNS
        FROM Product
        ${filter.whereClause}
        """
            .trimIndent()
    ) {
        val isLiquid = boolean("isLiquid")
        long("id") to
            UserProduct(
                id = UserProductId(),
                name = FoodName(fallback = text("name")),
                brand = nonBlankText("brand"),
                barcode =
                    nonBlankText("barcode")
                        ?.takeIf { it.all(Char::isDigit) }
                        ?.let(::UserProductBarcode),
                note = nonBlankText("note"),
                image = null,
                nutritionFacts = nutritionFacts(),
                servingQuantity = weightOrVolume(doubleOrNull("servingWeight"), isLiquid),
                packageQuantity = weightOrVolume(doubleOrNull("packageWeight"), isLiquid),
                isLiquid = isLiquid,
            )
    }
}

private data class F3DiaryProductInfo(
    val snapshot: AnonymousLeafFoodSnapshot,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
)

private data class F3DiaryRecipeInfo(
    val snapshot: AnonymousCompositeFoodSnapshot,
    val servingQuantity: AbsoluteQuantity,
)

private fun SQLiteConnection.readDiaryProducts(): Map<Long, F3DiaryProductInfo> =
    query(
            """
            SELECT id, name, packageWeight, servingWeight, isLiquid, $NUTRIENT_COLUMNS
            FROM DiaryProduct
            """
                .trimIndent()
        ) {
            val isLiquid = boolean("isLiquid")
            long("id") to
                F3DiaryProductInfo(
                    snapshot =
                        AnonymousLeafFoodSnapshot(
                            id = FoodSnapshotId.Anonymous(),
                            name = FoodName(fallback = text("name")),
                            brand = null,
                            image = null,
                            nutritionFacts = nutritionFacts(),
                        ),
                    servingQuantity = weightOrVolume(doubleOrNull("servingWeight"), isLiquid),
                    packageQuantity = weightOrVolume(doubleOrNull("packageWeight"), isLiquid),
                )
        }
        .toMap()

private fun SQLiteConnection.readDiaryRecipes(
    diaryProducts: Map<Long, F3DiaryProductInfo>
): Map<Long, F3DiaryRecipeInfo> =
    RecipeGraph(
            recipes = readRawRecipes("DiaryRecipe").toList(),
            ingredients = readRawRecipeIngredients("DiaryRecipeIngredient").toList(),
        )
        .resolve(
            productComponent = { ingredient, productId ->
                diaryProducts[productId]?.let {
                    ingredient.measure(it.snapshot, it.servingQuantity, it.packageQuantity)
                }
            },
            recipeComponent = { ingredient, subRecipe ->
                ingredient.measure(subRecipe.snapshot, subRecipe.servingQuantity, null)
            },
            build = { raw, components ->
                val servings = if (raw.servings > 0) raw.servings else 1.0
                F3DiaryRecipeInfo(
                    snapshot =
                        AnonymousCompositeFoodSnapshot(
                            id = FoodSnapshotId.Anonymous(),
                            name = FoodName(fallback = raw.name),
                            brand = null,
                            image = null,
                            components = components,
                        ),
                    servingQuantity =
                        weightOrVolume(components.totalWeight / servings, raw.isLiquid),
                )
            },
        )

private fun SQLiteConnection.readMeasurements(
    mealIds: Map<Long, MealId>,
    profileId: ProfileId,
): Sequence<FoodDiaryEntryCommand.ImportFoodYou3> {
    val products = readDiaryProducts()
    val recipes = readDiaryRecipes(products)

    return query(
        "SELECT id, mealId, epochDay, productId, recipeId, measurement, quantity FROM Measurement"
    ) {
        val id = long("id")
        val type = int("measurement")
        val amount = double("quantity")
        val productId = longOrNull("productId")
        val recipeId = longOrNull("recipeId")

        val measured =
            when {
                productId != null ->
                    products[productId]?.let {
                        MeasuredFoodSnapshot(
                            snapshot = it.snapshot,
                            quantity =
                                buildFoodSnapshotQuantity(
                                    type,
                                    amount,
                                    it.servingQuantity,
                                    it.packageQuantity,
                                ),
                        )
                    }
                recipeId != null ->
                    recipes[recipeId]?.let {
                        MeasuredFoodSnapshot(
                            snapshot = it.snapshot,
                            quantity =
                                buildFoodSnapshotQuantity(
                                    type,
                                    amount,
                                    it.servingQuantity,
                                    null,
                                ),
                        )
                    }

                else -> error("Measurement without product or recipe: $id")
            }

        FoodDiaryEntryCommand.ImportFoodYou3(
            id = FoodDiaryEntryId(),
            profileId = profileId,
            snapshot = checkNotNull(measured) { "Measurement without product or recipe: $id" },
            mealId = mealIds[long("mealId")],
            entryDate = LocalDate.fromEpochDays(long("epochDay")),
        )
    }
}

private fun SQLiteConnection.readManualDiaryEntries(
    mealIds: Map<Long, MealId>,
    profileId: ProfileId,
): Sequence<QuickFoodDiaryEntryCommand.ImportFoodYou3> =
    query("SELECT mealId, dateEpochDay, name, $NUTRIENT_COLUMNS FROM ManualDiaryEntry") {
        QuickFoodDiaryEntryCommand.ImportFoodYou3(
            id = QuickFoodDiaryEntryId(),
            profileId = profileId,
            name = text("name"),
            nutritionFacts = nutritionFacts(),
            mealId = mealIds[long("mealId")],
            entryDate = LocalDate.fromEpochDays(long("dateEpochDay")),
        )
    }

private data class F3RawRecipe(
    val id: Long,
    val name: String,
    val servings: Double,
    val note: String?,
    val isLiquid: Boolean,
)

private data class F3RawRecipeIngredient(
    val recipeId: Long,
    val ingredientProductId: Long?,
    val ingredientRecipeId: Long?,
    val measurement: Int,
    val quantity: Double,
)

private fun F3RawRecipeIngredient.measure(
    snapshot: FoodSnapshot,
    servingQuantity: AbsoluteQuantity?,
    packageQuantity: AbsoluteQuantity?,
) =
    MeasuredFoodSnapshot(
        snapshot = snapshot,
        quantity =
            buildFoodSnapshotQuantity(measurement, quantity, servingQuantity, packageQuantity),
    )

private fun SQLiteConnection.readRawRecipes(tableName: String = "Recipe"): Sequence<F3RawRecipe> =
    query("SELECT id, name, servings, note, isLiquid FROM $tableName") {
        F3RawRecipe(
            id = long("id"),
            name = text("name"),
            servings = double("servings"),
            note = nonBlankText("note"),
            isLiquid = boolean("isLiquid"),
        )
    }

private fun SQLiteConnection.readRawRecipeIngredients(
    tableName: String = "RecipeIngredient"
): Sequence<F3RawRecipeIngredient> =
    query(
        "SELECT recipeId, ingredientProductId, ingredientRecipeId, measurement, quantity FROM $tableName"
    ) {
        F3RawRecipeIngredient(
            recipeId = long("recipeId"),
            ingredientProductId = longOrNull("ingredientProductId"),
            ingredientRecipeId = longOrNull("ingredientRecipeId"),
            measurement = int("measurement"),
            quantity = double("quantity"),
        )
    }

private fun SQLiteConnection.readRecipes(
    importedProducts: Map<Long, UserProduct>
): Sequence<UserRecipeCommand.Create> =
    RecipeGraph(
            recipes = readRawRecipes().toList(),
            ingredients = readRawRecipeIngredients().toList(),
        )
        .resolve(
            productComponent = { ingredient, productId ->
                importedProducts[productId]?.let {
                    ingredient.measure(it.toSnapshot(), it.servingQuantity, it.packageQuantity)
                }
            },
            recipeComponent = { ingredient, subRecipe: UserRecipe ->
                ingredient.measure(
                    snapshot = subRecipe.toSnapshot(),
                    servingQuantity = subRecipe.servingWeight.toAbsoluteQuantity(),
                    packageQuantity = null,
                )
            },
            build = { raw, components ->
                UserRecipe(
                    id = UserRecipeId(),
                    name = FoodName(fallback = raw.name),
                    note = raw.note,
                    image = null,
                    servings = raw.servings,
                    components = components,
                )
            },
        )
        .values
        .asSequence()
        .map { UserRecipeCommand.Create(it) }

/**
 * Resolves nested recipes (recipes that use other recipes as ingredients) exactly once each,
 * dependencies first, skipping cycles, missing ingredients and recipes without any component.
 */
private class RecipeGraph(recipes: List<F3RawRecipe>, ingredients: List<F3RawRecipeIngredient>) {
    private val recipesById = recipes.associateBy { it.id }
    private val ingredientsByRecipe = ingredients.groupBy { it.recipeId }

    fun <R : Any> resolve(
        productComponent: (F3RawRecipeIngredient, productId: Long) -> MeasuredFoodSnapshot?,
        recipeComponent: (F3RawRecipeIngredient, subRecipe: R) -> MeasuredFoodSnapshot,
        build: (F3RawRecipe, List<MeasuredFoodSnapshot>) -> R,
    ): Map<Long, R> {
        val resolved = LinkedHashMap<Long, R>()
        val visiting = mutableSetOf<Long>()

        fun resolveRecipe(id: Long): R? {
            resolved[id]?.let {
                return it
            }
            val raw = checkNotNull(recipesById[id]) { "Recipe $id not found" }
            check(visiting.add(id)) { "Cycle in recipe $id" }

            val components =
                ingredientsByRecipe[id].orEmpty().mapNotNull { ingredient ->
                    when {
                        ingredient.ingredientProductId != null ->
                            productComponent(ingredient, ingredient.ingredientProductId)
                        ingredient.ingredientRecipeId != null ->
                            resolveRecipe(ingredient.ingredientRecipeId)?.let {
                                recipeComponent(ingredient, it)
                            }
                        else -> null
                    }
                }
            visiting.remove(id)

            if (components.isEmpty()) return null
            return build(raw, components).also { resolved[id] = it }
        }

        for (id in recipesById.keys) resolveRecipe(id)
        return resolved
    }
}

private fun weightOrVolume(value: Double, isLiquid: Boolean): AbsoluteQuantity =
    if (isLiquid) value.milliliters.toAbsoluteQuantity() else value.grams.toAbsoluteQuantity()

private fun weightOrVolume(value: Double?, isLiquid: Boolean): AbsoluteQuantity? = value?.let {
    weightOrVolume(it, isLiquid)
}

private fun weightOrVolume(weight: Weight, isLiquid: Boolean): AbsoluteQuantity =
    if (isLiquid) weight.grams.milliliters.toAbsoluteQuantity() else weight.toAbsoluteQuantity()

private object F3Measurement {
    const val GRAMS = 0
    const val PACKAGE = 1
    const val SERVING = 2
    const val MILLILITERS = 3
    const val OUNCES = 4
    const val FLUID_OUNCES = 5
}

private fun buildFoodSnapshotQuantity(
    measurementType: Int,
    quantityVal: Double,
    servingQuantity: AbsoluteQuantity?,
    packageQuantity: AbsoluteQuantity?,
): FoodSnapshotQuantity =
    when (measurementType) {
        F3Measurement.GRAMS ->
            FoodSnapshotQuantity.Weight(quantityVal.grams, servingQuantity, packageQuantity)
        F3Measurement.PACKAGE if packageQuantity != null ->
            FoodSnapshotQuantity.Package(quantityVal, servingQuantity, packageQuantity)
        F3Measurement.SERVING if servingQuantity != null ->
            FoodSnapshotQuantity.Serving(quantityVal, servingQuantity, packageQuantity)
        F3Measurement.MILLILITERS ->
            FoodSnapshotQuantity.Volume(quantityVal.milliliters, servingQuantity, packageQuantity)
        F3Measurement.FLUID_OUNCES ->
            FoodSnapshotQuantity.Volume(quantityVal.fluidOunces, servingQuantity, packageQuantity)
        F3Measurement.OUNCES ->
            FoodSnapshotQuantity.Weight(quantityVal.ounces, servingQuantity, packageQuantity)
        else -> error("Unknown measurement type: $measurementType")
    }
