package com.maksimowiczm.foodyou.food.infrastructure.user

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.FluidOunces
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Milliliters
import com.maksimowiczm.foodyou.common.domain.Ounces
import com.maksimowiczm.foodyou.common.domain.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.Quantity
import com.maksimowiczm.foodyou.common.domain.ServingQuantity
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.infrastructure.user.room.QuantityEntity
import com.maksimowiczm.foodyou.food.infrastructure.user.room.QuantityType
import com.maksimowiczm.foodyou.food.infrastructure.user.room.Unit
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodDao
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodEntity
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchQuery
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserFoodRepository(private val dao: UserFoodDao, private val nameSelector: FoodNameSelector) {
    private val mapper = UserFoodMapper()

    @OptIn(ExperimentalPagingApi::class)
    fun search(
        searchFoodParams: SearchParameters.User,
        pageSize: Int,
    ): Flow<PagingData<SearchableFoodDto>> {
        val language = nameSelector.select()

        val config = PagingConfig(pageSize = pageSize)
        val factory = {
            when (searchFoodParams.query) {
                SearchQuery.Blank ->
                    dao.getPagingSource(language.tag, searchFoodParams.accountId.value)

                is SearchQuery.Barcode ->
                    dao.getPagingSourceByBarcode(
                        searchFoodParams.query.barcode,
                        language.tag,
                        searchFoodParams.accountId.value,
                    )

                is SearchQuery.Text ->
                    dao.getPagingSourceByQuery(
                        searchFoodParams.query.query,
                        language.tag,
                        searchFoodParams.accountId.value,
                    )
            }
        }

        return Pager(config = config, pagingSourceFactory = factory).flow.map { data ->
            data.map(mapper::map)
        }
    }

    fun count(parameters: SearchParameters.User): Flow<Int> {
        val countFlow: Flow<Int> =
            when (parameters.query) {
                SearchQuery.Blank -> dao.observeCount(parameters.accountId.value)

                is SearchQuery.Barcode ->
                    dao.observeCountByBarcode(parameters.query.barcode, parameters.accountId.value)

                is SearchQuery.Text ->
                    dao.observeCountByQuery(parameters.query.query, parameters.accountId.value)
            }

        return countFlow
    }
}

class UserFoodMapper {
    fun map(entity: UserFoodEntity): SearchableFoodDto =
        with(entity) {
            val name =
                FoodName(
                    english = name.english,
                    catalan = name.catalan,
                    danish = name.danish,
                    german = name.german,
                    spanish = name.spanish,
                    french = name.french,
                    italian = name.italian,
                    hungarian = name.hungarian,
                    dutch = name.dutch,
                    polish = name.polish,
                    portugueseBrazil = name.portugueseBrazil,
                    turkish = name.turkish,
                    russian = name.russian,
                    ukrainian = name.ukrainian,
                    arabic = name.arabic,
                    chineseSimplified = name.chineseSimplified,
                    fallback = name.fallback,
                )

            val nutrients =
                NutritionFacts.requireAll(
                    proteins = nutrients.proteins.toNutrientValue(),
                    carbohydrates = nutrients.carbohydrates.toNutrientValue(),
                    energy = nutrients.energy.toNutrientValue(),
                    fats = nutrients.fats.toNutrientValue(),
                    saturatedFats = nutrients.saturatedFats.toNutrientValue(),
                    transFats = nutrients.transFats.toNutrientValue(),
                    monounsaturatedFats = nutrients.monounsaturatedFats.toNutrientValue(),
                    polyunsaturatedFats = nutrients.polyunsaturatedFats.toNutrientValue(),
                    omega3 = nutrients.omega3.toNutrientValue(),
                    omega6 = nutrients.omega6.toNutrientValue(),
                    sugars = nutrients.sugars.toNutrientValue(),
                    addedSugars = nutrients.addedSugars.toNutrientValue(),
                    dietaryFiber = nutrients.dietaryFiber.toNutrientValue(),
                    solubleFiber = nutrients.solubleFiber.toNutrientValue(),
                    insolubleFiber = nutrients.insolubleFiber.toNutrientValue(),
                    salt = nutrients.salt.toNutrientValue(),
                    cholesterol = nutrients.cholesterol.toNutrientValue(),
                    caffeine = nutrients.caffeine.toNutrientValue(),
                    vitaminA = nutrients.vitaminA.toNutrientValue(),
                    vitaminB1 = nutrients.vitaminB1.toNutrientValue(),
                    vitaminB2 = nutrients.vitaminB2.toNutrientValue(),
                    vitaminB3 = nutrients.vitaminB3.toNutrientValue(),
                    vitaminB5 = nutrients.vitaminB5.toNutrientValue(),
                    vitaminB6 = nutrients.vitaminB6.toNutrientValue(),
                    vitaminB7 = nutrients.vitaminB7.toNutrientValue(),
                    vitaminB9 = nutrients.vitaminB9.toNutrientValue(),
                    vitaminB12 = nutrients.vitaminB12.toNutrientValue(),
                    vitaminC = nutrients.vitaminC.toNutrientValue(),
                    vitaminD = nutrients.vitaminD.toNutrientValue(),
                    vitaminE = nutrients.vitaminE.toNutrientValue(),
                    vitaminK = nutrients.vitaminK.toNutrientValue(),
                    manganese = nutrients.manganese.toNutrientValue(),
                    magnesium = nutrients.magnesium.toNutrientValue(),
                    potassium = nutrients.potassium.toNutrientValue(),
                    calcium = nutrients.calcium.toNutrientValue(),
                    copper = nutrients.copper.toNutrientValue(),
                    zinc = nutrients.zinc.toNutrientValue(),
                    sodium = nutrients.sodium.toNutrientValue(),
                    iron = nutrients.iron.toNutrientValue(),
                    phosphorus = nutrients.phosphorus.toNutrientValue(),
                    selenium = nutrients.selenium.toNutrientValue(),
                    iodine = nutrients.iodine.toNutrientValue(),
                    chromium = nutrients.chromium.toNutrientValue(),
                )

            val servingQuantity = servingSize?.toQuantity()
            val packageQuantity = packageSize?.toQuantity()

            // Probably move it to domain service
            val suggestedQuantity: Quantity =
                when {
                    servingQuantity != null -> ServingQuantity(1.0)
                    packageQuantity != null -> PackageQuantity(1.0)
                    else -> AbsoluteQuantity.Weight(Grams(100.0))
                }

            // TODO
            val image: FoodImage? = null

            return SearchableFoodDto(
                identity = FoodProductIdentity.Local(id),
                name = name,
                nutritionFacts = nutrients,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                suggestedQuantity = suggestedQuantity,
                image = image,
            )
        }

    private fun QuantityEntity.toQuantity(): AbsoluteQuantity =
        when (type) {
            QuantityType.Weight -> {
                val weight =
                    when (unit) {
                        Unit.Grams -> Grams(amount)
                        Unit.Ounces -> Ounces(amount)
                        Unit.Milliliters,
                        Unit.FluidOunces -> error("Invalid unit for weight: $unit")
                    }

                AbsoluteQuantity.Weight(weight)
            }

            QuantityType.Volume -> {
                val volume =
                    when (unit) {
                        Unit.Grams,
                        Unit.Ounces -> error("Invalid unit for volume: $unit")

                        Unit.Milliliters -> Milliliters(amount)
                        Unit.FluidOunces -> FluidOunces(amount)
                    }

                AbsoluteQuantity.Volume(volume)
            }
        }
}
