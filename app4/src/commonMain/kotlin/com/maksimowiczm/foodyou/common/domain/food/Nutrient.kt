package com.maksimowiczm.foodyou.common.domain.food

enum class Nutrient {
    Energy,
    Proteins,
    Carbohydrates,
    Fats,
    SaturatedFats,
    TransFats,
    MonounsaturatedFats,
    PolyunsaturatedFats,
    Omega3,
    Omega6,
    Sugars,
    AddedSugars,
    DietaryFiber,
    SolubleFiber,
    InsolubleFiber,
    Salt,
    Cholesterol,
    Caffeine,
    VitaminA,
    VitaminB1,
    VitaminB2,
    VitaminB3,
    VitaminB5,
    VitaminB6,
    VitaminB7,
    VitaminB9,
    VitaminB12,
    VitaminC,
    VitaminD,
    VitaminE,
    VitaminK,
    Manganese,
    Magnesium,
    Potassium,
    Calcium,
    Copper,
    Zinc,
    Sodium,
    Iron,
    Phosphorus,
    Selenium,
    Iodine,
    Chromium;

    companion object {
        val all = entries.toSet()
        val basic = setOf(Proteins, Carbohydrates, Fats, Energy)
    }
}
