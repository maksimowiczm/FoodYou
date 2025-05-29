package com.maksimowiczm.foodyou.feature.reciperedesign.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.domain.model.NutritionFacts
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.domain.model.Product
import com.maksimowiczm.foodyou.core.domain.model.Recipe
import com.maksimowiczm.foodyou.core.domain.model.RecipeIngredient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class CreateRecipeViewModel : ViewModel() {
    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())

    val ingredients = _ingredients.asStateFlow()

    init {
        _ingredients.value = listOf(
            Ingredient.Product(
                uniqueId = "1",
                food = testProduct(),
                measurement = Measurement.Gram(100f)
            ),
            Ingredient.Product(
                uniqueId = "2",
                food = testProduct(
                    name = "Another Product",
                    brand = "Brand B",
                    nutritionFacts = testNutritionFacts(
                        sodiumMilli = null
                    )
                ),
                measurement = Measurement.Serving(2f)
            ),
            Ingredient.Recipe(
                uniqueId = "3",
                food = Recipe(
                    id = FoodId.Recipe(1L),
                    name = "Test Recipe",
                    servings = 4,
                    ingredients = listOf(
                        RecipeIngredient(
                            food = testProduct(
                                name = "Another Product",
                                brand = "Brand B",
                                nutritionFacts = testNutritionFacts(
                                    sodiumMilli = null
                                )
                            ),
                            measurement = Measurement.Gram(150f)
                        )
                    )
                ),
                measurement = Measurement.Package(1f)
            )
        )
    }

    fun addIngredient(foodId: FoodId, measurement: Measurement) {
        // TODO
    }
}

fun testProduct(
    id: FoodId.Product = FoodId.Product(1L),
    name: String = "Test Product",
    brand: String? = "Test Brand",
    nutritionFacts: NutritionFacts = testNutritionFacts(),
    barcode: String? = "1234567890123",
    packageWeight: PortionWeight.Package? = PortionWeight.Package(500f),
    servingWeight: PortionWeight.Serving? = PortionWeight.Serving(20f)
): Product = Product(
    id = id,
    name = name,
    brand = brand,
    nutritionFacts = nutritionFacts,
    barcode = barcode,
    packageWeight = packageWeight,
    servingWeight = servingWeight
)

fun testNutritionFacts(
    proteins: Float = 10f,
    carbohydrates: Float = 15f,
    fats: Float = 5f,
    calories: Float = 145f,
    saturatedFats: Float? = 2f,
    monounsaturatedFats: Float? = 1f,
    polyunsaturatedFats: Float? = 1f,
    omega3: Float? = 0.5f,
    omega6: Float? = 0.5f,
    sugars: Float? = 5f,
    salt: Float? = 0.5f,
    fiber: Float? = 3f,
    cholesterolMilli: Float? = 0.1f,
    caffeineMilli: Float? = 0.1f,
    vitaminAMicro: Float? = 0.1f,
    vitaminB1Milli: Float? = 0.1f,
    vitaminB2Milli: Float? = 0.1f,
    vitaminB3Milli: Float? = 0.1f,
    vitaminB5Milli: Float? = 0.1f,
    vitaminB6Milli: Float? = 0.1f,
    vitaminB7Micro: Float? = 0.1f,
    vitaminB9Micro: Float? = 0.1f,
    vitaminB12Micro: Float? = 0.1f,
    vitaminCMilli: Float? = 0.1f,
    vitaminDMicro: Float? = 0.1f,
    vitaminEMilli: Float? = 0.1f,
    vitaminKMicro: Float? = 0.1f,
    manganeseMilli: Float? = 0.1f,
    magnesiumMilli: Float? = 0.1f,
    potassiumMilli: Float? = 0.1f,
    calciumMilli: Float? = 0.1f,
    copperMilli: Float? = 0.1f,
    zincMilli: Float? = 0.1f,
    sodiumMilli: Float? = 0.1f,
    ironMilli: Float? = 0.1f,
    phosphorusMilli: Float? = 0.1f,
    seleniumMicro: Float? = 0.1f,
    iodineMicro: Float? = 0.1f
) = NutritionFacts(
    proteins = proteins.toNutrientValue(),
    carbohydrates = carbohydrates.toNutrientValue(),
    fats = fats.toNutrientValue(),
    calories = calories.toNutrientValue(),
    saturatedFats = saturatedFats.toNutrientValue(),
    monounsaturatedFats = monounsaturatedFats.toNutrientValue(),
    polyunsaturatedFats = polyunsaturatedFats.toNutrientValue(),
    omega3 = omega3.toNutrientValue(),
    omega6 = omega6.toNutrientValue(),
    sugars = sugars.toNutrientValue(),
    salt = salt.toNutrientValue(),
    fiber = fiber.toNutrientValue(),
    cholesterolMilli = cholesterolMilli.toNutrientValue(),
    caffeineMilli = caffeineMilli.toNutrientValue(),
    vitaminAMicro = vitaminAMicro.toNutrientValue(),
    vitaminB1Milli = vitaminB1Milli.toNutrientValue(),
    vitaminB2Milli = vitaminB2Milli.toNutrientValue(),
    vitaminB3Milli = vitaminB3Milli.toNutrientValue(),
    vitaminB5Milli = vitaminB5Milli.toNutrientValue(),
    vitaminB6Milli = vitaminB6Milli.toNutrientValue(),
    vitaminB7Micro = vitaminB7Micro.toNutrientValue(),
    vitaminB9Micro = vitaminB9Micro.toNutrientValue(),
    vitaminB12Micro = vitaminB12Micro.toNutrientValue(),
    vitaminCMilli = vitaminCMilli.toNutrientValue(),
    vitaminDMicro = vitaminDMicro.toNutrientValue(),
    vitaminEMilli = vitaminEMilli.toNutrientValue(),
    vitaminKMicro = vitaminKMicro.toNutrientValue(),
    manganeseMilli = manganeseMilli.toNutrientValue(),
    magnesiumMilli = magnesiumMilli.toNutrientValue(),
    potassiumMilli = potassiumMilli.toNutrientValue(),
    calciumMilli = calciumMilli.toNutrientValue(),
    copperMilli = copperMilli.toNutrientValue(),
    zincMilli = zincMilli.toNutrientValue(),
    sodiumMilli = sodiumMilli.toNutrientValue(),
    ironMilli = ironMilli.toNutrientValue(),
    phosphorusMilli = phosphorusMilli.toNutrientValue(),
    seleniumMicro = seleniumMicro.toNutrientValue(),
    iodineMicro = iodineMicro.toNutrientValue()
)
