package com.maksimowiczm.foodyou.swissfoodcomposition.compiler

import arrow.core.Either
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProduct
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProductId
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodNutrientComponent

class EntityBuilder {
    fun build(
        csv: CsvContent,
        translationsMap: TranslationsMap,
    ): Either<Errors, List<SwissFoodCompositionProduct>> = either {
        val products = mutableListOf<SwissFoodCompositionProduct>()

        for ((rowIndex, row) in csv.withIndex()) {
            val idStr = row["ID"]
            val id =
                ensureNotNull(idStr?.toLongOrNull()) {
                    nonEmptyListOf("Row ${rowIndex + 2}: Invalid or missing ID '$idStr'")
                }

            val englishName =
                checkNotNull(translationsMap[Language.English]?.get(id.toInt())) {
                    nonEmptyListOf("Row ${rowIndex + 2}: Missing English name for ID '$id'")
                }

            val foodName =
                FoodName.requireAll(
                    english = englishName,
                    catalan = translationsMap[Language.Catalan]?.get(id.toInt()),
                    czech = translationsMap[Language.Czech]?.get(id.toInt()),
                    danish = translationsMap[Language.Danish]?.get(id.toInt()),
                    german = translationsMap[Language.German]?.get(id.toInt()),
                    spanish = translationsMap[Language.Spanish]?.get(id.toInt()),
                    french = translationsMap[Language.French]?.get(id.toInt()),
                    indonesian = translationsMap[Language.Indonesian]?.get(id.toInt()),
                    italian = translationsMap[Language.Italian]?.get(id.toInt()),
                    hungarian = translationsMap[Language.Hungarian]?.get(id.toInt()),
                    dutch = translationsMap[Language.Dutch]?.get(id.toInt()),
                    polish = translationsMap[Language.Polish]?.get(id.toInt()),
                    portugueseBrazil = translationsMap[Language.PortugueseBrazil]?.get(id.toInt()),
                    portuguesePortugal =
                        translationsMap[Language.PortuguesePortugal]?.get(id.toInt()),
                    slovenian = translationsMap[Language.Slovenian]?.get(id.toInt()),
                    turkish = translationsMap[Language.Turkish]?.get(id.toInt()),
                    russian = translationsMap[Language.Russian]?.get(id.toInt()),
                    ukrainian = translationsMap[Language.Ukrainian]?.get(id.toInt()),
                    arabic = translationsMap[Language.Arabic]?.get(id.toInt()),
                    chineseSimplified =
                        translationsMap[Language.ChineseSimplified]?.get(id.toInt()),
                    fallback = englishName,
                )

            val density = parseNutrientValue(row["Density"])

            val product =
                SwissFoodCompositionProduct(
                    id = SwissFoodCompositionProductId(id),
                    name = foodName,
                    density = density,
                    energyKj =
                        parseEnergyNutrient(row, "Energy, kilojoules (kJ)", EnergyUnit.Kilojoules),
                    energyKcal =
                        parseEnergyNutrient(
                            row,
                            "Energy, kilocalories (kcal)",
                            EnergyUnit.Kilocalories,
                        ),
                    fatTotal = parseWeightNutrient(row, "Fat, total (g)") { grams },
                    fattyAcidsSaturated =
                        parseWeightNutrient(row, "Fatty acids, saturated (g)") { grams },
                    fattyAcidsMonounsaturated =
                        parseWeightNutrient(row, "Fatty acids, monounsaturated (g)") { grams },
                    fattyAcidsPolyunsaturated =
                        parseWeightNutrient(row, "Fatty acids, polyunsaturated (g)") { grams },
                    linoleicAcid = parseWeightNutrient(row, "Linoleic acid (g)") { grams },
                    alphaLinolenicAcid =
                        parseWeightNutrient(row, "Alpha-linolenic acid (g)") { grams },
                    eicosapentaenoicAcidEpa =
                        parseWeightNutrient(row, "Eicosapentaenoic acid EPA (g)") { grams },
                    docosahexaenoicAcidDha =
                        parseWeightNutrient(row, "Docosahexaenoic acid (DHA) (g)") { grams },
                    cholesterol = parseWeightNutrient(row, "Cholesterol (mg)") { milligrams },
                    carbohydratesAvailable =
                        parseWeightNutrient(row, "Carbohydrates, available (g)") { grams },
                    sugars = parseWeightNutrient(row, "Sugars (g)") { grams },
                    starch = parseWeightNutrient(row, "Starch (g)") { grams },
                    dietaryFibres = parseWeightNutrient(row, "Dietary fibres (g)") { grams },
                    protein = parseWeightNutrient(row, "Protein (g)") { grams },
                    saltNacl = parseWeightNutrient(row, "Salt (NaCl) (g)") { grams },
                    alcohol = parseWeightNutrient(row, "Alcohol (g)") { grams },
                    water = parseWeightNutrient(row, "Water (g)") { grams },
                    vitaminARe =
                        parseWeightNutrient(row, "Vitamin A activity, RE (µg)") { micrograms },
                    vitaminARae =
                        parseWeightNutrient(row, "Vitamin A activity, RAE (µg)") { micrograms },
                    retinol = parseWeightNutrient(row, "Retinol (µg)") { micrograms },
                    betaCaroteneActivity =
                        parseWeightNutrient(row, "Beta- carotene activity (µg)") { micrograms },
                    betaCarotene = parseWeightNutrient(row, "Beta-carotene (µg)") { micrograms },
                    vitaminB1Thiamine =
                        parseWeightNutrient(row, "Vitamin B1 (thiamine) (mg)") { milligrams },
                    vitaminB2Riboflavin =
                        parseWeightNutrient(row, "Vitamin B2 (riboflavin) (mg)") { milligrams },
                    vitaminB6Pyridoxine =
                        parseWeightNutrient(row, "Vitamin B6 (pyridoxine) (mg)") { milligrams },
                    vitaminB12Cobalamin =
                        parseWeightNutrient(row, "Vitamin B12 (cobalamin) (µg)") { micrograms },
                    niacinEquivalent =
                        parseWeightNutrient(row, "Niacin equivalent (mg)") { milligrams },
                    niacin = parseWeightNutrient(row, "Niacin (mg)") { milligrams },
                    folate = parseWeightNutrient(row, "Folate (µg)") { micrograms },
                    pantothenicAcid =
                        parseWeightNutrient(row, "Pantothenic acid (mg)") { milligrams },
                    vitaminCAscorbicAcid =
                        parseWeightNutrient(row, "Vitamin C (ascorbic acid) (mg)") { milligrams },
                    vitaminDCalciferol =
                        parseWeightNutrient(row, "Vitamin D (calciferol) (µg)") { micrograms },
                    vitaminETocopherol =
                        parseWeightNutrient(row, "Vitamin E (α-tocopherol) (mg)") { milligrams },
                    potassium = parseWeightNutrient(row, "Potassium (K) (mg)") { milligrams },
                    sodium = parseWeightNutrient(row, "Sodium (Na) (mg)") { milligrams },
                    chloride = parseWeightNutrient(row, "Chloride (Cl) (mg)") { milligrams },
                    calcium = parseWeightNutrient(row, "Calcium (Ca) (mg)") { milligrams },
                    magnesium = parseWeightNutrient(row, "Magnesium (Mg) (mg)") { milligrams },
                    phosphorus = parseWeightNutrient(row, "Phosphorus (P) (mg)") { milligrams },
                    iron = parseWeightNutrient(row, "Iron (Fe) (mg)") { milligrams },
                    iodide = parseWeightNutrient(row, "Iodide (I) (µg)") { micrograms },
                    zinc = parseWeightNutrient(row, "Zinc (Zn) (mg)") { milligrams },
                    selenium = parseWeightNutrient(row, "Selenium (Se) (µg)") { micrograms },
                )

            products.add(product)
        }

        products
    }

    private fun parseNutrientValue(valueStr: String?): Double? {
        if (valueStr == null) return null
        val trimmed = valueStr.trim()
        if (trimmed.isEmpty() || trimmed.lowercase() == "n.d." || trimmed.lowercase() == "n.b.")
            return null
        if (trimmed.lowercase() == "tr." || trimmed.lowercase() == "tr") return 0.0
        val cleaned = trimmed.removePrefix("<").removePrefix(">").trim()
        return cleaned.toDoubleOrNull()
    }

    private fun parseEnergyNutrient(
        row: Map<String, String?>,
        nutrientName: String,
        unit: EnergyUnit,
    ): SwissFoodNutrientComponent<Energy>? {
        val valueStr = row[nutrientName]
        val rawValue = parseNutrientValue(valueStr) ?: return null

        val energyValue = rawValue.let { Energy.from(it, unit).toNutrientValue() }

        return SwissFoodNutrientComponent(value = energyValue)
    }

    private fun parseWeightNutrient(
        row: Map<String, String?>,
        nutrientName: String,
        factory: Double.() -> Weight,
    ): SwissFoodNutrientComponent<Weight>? {
        val valueStr = row[nutrientName]
        val rawValue = parseNutrientValue(valueStr) ?: return null
        val weightValue = rawValue.let(factory).inUnit(WeightUnit.Grams).toNutrientValue()
        return SwissFoodNutrientComponent(value = weightValue)
    }
}
