package com.maksimowiczm.foodyou.business.shared.domain.nutrients

operator fun NutritionFacts.get(field: NutritionFactsField): NutrientValue =
    when (field) {
        NutritionFactsField.Energy -> energy
        NutritionFactsField.Proteins -> proteins
        NutritionFactsField.Fats -> fats
        NutritionFactsField.SaturatedFats -> saturatedFats
        NutritionFactsField.TransFats -> transFats
        NutritionFactsField.MonounsaturatedFats -> monounsaturatedFats
        NutritionFactsField.PolyunsaturatedFats -> polyunsaturatedFats
        NutritionFactsField.Omega3 -> omega3
        NutritionFactsField.Omega6 -> omega6
        NutritionFactsField.Carbohydrates -> carbohydrates
        NutritionFactsField.Sugars -> sugars
        NutritionFactsField.AddedSugars -> addedSugars
        NutritionFactsField.DietaryFiber -> dietaryFiber
        NutritionFactsField.SolubleFiber -> solubleFiber
        NutritionFactsField.InsolubleFiber -> insolubleFiber
        NutritionFactsField.Salt -> salt
        NutritionFactsField.Cholesterol -> cholesterol
        NutritionFactsField.Caffeine -> caffeine
        NutritionFactsField.VitaminA -> vitaminA
        NutritionFactsField.VitaminB1 -> vitaminB1
        NutritionFactsField.VitaminB2 -> vitaminB2
        NutritionFactsField.VitaminB3 -> vitaminB3
        NutritionFactsField.VitaminB5 -> vitaminB5
        NutritionFactsField.VitaminB6 -> vitaminB6
        NutritionFactsField.VitaminB7 -> vitaminB7
        NutritionFactsField.VitaminB9 -> vitaminB9
        NutritionFactsField.VitaminB12 -> vitaminB12
        NutritionFactsField.VitaminC -> vitaminC
        NutritionFactsField.VitaminD -> vitaminD
        NutritionFactsField.VitaminE -> vitaminE
        NutritionFactsField.VitaminK -> vitaminK
        NutritionFactsField.Manganese -> manganese
        NutritionFactsField.Magnesium -> magnesium
        NutritionFactsField.Potassium -> potassium
        NutritionFactsField.Calcium -> calcium
        NutritionFactsField.Copper -> copper
        NutritionFactsField.Zinc -> zinc
        NutritionFactsField.Sodium -> sodium
        NutritionFactsField.Iron -> iron
        NutritionFactsField.Phosphorus -> phosphorus
        NutritionFactsField.Selenium -> selenium
        NutritionFactsField.Iodine -> iodine
        NutritionFactsField.Chromium -> chromium
    }

val NutritionFacts.isComplete: Boolean
    get() = NutritionFactsField.entries.all { get(it).isComplete }
