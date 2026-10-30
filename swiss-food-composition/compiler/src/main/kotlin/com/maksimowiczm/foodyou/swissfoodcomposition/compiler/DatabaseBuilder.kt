package com.maksimowiczm.foodyou.swissfoodcomposition.compiler

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import arrow.core.Either
import arrow.core.nonEmptyListOf
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodNameMapper
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProduct
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodNutrientComponent
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodCompositionDatabase
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodCompositionDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodCompositionEntity
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodNutrientComponentEntity
import java.io.File
import kotlinx.coroutines.runBlocking

class DatabaseBuilder {
    fun build(
        file: File,
        products: List<SwissFoodCompositionProduct>,
    ): Either<Errors, Unit> =
        Either.catch {
                file.parentFile?.mkdirs()
                if (file.exists()) {
                    file.delete()
                }

                val entities = products.map { it.toEntity() }

                val database =
                    Room.databaseBuilder<SwissFoodCompositionDatabase>(file.absolutePath)
                        .setDriver(BundledSQLiteDriver())
                        .buildDatabase()

                try {
                    runBlocking {
                        database.swissFoodCompositionDao.insertAll(entities)
                    }
                } finally {
                    database.close()
                    File("${file.absolutePath}.lck").takeIf { it.exists() }?.delete()
                    File("${file.absolutePath}-wal").takeIf { it.exists() }?.delete()
                    File("${file.absolutePath}-shm").takeIf { it.exists() }?.delete()
                }
            }
            .mapLeft { throwable ->
                nonEmptyListOf(
                    "Failed to build database at '${file.path}': ${throwable.message ?: throwable.toString()}"
                )
            }
}

private fun SwissFoodCompositionProduct.toEntity() =
    SwissFoodCompositionEntity(
        id = id.value,
        name = FoodNameMapper.toFoodNameEntity(name),
        density = density,
        energyKj = energyKj?.toEntity(EnergyUnit.Kilojoules),
        energyKcal = energyKcal?.toEntity(EnergyUnit.Kilocalories),
        fatTotal = fatTotal?.toEntity(),
        fattyAcidsSaturated = fattyAcidsSaturated?.toEntity(),
        fattyAcidsMonounsaturated = fattyAcidsMonounsaturated?.toEntity(),
        fattyAcidsPolyunsaturated = fattyAcidsPolyunsaturated?.toEntity(),
        linoleicAcid = linoleicAcid?.toEntity(),
        alphaLinolenicAcid = alphaLinolenicAcid?.toEntity(),
        eicosapentaenoicAcidEpa = eicosapentaenoicAcidEpa?.toEntity(),
        docosahexaenoicAcidDha = docosahexaenoicAcidDha?.toEntity(),
        cholesterol = cholesterol?.toEntity(),
        carbohydratesAvailable = carbohydratesAvailable?.toEntity(),
        sugars = sugars?.toEntity(),
        starch = starch?.toEntity(),
        dietaryFibres = dietaryFibres?.toEntity(),
        protein = protein?.toEntity(),
        saltNacl = saltNacl?.toEntity(),
        alcohol = alcohol?.toEntity(),
        water = water?.toEntity(),
        vitaminARe = vitaminARe?.toEntity(),
        vitaminARae = vitaminARae?.toEntity(),
        retinol = retinol?.toEntity(),
        betaCaroteneActivity = betaCaroteneActivity?.toEntity(),
        betaCarotene = betaCarotene?.toEntity(),
        vitaminB1Thiamine = vitaminB1Thiamine?.toEntity(),
        vitaminB2Riboflavin = vitaminB2Riboflavin?.toEntity(),
        vitaminB6Pyridoxine = vitaminB6Pyridoxine?.toEntity(),
        vitaminB12Cobalamin = vitaminB12Cobalamin?.toEntity(),
        niacinEquivalent = niacinEquivalent?.toEntity(),
        niacin = niacin?.toEntity(),
        folate = folate?.toEntity(),
        pantothenicAcid = pantothenicAcid?.toEntity(),
        vitaminCAscorbicAcid = vitaminCAscorbicAcid?.toEntity(),
        vitaminDCalciferol = vitaminDCalciferol?.toEntity(),
        vitaminETocopherol = vitaminETocopherol?.toEntity(),
        potassium = potassium?.toEntity(),
        sodium = sodium?.toEntity(),
        chloride = chloride?.toEntity(),
        calcium = calcium?.toEntity(),
        magnesium = magnesium?.toEntity(),
        phosphorus = phosphorus?.toEntity(),
        iron = iron?.toEntity(),
        iodide = iodide?.toEntity(),
        zinc = zinc?.toEntity(),
        selenium = selenium?.toEntity(),
    )

private fun SwissFoodNutrientComponent<Energy>.toEntity(unit: EnergyUnit) =
    SwissFoodNutrientComponentEntity(value = value.value?.toDouble(unit))

private fun SwissFoodNutrientComponent<Weight>.toEntity() =
    SwissFoodNutrientComponentEntity(value = value.value?.grams)
