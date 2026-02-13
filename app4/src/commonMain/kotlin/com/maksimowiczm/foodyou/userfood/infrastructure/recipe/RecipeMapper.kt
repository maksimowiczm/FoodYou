package com.maksimowiczm.foodyou.userfood.infrastructure.recipe

import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FluidOunces
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.Milliliters
import com.maksimowiczm.foodyou.common.domain.food.Ounces
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnit
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.recipe.FoodReference
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipe
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIdentity
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIngredient
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeName
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.FoodReferenceType
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeQuantityEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeQuantityType
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeWithIngredients

internal class RecipeMapper {

    fun toDomain(entity: RecipeWithIngredients): UserRecipe {
        return UserRecipe(
            identity =
                UserRecipeIdentity(entity.recipe.uuid, LocalAccountId(entity.recipe.accountId)),
            name = UserRecipeName(entity.recipe.name),
            servings = entity.recipe.servings,
            image = entity.recipe.imagePath?.let { Image.Local(it) },
            note = entity.recipe.note?.let { UserFoodNote(it) },
            finalWeight = entity.recipe.finalWeight,
            ingredients = entity.ingredients.map { toIngredient(it) },
        )
    }

    fun toEntity(recipe: UserRecipe, sqliteId: Long = 0): RecipeEntity {
        return RecipeEntity(
            sqliteId = sqliteId,
            uuid = recipe.identity.id,
            name = recipe.name.value,
            servings = recipe.servings,
            imagePath = recipe.image?.uri,
            note = recipe.note?.value?.takeIf { it.isNotBlank() },
            finalWeight = recipe.finalWeight,
            accountId = recipe.identity.accountId.value,
        )
    }

    fun toIngredientEntities(
        ingredients: List<UserRecipeIngredient>,
        recipeSqliteId: Long = 0,
    ): List<RecipeIngredientEntity> {
        return ingredients.map { ingredient ->
            RecipeIngredientEntity(
                recipeSqliteId = recipeSqliteId,
                foodReferenceType =
                    when (ingredient.foodReference) {
                        is FoodReference.UserProduct -> FoodReferenceType.UserFood
                        is FoodReference.FoodDataCentral -> FoodReferenceType.FoodDataCentral
                        is FoodReference.OpenFoodFacts -> FoodReferenceType.OpenFoodFacts
                        is FoodReference.UserRecipe -> FoodReferenceType.UserRecipe
                    },
                foodId = ingredient.foodReference.foodId,
                quantity = toQuantityEntity(ingredient.quantity),
            )
        }
    }

    private fun toIngredient(entity: RecipeIngredientEntity): UserRecipeIngredient {
        val foodReference =
            when (entity.foodReferenceType) {
                FoodReferenceType.UserFood -> FoodReference.UserProduct(entity.foodId)
                FoodReferenceType.FoodDataCentral -> FoodReference.FoodDataCentral(entity.foodId)
                FoodReferenceType.OpenFoodFacts -> FoodReference.OpenFoodFacts(entity.foodId)
                FoodReferenceType.UserRecipe -> FoodReference.UserRecipe(entity.foodId)
            }

        val quantity = toQuantity(entity.quantity)

        return UserRecipeIngredient(foodReference = foodReference, quantity = quantity)
    }

    private fun toQuantityEntity(quantity: Quantity): RecipeQuantityEntity {
        return when (quantity) {
            is AbsoluteQuantity.Weight -> {
                val (amount, unit) =
                    when (val weight = quantity.weight) {
                        is Grams -> weight.grams to MeasurementUnit.Grams
                        is Ounces -> weight.ounces to MeasurementUnit.Ounces
                    }
                RecipeQuantityEntity(type = RecipeQuantityType.Weight, amount = amount, unit = unit)
            }

            is AbsoluteQuantity.Volume -> {
                val (amount, unit) =
                    when (val volume = quantity.volume) {
                        is Milliliters -> volume.milliliters to MeasurementUnit.Milliliters
                        is FluidOunces -> volume.fluidOunces to MeasurementUnit.FluidOunces
                    }
                RecipeQuantityEntity(type = RecipeQuantityType.Volume, amount = amount, unit = unit)
            }

            is PackageQuantity ->
                RecipeQuantityEntity(
                    type = RecipeQuantityType.Package,
                    amount = quantity.packages,
                    unit = null,
                )

            is ServingQuantity ->
                RecipeQuantityEntity(
                    type = RecipeQuantityType.Serving,
                    amount = quantity.servings,
                    unit = null,
                )
        }
    }

    private fun toQuantity(entity: RecipeQuantityEntity): Quantity {
        return when (entity.type) {
            RecipeQuantityType.Weight -> {
                val weight =
                    when (entity.unit) {
                        MeasurementUnit.Grams -> Grams(entity.amount)
                        MeasurementUnit.Ounces -> Ounces(entity.amount)
                        MeasurementUnit.Milliliters,
                        MeasurementUnit.FluidOunces,
                        null -> error("Invalid unit for weight: ${entity.unit}")
                    }
                AbsoluteQuantity.Weight(weight)
            }

            RecipeQuantityType.Volume -> {
                val volume =
                    when (entity.unit) {
                        MeasurementUnit.Milliliters -> Milliliters(entity.amount)
                        MeasurementUnit.FluidOunces -> FluidOunces(entity.amount)
                        MeasurementUnit.Grams,
                        MeasurementUnit.Ounces,
                        null -> error("Invalid unit for volume: ${entity.unit}")
                    }
                AbsoluteQuantity.Volume(volume)
            }

            RecipeQuantityType.Package -> PackageQuantity(entity.amount)

            RecipeQuantityType.Serving -> ServingQuantity(entity.amount)
        }
    }
}
