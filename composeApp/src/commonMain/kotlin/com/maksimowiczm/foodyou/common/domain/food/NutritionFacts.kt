package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import kotlinx.serialization.Serializable

/**
 * Comprehensive nutritional information for a food product.
 *
 * Contains macronutrients, vitamins, minerals, and other nutritional components. All values are
 * represented as NutrientValue, which tracks data completeness. Supports arithmetic operations for
 * calculating combined or scaled nutrition facts.
 *
 * By default, all nutrient values are incomplete (null), indicating missing data.
 */
@Serializable
data class NutritionFacts(
    val proteins: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val carbohydrates: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val energy: NutrientValue<Energy> = NutrientValue.Incomplete(null),
    val fats: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val saturatedFats: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val transFats: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val monounsaturatedFats: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val polyunsaturatedFats: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val omega3: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val omega6: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val sugars: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val addedSugars: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val dietaryFiber: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val solubleFiber: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val insolubleFiber: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val salt: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val cholesterol: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val caffeine: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminA: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminB1: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminB2: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminB3: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminB5: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminB6: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminB7: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminB9: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminB12: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminC: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminD: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminE: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val vitaminK: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val manganese: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val magnesium: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val potassium: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val calcium: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val copper: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val zinc: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val sodium: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val iron: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val phosphorus: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val selenium: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val iodine: NutrientValue<Weight> = NutrientValue.Incomplete(null),
    val chromium: NutrientValue<Weight> = NutrientValue.Incomplete(null),
) {
    /**
     * Scales all nutrient values by the given multiplier.
     *
     * Useful for calculating nutrition for different serving sizes.
     */
    operator fun times(multiplier: Double): NutritionFacts =
        NutritionFacts(
            proteins = proteins * multiplier,
            carbohydrates = carbohydrates * multiplier,
            energy = energy * multiplier,
            fats = fats * multiplier,
            saturatedFats = saturatedFats * multiplier,
            transFats = transFats * multiplier,
            monounsaturatedFats = monounsaturatedFats * multiplier,
            polyunsaturatedFats = polyunsaturatedFats * multiplier,
            omega3 = omega3 * multiplier,
            omega6 = omega6 * multiplier,
            sugars = sugars * multiplier,
            addedSugars = addedSugars * multiplier,
            dietaryFiber = dietaryFiber * multiplier,
            solubleFiber = solubleFiber * multiplier,
            insolubleFiber = insolubleFiber * multiplier,
            salt = salt * multiplier,
            cholesterol = cholesterol * multiplier,
            caffeine = caffeine * multiplier,
            vitaminA = vitaminA * multiplier,
            vitaminB1 = vitaminB1 * multiplier,
            vitaminB2 = vitaminB2 * multiplier,
            vitaminB3 = vitaminB3 * multiplier,
            vitaminB5 = vitaminB5 * multiplier,
            vitaminB6 = vitaminB6 * multiplier,
            vitaminB7 = vitaminB7 * multiplier,
            vitaminB9 = vitaminB9 * multiplier,
            vitaminB12 = vitaminB12 * multiplier,
            vitaminC = vitaminC * multiplier,
            vitaminD = vitaminD * multiplier,
            vitaminE = vitaminE * multiplier,
            vitaminK = vitaminK * multiplier,
            manganese = manganese * multiplier,
            magnesium = magnesium * multiplier,
            potassium = potassium * multiplier,
            calcium = calcium * multiplier,
            copper = copper * multiplier,
            zinc = zinc * multiplier,
            sodium = sodium * multiplier,
            iron = iron * multiplier,
            phosphorus = phosphorus * multiplier,
            selenium = selenium * multiplier,
            iodine = iodine * multiplier,
            chromium = chromium * multiplier,
        )

    /**
     * Scales all nutrient values by dividing by the given divisor.
     *
     * Useful for normalizing nutrition to standard serving sizes.
     */
    operator fun div(divisor: Double): NutritionFacts =
        NutritionFacts(
            proteins = proteins / divisor,
            carbohydrates = carbohydrates / divisor,
            energy = energy / divisor,
            fats = fats / divisor,
            saturatedFats = saturatedFats / divisor,
            transFats = transFats / divisor,
            monounsaturatedFats = monounsaturatedFats / divisor,
            polyunsaturatedFats = polyunsaturatedFats / divisor,
            omega3 = omega3 / divisor,
            omega6 = omega6 / divisor,
            sugars = sugars / divisor,
            addedSugars = addedSugars / divisor,
            dietaryFiber = dietaryFiber / divisor,
            solubleFiber = solubleFiber / divisor,
            insolubleFiber = insolubleFiber / divisor,
            salt = salt / divisor,
            cholesterol = cholesterol / divisor,
            caffeine = caffeine / divisor,
            vitaminA = vitaminA / divisor,
            vitaminB1 = vitaminB1 / divisor,
            vitaminB2 = vitaminB2 / divisor,
            vitaminB3 = vitaminB3 / divisor,
            vitaminB5 = vitaminB5 / divisor,
            vitaminB6 = vitaminB6 / divisor,
            vitaminB7 = vitaminB7 / divisor,
            vitaminB9 = vitaminB9 / divisor,
            vitaminB12 = vitaminB12 / divisor,
            vitaminC = vitaminC / divisor,
            vitaminD = vitaminD / divisor,
            vitaminE = vitaminE / divisor,
            vitaminK = vitaminK / divisor,
            manganese = manganese / divisor,
            magnesium = magnesium / divisor,
            potassium = potassium / divisor,
            calcium = calcium / divisor,
            copper = copper / divisor,
            zinc = zinc / divisor,
            sodium = sodium / divisor,
            iron = iron / divisor,
            phosphorus = phosphorus / divisor,
            selenium = selenium / divisor,
            iodine = iodine / divisor,
            chromium = chromium / divisor,
        )

    /**
     * Combines two NutritionFacts by summing corresponding nutrient values.
     *
     * Useful for calculating total nutrition from multiple food items. Completeness is preserved
     * according to NutrientValue addition rules.
     */
    operator fun plus(other: NutritionFacts): NutritionFacts =
        NutritionFacts(
            proteins = proteins + other.proteins,
            carbohydrates = carbohydrates + other.carbohydrates,
            energy = energy + other.energy,
            fats = fats + other.fats,
            saturatedFats = saturatedFats + other.saturatedFats,
            transFats = transFats + other.transFats,
            monounsaturatedFats = monounsaturatedFats + other.monounsaturatedFats,
            polyunsaturatedFats = polyunsaturatedFats + other.polyunsaturatedFats,
            omega3 = omega3 + other.omega3,
            omega6 = omega6 + other.omega6,
            sugars = sugars + other.sugars,
            addedSugars = addedSugars + other.addedSugars,
            dietaryFiber = dietaryFiber + other.dietaryFiber,
            solubleFiber = solubleFiber + other.solubleFiber,
            insolubleFiber = insolubleFiber + other.insolubleFiber,
            salt = salt + other.salt,
            cholesterol = cholesterol + other.cholesterol,
            caffeine = caffeine + other.caffeine,
            vitaminA = vitaminA + other.vitaminA,
            vitaminB1 = vitaminB1 + other.vitaminB1,
            vitaminB2 = vitaminB2 + other.vitaminB2,
            vitaminB3 = vitaminB3 + other.vitaminB3,
            vitaminB5 = vitaminB5 + other.vitaminB5,
            vitaminB6 = vitaminB6 + other.vitaminB6,
            vitaminB7 = vitaminB7 + other.vitaminB7,
            vitaminB9 = vitaminB9 + other.vitaminB9,
            vitaminB12 = vitaminB12 + other.vitaminB12,
            vitaminC = vitaminC + other.vitaminC,
            vitaminD = vitaminD + other.vitaminD,
            vitaminE = vitaminE + other.vitaminE,
            vitaminK = vitaminK + other.vitaminK,
            manganese = manganese + other.manganese,
            magnesium = magnesium + other.magnesium,
            potassium = potassium + other.potassium,
            calcium = calcium + other.calcium,
            copper = copper + other.copper,
            zinc = zinc + other.zinc,
            sodium = sodium + other.sodium,
            iron = iron + other.iron,
            phosphorus = phosphorus + other.phosphorus,
            selenium = selenium + other.selenium,
            iodine = iodine + other.iodine,
            chromium = chromium + other.chromium,
        )

    operator fun get(nutrient: Nutrient): NutrientValue<Weight> =
        when (nutrient) {
            Nutrient.Proteins -> proteins
            Nutrient.Carbohydrates -> carbohydrates
            Nutrient.Fats -> fats
            Nutrient.SaturatedFats -> saturatedFats
            Nutrient.TransFats -> transFats
            Nutrient.MonounsaturatedFats -> monounsaturatedFats
            Nutrient.PolyunsaturatedFats -> polyunsaturatedFats
            Nutrient.Omega3 -> omega3
            Nutrient.Omega6 -> omega6
            Nutrient.Sugars -> sugars
            Nutrient.AddedSugars -> addedSugars
            Nutrient.DietaryFiber -> dietaryFiber
            Nutrient.SolubleFiber -> solubleFiber
            Nutrient.InsolubleFiber -> insolubleFiber
            Nutrient.Salt -> salt
            Nutrient.Cholesterol -> cholesterol
            Nutrient.Caffeine -> caffeine
            Nutrient.VitaminA -> vitaminA
            Nutrient.VitaminB1 -> vitaminB1
            Nutrient.VitaminB2 -> vitaminB2
            Nutrient.VitaminB3 -> vitaminB3
            Nutrient.VitaminB5 -> vitaminB5
            Nutrient.VitaminB6 -> vitaminB6
            Nutrient.VitaminB7 -> vitaminB7
            Nutrient.VitaminB9 -> vitaminB9
            Nutrient.VitaminB12 -> vitaminB12
            Nutrient.VitaminC -> vitaminC
            Nutrient.VitaminD -> vitaminD
            Nutrient.VitaminE -> vitaminE
            Nutrient.VitaminK -> vitaminK
            Nutrient.Manganese -> manganese
            Nutrient.Magnesium -> magnesium
            Nutrient.Potassium -> potassium
            Nutrient.Calcium -> calcium
            Nutrient.Copper -> copper
            Nutrient.Zinc -> zinc
            Nutrient.Sodium -> sodium
            Nutrient.Iron -> iron
            Nutrient.Phosphorus -> phosphorus
            Nutrient.Selenium -> selenium
            Nutrient.Iodine -> iodine
            Nutrient.Chromium -> chromium
        }

    fun asMap(): Map<Nutrient, NutrientValue<Weight>> = Nutrient.entries.associateWith { this[it] }

    /** Checks if any nutrient value (including energy) is incomplete. */
    fun isIncomplete(): Boolean = asMap().values.any { it.isIncomplete() } || energy.isIncomplete()

    fun hasMacronutrientsValues(): Boolean =
        proteins.value != null && carbohydrates.value != null && fats.value != null

    fun hasProteins(): Boolean = proteins.value != null

    fun hasAnyCarbohydratesComponent(): Boolean =
        carbohydrates.value != null ||
            sugars.value != null ||
            addedSugars.value != null ||
            dietaryFiber.value != null ||
            solubleFiber.value != null ||
            insolubleFiber.value != null

    fun hasAnyFatsComponent(): Boolean =
        fats.value != null ||
            saturatedFats.value != null ||
            transFats.value != null ||
            monounsaturatedFats.value != null ||
            polyunsaturatedFats.value != null ||
            omega3.value != null ||
            omega6.value != null

    fun hasAnyOtherComponent(): Boolean =
        salt.value != null || cholesterol.value != null || caffeine.value != null

    fun hasAnyVitamins(): Boolean =
        vitaminA.value != null ||
            vitaminB1.value != null ||
            vitaminB2.value != null ||
            vitaminB3.value != null ||
            vitaminB5.value != null ||
            vitaminB6.value != null ||
            vitaminB7.value != null ||
            vitaminB9.value != null ||
            vitaminB12.value != null ||
            vitaminC.value != null ||
            vitaminD.value != null ||
            vitaminE.value != null ||
            vitaminK.value != null

    fun hasAnyMinerals(): Boolean =
        manganese.value != null ||
            magnesium.value != null ||
            potassium.value != null ||
            calcium.value != null ||
            copper.value != null ||
            zinc.value != null ||
            sodium.value != null ||
            iron.value != null ||
            phosphorus.value != null ||
            selenium.value != null ||
            iodine.value != null ||
            chromium.value != null

    companion object {
        /**
         * Creates a NutritionFacts instance requiring all nutrient values to be explicitly
         * provided.
         *
         * Prevents accidental omission of nutrient values during construction.
         */
        fun requireAll(
            proteins: NutrientValue<Weight>,
            carbohydrates: NutrientValue<Weight>,
            energy: NutrientValue<Energy>,
            fats: NutrientValue<Weight>,
            saturatedFats: NutrientValue<Weight>,
            transFats: NutrientValue<Weight>,
            monounsaturatedFats: NutrientValue<Weight>,
            polyunsaturatedFats: NutrientValue<Weight>,
            omega3: NutrientValue<Weight>,
            omega6: NutrientValue<Weight>,
            sugars: NutrientValue<Weight>,
            addedSugars: NutrientValue<Weight>,
            dietaryFiber: NutrientValue<Weight>,
            solubleFiber: NutrientValue<Weight>,
            insolubleFiber: NutrientValue<Weight>,
            salt: NutrientValue<Weight>,
            cholesterol: NutrientValue<Weight>,
            caffeine: NutrientValue<Weight>,
            vitaminA: NutrientValue<Weight>,
            vitaminB1: NutrientValue<Weight>,
            vitaminB2: NutrientValue<Weight>,
            vitaminB3: NutrientValue<Weight>,
            vitaminB5: NutrientValue<Weight>,
            vitaminB6: NutrientValue<Weight>,
            vitaminB7: NutrientValue<Weight>,
            vitaminB9: NutrientValue<Weight>,
            vitaminB12: NutrientValue<Weight>,
            vitaminC: NutrientValue<Weight>,
            vitaminD: NutrientValue<Weight>,
            vitaminE: NutrientValue<Weight>,
            vitaminK: NutrientValue<Weight>,
            manganese: NutrientValue<Weight>,
            magnesium: NutrientValue<Weight>,
            potassium: NutrientValue<Weight>,
            calcium: NutrientValue<Weight>,
            copper: NutrientValue<Weight>,
            zinc: NutrientValue<Weight>,
            sodium: NutrientValue<Weight>,
            iron: NutrientValue<Weight>,
            phosphorus: NutrientValue<Weight>,
            selenium: NutrientValue<Weight>,
            iodine: NutrientValue<Weight>,
            chromium: NutrientValue<Weight>,
        ): NutritionFacts =
            NutritionFacts(
                proteins = proteins,
                carbohydrates = carbohydrates,
                energy = energy,
                fats = fats,
                saturatedFats = saturatedFats,
                transFats = transFats,
                monounsaturatedFats = monounsaturatedFats,
                polyunsaturatedFats = polyunsaturatedFats,
                omega3 = omega3,
                omega6 = omega6,
                sugars = sugars,
                addedSugars = addedSugars,
                dietaryFiber = dietaryFiber,
                solubleFiber = solubleFiber,
                insolubleFiber = insolubleFiber,
                salt = salt,
                cholesterol = cholesterol,
                caffeine = caffeine,
                vitaminA = vitaminA,
                vitaminB1 = vitaminB1,
                vitaminB2 = vitaminB2,
                vitaminB3 = vitaminB3,
                vitaminB5 = vitaminB5,
                vitaminB6 = vitaminB6,
                vitaminB7 = vitaminB7,
                vitaminB9 = vitaminB9,
                vitaminB12 = vitaminB12,
                vitaminC = vitaminC,
                vitaminD = vitaminD,
                vitaminE = vitaminE,
                vitaminK = vitaminK,
                manganese = manganese,
                magnesium = magnesium,
                potassium = potassium,
                calcium = calcium,
                copper = copper,
                zinc = zinc,
                sodium = sodium,
                iron = iron,
                phosphorus = phosphorus,
                selenium = selenium,
                iodine = iodine,
                chromium = chromium,
            )

        val zeroCompleted: NutritionFacts =
            NutritionFacts(
                proteins = NutrientValue.Complete(0.grams),
                carbohydrates = NutrientValue.Complete(0.grams),
                energy = NutrientValue.Complete(0.kilocalories),
                fats = NutrientValue.Complete(0.grams),
                saturatedFats = NutrientValue.Complete(0.grams),
                transFats = NutrientValue.Complete(0.grams),
                monounsaturatedFats = NutrientValue.Complete(0.grams),
                polyunsaturatedFats = NutrientValue.Complete(0.grams),
                omega3 = NutrientValue.Complete(0.grams),
                omega6 = NutrientValue.Complete(0.grams),
                sugars = NutrientValue.Complete(0.grams),
                addedSugars = NutrientValue.Complete(0.grams),
                dietaryFiber = NutrientValue.Complete(0.grams),
                solubleFiber = NutrientValue.Complete(0.grams),
                insolubleFiber = NutrientValue.Complete(0.grams),
                salt = NutrientValue.Complete(0.grams),
                cholesterol = NutrientValue.Complete(0.grams),
                caffeine = NutrientValue.Complete(0.grams),
                vitaminA = NutrientValue.Complete(0.grams),
                vitaminB1 = NutrientValue.Complete(0.grams),
                vitaminB2 = NutrientValue.Complete(0.grams),
                vitaminB3 = NutrientValue.Complete(0.grams),
                vitaminB5 = NutrientValue.Complete(0.grams),
                vitaminB6 = NutrientValue.Complete(0.grams),
                vitaminB7 = NutrientValue.Complete(0.grams),
                vitaminB9 = NutrientValue.Complete(0.grams),
                vitaminB12 = NutrientValue.Complete(0.grams),
                vitaminC = NutrientValue.Complete(0.grams),
                vitaminD = NutrientValue.Complete(0.grams),
                vitaminE = NutrientValue.Complete(0.grams),
                vitaminK = NutrientValue.Complete(0.grams),
                manganese = NutrientValue.Complete(0.grams),
                magnesium = NutrientValue.Complete(0.grams),
                potassium = NutrientValue.Complete(0.grams),
                calcium = NutrientValue.Complete(0.grams),
                copper = NutrientValue.Complete(0.grams),
                zinc = NutrientValue.Complete(0.grams),
                sodium = NutrientValue.Complete(0.grams),
                iron = NutrientValue.Complete(0.grams),
                phosphorus = NutrientValue.Complete(0.grams),
                selenium = NutrientValue.Complete(0.grams),
                iodine = NutrientValue.Complete(0.grams),
                chromium = NutrientValue.Complete(0.grams),
            )
    }
}

/**
 * Sums a collection of NutritionFacts into a single combined instance.
 *
 * Useful for calculating total nutrition from multiple food items in a meal.
 */
fun Iterable<NutritionFacts>.sum(): NutritionFacts =
    fold(NutritionFacts.zeroCompleted) { acc, nutrients -> acc + nutrients }
