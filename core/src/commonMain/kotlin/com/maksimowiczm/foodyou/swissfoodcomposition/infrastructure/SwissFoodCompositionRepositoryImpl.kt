package com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodNameMapper
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProduct
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProductId
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionRepository
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodNutrientComponent
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodCompositionDao
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodCompositionEntity
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodNutrientComponentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SwissFoodCompositionRepositoryImpl(private val dao: SwissFoodCompositionDao) :
    SwissFoodCompositionRepository {
    override fun search(
        query: String,
        pageSize: Int,
        languageCode: String,
    ): Flow<PagingData<SwissFoodCompositionProduct>> {
        val config = PagingConfig(pageSize = pageSize)
        val factory = {
            if (query.isBlank()) {
                dao.getPagingSource(languageCode)
            } else {
                dao.getPagingSourceByQuery(query, languageCode)
            }
        }
        return Pager(config = config, pagingSourceFactory = factory).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun count(query: String): Flow<Int> {
        return if (query.isBlank()) {
            dao.observeCount()
        } else {
            dao.observeCountByQuery(query)
        }
    }
}

internal fun SwissFoodNutrientComponentEntity?.toEnergyComponent(
    unit: EnergyUnit
): SwissFoodNutrientComponent<Energy>? =
    this?.let {
        SwissFoodNutrientComponent(value = value?.let { Energy.from(it, unit) }.toNutrientValue())
    }

internal fun SwissFoodNutrientComponentEntity?.toWeightComponent():
    SwissFoodNutrientComponent<Weight>? =
    this?.let {
        SwissFoodNutrientComponent(value = value?.grams.toNutrientValue())
    }

internal fun SwissFoodCompositionEntity.toDomain() =
    SwissFoodCompositionProduct(
        id = SwissFoodCompositionProductId(id),
        name = FoodNameMapper.toFoodName(name),
        density = density,
        energyKj = energyKj.toEnergyComponent(EnergyUnit.Kilojoules),
        energyKcal = energyKcal.toEnergyComponent(EnergyUnit.Kilocalories),
        fatTotal = fatTotal.toWeightComponent(),
        fattyAcidsSaturated = fattyAcidsSaturated.toWeightComponent(),
        fattyAcidsMonounsaturated = fattyAcidsMonounsaturated.toWeightComponent(),
        fattyAcidsPolyunsaturated = fattyAcidsPolyunsaturated.toWeightComponent(),
        linoleicAcid = linoleicAcid.toWeightComponent(),
        alphaLinolenicAcid = alphaLinolenicAcid.toWeightComponent(),
        eicosapentaenoicAcidEpa = eicosapentaenoicAcidEpa.toWeightComponent(),
        docosahexaenoicAcidDha = docosahexaenoicAcidDha.toWeightComponent(),
        cholesterol = cholesterol.toWeightComponent(),
        carbohydratesAvailable = carbohydratesAvailable.toWeightComponent(),
        sugars = sugars.toWeightComponent(),
        starch = starch.toWeightComponent(),
        dietaryFibres = dietaryFibres.toWeightComponent(),
        protein = protein.toWeightComponent(),
        saltNacl = saltNacl.toWeightComponent(),
        alcohol = alcohol.toWeightComponent(),
        water = water.toWeightComponent(),
        vitaminARe = vitaminARe.toWeightComponent(),
        vitaminARae = vitaminARae.toWeightComponent(),
        retinol = retinol.toWeightComponent(),
        betaCaroteneActivity = betaCaroteneActivity.toWeightComponent(),
        betaCarotene = betaCarotene.toWeightComponent(),
        vitaminB1Thiamine = vitaminB1Thiamine.toWeightComponent(),
        vitaminB2Riboflavin = vitaminB2Riboflavin.toWeightComponent(),
        vitaminB6Pyridoxine = vitaminB6Pyridoxine.toWeightComponent(),
        vitaminB12Cobalamin = vitaminB12Cobalamin.toWeightComponent(),
        niacinEquivalent = niacinEquivalent.toWeightComponent(),
        niacin = niacin.toWeightComponent(),
        folate = folate.toWeightComponent(),
        pantothenicAcid = pantothenicAcid.toWeightComponent(),
        vitaminCAscorbicAcid = vitaminCAscorbicAcid.toWeightComponent(),
        vitaminDCalciferol = vitaminDCalciferol.toWeightComponent(),
        vitaminETocopherol = vitaminETocopherol.toWeightComponent(),
        potassium = potassium.toWeightComponent(),
        sodium = sodium.toWeightComponent(),
        chloride = chloride.toWeightComponent(),
        calcium = calcium.toWeightComponent(),
        magnesium = magnesium.toWeightComponent(),
        phosphorus = phosphorus.toWeightComponent(),
        iron = iron.toWeightComponent(),
        iodide = iodide.toWeightComponent(),
        zinc = zinc.toWeightComponent(),
        selenium = selenium.toWeightComponent(),
    )
