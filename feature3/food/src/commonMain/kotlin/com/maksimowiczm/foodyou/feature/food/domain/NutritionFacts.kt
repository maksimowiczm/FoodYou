package com.maksimowiczm.foodyou.feature.food.domain

data class NutritionFacts(
    // Macronutrients
    val proteins: NutrientValue.Complete,
    val carbohydrates: NutrientValue.Complete,
    val energy: NutrientValue.Complete,
    // Fats
    val fats: NutrientValue.Complete,
    val saturatedFats: NutrientValue,
    val transFats: NutrientValue,
    val monounsaturatedFats: NutrientValue,
    val polyunsaturatedFats: NutrientValue,
    val omega3: NutrientValue,
    val omega6: NutrientValue,
    // Other
    val sugars: NutrientValue,
    val addedSugars: NutrientValue,
    val dietaryFiber: NutrientValue,
    val solubleFiber: NutrientValue,
    val insolubleFiber: NutrientValue,
    val salt: NutrientValue,
    val cholesterolMilli: NutrientValue,
    val caffeineMilli: NutrientValue,
    // Vitamins
    val vitaminAMicro: NutrientValue,
    val vitaminB1Milli: NutrientValue,
    val vitaminB2Milli: NutrientValue,
    val vitaminB3Milli: NutrientValue,
    val vitaminB5Milli: NutrientValue,
    val vitaminB6Milli: NutrientValue,
    val vitaminB7Micro: NutrientValue,
    val vitaminB9Micro: NutrientValue,
    val vitaminB12Micro: NutrientValue,
    val vitaminCMilli: NutrientValue,
    val vitaminDMicro: NutrientValue,
    val vitaminEMilli: NutrientValue,
    val vitaminKMicro: NutrientValue,
    // Minerals
    val manganeseMilli: NutrientValue,
    val magnesiumMilli: NutrientValue,
    val potassiumMilli: NutrientValue,
    val calciumMilli: NutrientValue,
    val copperMilli: NutrientValue,
    val zincMilli: NutrientValue,
    val sodiumMilli: NutrientValue,
    val ironMilli: NutrientValue,
    val phosphorusMilli: NutrientValue,
    val seleniumMicro: NutrientValue,
    val iodineMicro: NutrientValue,
    val chromiumMicro: NutrientValue
) {
    private val map = NutritionFactsField.entries.associateWith {
        when (it) {
            NutritionFactsField.Proteins -> proteins
            NutritionFactsField.Carbohydrates -> carbohydrates
            NutritionFactsField.Fats -> fats
            NutritionFactsField.Energy -> energy
            NutritionFactsField.SaturatedFats -> saturatedFats
            NutritionFactsField.MonounsaturatedFats -> monounsaturatedFats
            NutritionFactsField.PolyunsaturatedFats -> polyunsaturatedFats
            NutritionFactsField.Omega3 -> omega3
            NutritionFactsField.Omega6 -> omega6
            NutritionFactsField.Sugars -> sugars
            NutritionFactsField.Salt -> salt
            NutritionFactsField.Cholesterol -> cholesterolMilli
            NutritionFactsField.Caffeine -> caffeineMilli
            NutritionFactsField.VitaminA -> vitaminAMicro
            NutritionFactsField.VitaminB1 -> vitaminB1Milli
            NutritionFactsField.VitaminB2 -> vitaminB2Milli
            NutritionFactsField.VitaminB3 -> vitaminB3Milli
            NutritionFactsField.VitaminB5 -> vitaminB5Milli
            NutritionFactsField.VitaminB6 -> vitaminB6Milli
            NutritionFactsField.VitaminB7 -> vitaminB7Micro
            NutritionFactsField.VitaminB9 -> vitaminB9Micro
            NutritionFactsField.VitaminB12 -> vitaminB12Micro
            NutritionFactsField.VitaminC -> vitaminCMilli
            NutritionFactsField.VitaminD -> vitaminDMicro
            NutritionFactsField.VitaminE -> vitaminEMilli
            NutritionFactsField.VitaminK -> vitaminKMicro
            NutritionFactsField.Manganese -> manganeseMilli
            NutritionFactsField.Magnesium -> magnesiumMilli
            NutritionFactsField.Potassium -> potassiumMilli
            NutritionFactsField.Calcium -> calciumMilli
            NutritionFactsField.Copper -> copperMilli
            NutritionFactsField.Zinc -> zincMilli
            NutritionFactsField.Sodium -> sodiumMilli
            NutritionFactsField.Iron -> ironMilli
            NutritionFactsField.Phosphorus -> phosphorusMilli
            NutritionFactsField.Selenium -> seleniumMicro
            NutritionFactsField.Iodine -> iodineMicro
            NutritionFactsField.Chromium -> chromiumMicro
            NutritionFactsField.TransFats -> transFats
            NutritionFactsField.AddedSugars -> addedSugars
            NutritionFactsField.DietaryFiber -> dietaryFiber
            NutritionFactsField.SolubleFiber -> solubleFiber
            NutritionFactsField.InsolubleFiber -> insolubleFiber
        }
    }

    fun get(field: NutritionFactsField) = map[field] ?: error("Unknown field: $field")

    val isEmpty: Boolean
        get() = this == Empty

    val isComplete: Boolean
        get() = map.values.all { it is NutrientValue.Complete }

    operator fun plus(other: NutritionFacts): NutritionFacts = NutritionFacts(
        proteins = this.proteins + other.proteins,
        carbohydrates = this.carbohydrates + other.carbohydrates,
        energy = this.energy + other.energy,
        fats = this.fats + other.fats,
        saturatedFats = this.saturatedFats + other.saturatedFats,
        transFats = this.transFats + other.transFats,
        monounsaturatedFats = this.monounsaturatedFats + other.monounsaturatedFats,
        polyunsaturatedFats = this.polyunsaturatedFats + other.polyunsaturatedFats,
        omega3 = this.omega3 + other.omega3,
        omega6 = this.omega6 + other.omega6,
        sugars = this.sugars + other.sugars,
        addedSugars = this.addedSugars + other.addedSugars,
        dietaryFiber = this.dietaryFiber + other.dietaryFiber,
        solubleFiber = this.solubleFiber + other.solubleFiber,
        insolubleFiber = this.insolubleFiber + other.insolubleFiber,
        salt = this.salt + other.salt,
        cholesterolMilli = this.cholesterolMilli + other.cholesterolMilli,
        caffeineMilli = this.caffeineMilli + other.caffeineMilli,
        vitaminAMicro = this.vitaminAMicro + other.vitaminAMicro,
        vitaminB1Milli = this.vitaminB1Milli + other.vitaminB1Milli,
        vitaminB2Milli = this.vitaminB2Milli + other.vitaminB2Milli,
        vitaminB3Milli = this.vitaminB3Milli + other.vitaminB3Milli,
        vitaminB5Milli = this.vitaminB5Milli + other.vitaminB5Milli,
        vitaminB6Milli = this.vitaminB6Milli + other.vitaminB6Milli,
        vitaminB7Micro = this.vitaminB7Micro + other.vitaminB7Micro,
        vitaminB9Micro = this.vitaminB9Micro + other.vitaminB9Micro,
        vitaminB12Micro = this.vitaminB12Micro + other.vitaminB12Micro,
        vitaminCMilli = this.vitaminCMilli + other.vitaminCMilli,
        vitaminDMicro = this.vitaminDMicro + other.vitaminDMicro,
        vitaminEMilli = this.vitaminEMilli + other.vitaminEMilli,
        vitaminKMicro = this.vitaminKMicro + other.vitaminKMicro,
        manganeseMilli = this.manganeseMilli + other.manganeseMilli,
        magnesiumMilli = this.magnesiumMilli + other.magnesiumMilli,
        potassiumMilli = this.potassiumMilli + other.potassiumMilli,
        calciumMilli = this.calciumMilli + other.calciumMilli,
        copperMilli = this.copperMilli + other.copperMilli,
        zincMilli = this.zincMilli + other.zincMilli,
        sodiumMilli = this.sodiumMilli + other.sodiumMilli,
        ironMilli = this.ironMilli + other.ironMilli,
        phosphorusMilli = this.phosphorusMilli + other.phosphorusMilli,
        seleniumMicro = this.seleniumMicro + other.seleniumMicro,
        iodineMicro = this.iodineMicro + other.iodineMicro,
        chromiumMicro = this.chromiumMicro + other.chromiumMicro
    )

    operator fun times(multiplier: Float): NutritionFacts = NutritionFacts(
        proteins = this.proteins * multiplier,
        carbohydrates = this.carbohydrates * multiplier,
        energy = this.energy * multiplier,
        fats = this.fats * multiplier,
        saturatedFats = this.saturatedFats * multiplier,
        transFats = this.transFats * multiplier,
        monounsaturatedFats = this.monounsaturatedFats * multiplier,
        polyunsaturatedFats = this.polyunsaturatedFats * multiplier,
        omega3 = this.omega3 * multiplier,
        omega6 = this.omega6 * multiplier,
        sugars = this.sugars * multiplier,
        addedSugars = this.addedSugars * multiplier,
        dietaryFiber = this.dietaryFiber * multiplier,
        solubleFiber = this.solubleFiber * multiplier,
        insolubleFiber = this.insolubleFiber * multiplier,
        salt = this.salt * multiplier,
        cholesterolMilli = this.cholesterolMilli * multiplier,
        caffeineMilli = this.caffeineMilli * multiplier,
        vitaminAMicro = this.vitaminAMicro * multiplier,
        vitaminB1Milli = this.vitaminB1Milli * multiplier,
        vitaminB2Milli = this.vitaminB2Milli * multiplier,
        vitaminB3Milli = this.vitaminB3Milli * multiplier,
        vitaminB5Milli = this.vitaminB5Milli * multiplier,
        vitaminB6Milli = this.vitaminB6Milli * multiplier,
        vitaminB7Micro = this.vitaminB7Micro * multiplier,
        vitaminB9Micro = this.vitaminB9Micro * multiplier,
        vitaminB12Micro = this.vitaminB12Micro * multiplier,
        vitaminCMilli = this.vitaminCMilli * multiplier,
        vitaminDMicro = this.vitaminDMicro * multiplier,
        vitaminEMilli = this.vitaminEMilli * multiplier,
        vitaminKMicro = this.vitaminKMicro * multiplier,
        manganeseMilli = this.manganeseMilli * multiplier,
        magnesiumMilli = this.magnesiumMilli * multiplier,
        potassiumMilli = this.potassiumMilli * multiplier,
        calciumMilli = this.calciumMilli * multiplier,
        copperMilli = this.copperMilli * multiplier,
        zincMilli = this.zincMilli * multiplier,
        sodiumMilli = this.sodiumMilli * multiplier,
        ironMilli = this.ironMilli * multiplier,
        phosphorusMilli = this.phosphorusMilli * multiplier,
        seleniumMicro = this.seleniumMicro * multiplier,
        iodineMicro = this.iodineMicro * multiplier,
        chromiumMicro = this.chromiumMicro * multiplier
    )

    operator fun div(divisor: Float): NutritionFacts = NutritionFacts(
        proteins = this.proteins / divisor,
        carbohydrates = this.carbohydrates / divisor,
        energy = this.energy / divisor,
        fats = this.fats / divisor,
        saturatedFats = this.saturatedFats / divisor,
        transFats = this.transFats / divisor,
        monounsaturatedFats = this.monounsaturatedFats / divisor,
        polyunsaturatedFats = this.polyunsaturatedFats / divisor,
        omega3 = this.omega3 / divisor,
        omega6 = this.omega6 / divisor,
        sugars = this.sugars / divisor,
        addedSugars = this.addedSugars / divisor,
        dietaryFiber = this.dietaryFiber / divisor,
        solubleFiber = this.solubleFiber / divisor,
        insolubleFiber = this.insolubleFiber / divisor,
        salt = this.salt / divisor,
        cholesterolMilli = this.cholesterolMilli / divisor,
        caffeineMilli = this.caffeineMilli / divisor,
        vitaminAMicro = this.vitaminAMicro / divisor,
        vitaminB1Milli = this.vitaminB1Milli / divisor,
        vitaminB2Milli = this.vitaminB2Milli / divisor,
        vitaminB3Milli = this.vitaminB3Milli / divisor,
        vitaminB5Milli = this.vitaminB5Milli / divisor,
        vitaminB6Milli = this.vitaminB6Milli / divisor,
        vitaminB7Micro = this.vitaminB7Micro / divisor,
        vitaminB9Micro = this.vitaminB9Micro / divisor,
        vitaminB12Micro = this.vitaminB12Micro / divisor,
        vitaminCMilli = this.vitaminCMilli / divisor,
        vitaminDMicro = this.vitaminDMicro / divisor,
        vitaminEMilli = this.vitaminEMilli / divisor,
        vitaminKMicro = this.vitaminKMicro / divisor,
        manganeseMilli = this.manganeseMilli / divisor,
        magnesiumMilli = this.magnesiumMilli / divisor,
        potassiumMilli = this.potassiumMilli / divisor,
        calciumMilli = this.calciumMilli / divisor,
        copperMilli = this.copperMilli / divisor,
        zincMilli = this.zincMilli / divisor,
        sodiumMilli = this.sodiumMilli / divisor,
        ironMilli = this.ironMilli / divisor,
        phosphorusMilli = this.phosphorusMilli / divisor,
        seleniumMicro = this.seleniumMicro / divisor,
        iodineMicro = this.iodineMicro / divisor,
        chromiumMicro = this.chromiumMicro / divisor
    )

    companion object {
        val Empty = NutritionFacts(
            proteins = NutrientValue.Complete(0f),
            carbohydrates = NutrientValue.Complete(0f),
            energy = NutrientValue.Complete(0f),
            fats = NutrientValue.Complete(0f),
            saturatedFats = NutrientValue.Complete(0f),
            transFats = NutrientValue.Complete(0f),
            monounsaturatedFats = NutrientValue.Complete(0f),
            polyunsaturatedFats = NutrientValue.Complete(0f),
            omega3 = NutrientValue.Complete(0f),
            omega6 = NutrientValue.Complete(0f),
            sugars = NutrientValue.Complete(0f),
            addedSugars = NutrientValue.Complete(0f),
            dietaryFiber = NutrientValue.Complete(0f),
            solubleFiber = NutrientValue.Complete(0f),
            insolubleFiber = NutrientValue.Complete(0f),
            salt = NutrientValue.Complete(0f),
            cholesterolMilli = NutrientValue.Complete(0f),
            caffeineMilli = NutrientValue.Complete(0f),
            vitaminAMicro = NutrientValue.Complete(0f),
            vitaminB1Milli = NutrientValue.Complete(0f),
            vitaminB2Milli = NutrientValue.Complete(0f),
            vitaminB3Milli = NutrientValue.Complete(0f),
            vitaminB5Milli = NutrientValue.Complete(0f),
            vitaminB6Milli = NutrientValue.Complete(0f),
            vitaminB7Micro = NutrientValue.Complete(0f),
            vitaminB9Micro = NutrientValue.Complete(0f),
            vitaminB12Micro = NutrientValue.Complete(0f),
            vitaminCMilli = NutrientValue.Complete(0f),
            vitaminDMicro = NutrientValue.Complete(0f),
            vitaminEMilli = NutrientValue.Complete(0f),
            vitaminKMicro = NutrientValue.Complete(0f),
            manganeseMilli = NutrientValue.Complete(0f),
            magnesiumMilli = NutrientValue.Complete(0f),
            potassiumMilli = NutrientValue.Complete(0f),
            calciumMilli = NutrientValue.Complete(0f),
            copperMilli = NutrientValue.Complete(0f),
            zincMilli = NutrientValue.Complete(0f),
            sodiumMilli = NutrientValue.Complete(0f),
            ironMilli = NutrientValue.Complete(0f),
            phosphorusMilli = NutrientValue.Complete(0f),
            seleniumMicro = NutrientValue.Complete(0f),
            iodineMicro = NutrientValue.Complete(0f),
            chromiumMicro = NutrientValue.Complete(0f)
        )
    }
}

fun Iterable<NutritionFacts>.sum(): NutritionFacts =
    fold(NutritionFacts.Empty) { acc, nutrients -> acc + nutrients }
