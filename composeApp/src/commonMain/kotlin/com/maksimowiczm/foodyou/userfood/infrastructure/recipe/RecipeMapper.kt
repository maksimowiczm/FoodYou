package com.maksimowiczm.foodyou.userfood.infrastructure.recipe

import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.domain.ounces
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnit
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.recipe.FoodReference
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipe
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIdentity
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIngredient
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeName
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeQuantityEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeQuantityType
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeWithIngredients
import kotlinx.serialization.json.Json

internal class RecipeMapper {

    fun toDomain(entity: RecipeWithIngredients): UserRecipe {
        return UserRecipe(
            identity = UserRecipeIdentity(entity.recipe.uuid),
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
        )
    }

    fun toIngredientEntities(
        ingredients: List<UserRecipeIngredient>,
        recipeSqliteId: Long = 0,
    ): List<RecipeIngredientEntity> {
        return ingredients.map { ingredient ->
            RecipeIngredientEntity(
                recipeSqliteId = recipeSqliteId,
                foodReferenceJson = Json.encodeToString(ingredient.foodReference),
                quantity = toQuantityEntity(ingredient.quantity),
            )
        }
    }

    private fun toIngredient(entity: RecipeIngredientEntity): UserRecipeIngredient {
        val foodReference = Json.decodeFromString<FoodReference>(entity.foodReferenceJson)
        val quantity = toQuantity(entity.quantity)

        return UserRecipeIngredient(foodReference = foodReference, quantity = quantity)
    }

    private fun toQuantityEntity(quantity: Quantity): RecipeQuantityEntity {
        return when (quantity) {
            is AbsoluteQuantity.Weight ->
                when (quantity.weight.unit) {
                    WeightUnit.Micrograms ->
                        RecipeQuantityEntity(
                            RecipeQuantityType.Weight,
                            quantity.weight.micrograms,
                            MeasurementUnit.Micrograms,
                        )

                    WeightUnit.Milligrams ->
                        RecipeQuantityEntity(
                            RecipeQuantityType.Weight,
                            quantity.weight.milligrams,
                            MeasurementUnit.Milligrams,
                        )

                    WeightUnit.Grams ->
                        RecipeQuantityEntity(
                            RecipeQuantityType.Weight,
                            quantity.weight.grams,
                            MeasurementUnit.Grams,
                        )

                    WeightUnit.Ounces ->
                        RecipeQuantityEntity(
                            RecipeQuantityType.Weight,
                            quantity.weight.ounces,
                            MeasurementUnit.Ounces,
                        )
                }

            is AbsoluteQuantity.Volume ->
                when (quantity.volume.unit) {
                    VolumeUnit.Milliliters ->
                        RecipeQuantityEntity(
                            RecipeQuantityType.Volume,
                            quantity.volume.milliliters,
                            MeasurementUnit.Milliliters,
                        )

                    VolumeUnit.FluidOunces ->
                        RecipeQuantityEntity(
                            RecipeQuantityType.Volume,
                            quantity.volume.fluidOunces,
                            MeasurementUnit.FluidOunces,
                        )
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

    private fun toQuantity(entity: RecipeQuantityEntity): Quantity =
        when (entity.type) {
            RecipeQuantityType.Weight ->
                AbsoluteQuantity.Weight(
                    weight =
                        when (entity.unit) {
                            MeasurementUnit.Micrograms -> entity.amount.micrograms
                            MeasurementUnit.Grams -> entity.amount.grams
                            MeasurementUnit.Ounces -> entity.amount.ounces
                            else -> error("Unexpected unit ${entity.unit} for weight")
                        }
                )

            RecipeQuantityType.Volume ->
                AbsoluteQuantity.Volume(
                    volume =
                        when (entity.unit) {
                            MeasurementUnit.Milliliters -> entity.amount.milliliters
                            MeasurementUnit.FluidOunces -> entity.amount.fluidOunces
                            else -> error("Unexpected unit ${entity.unit} for volume")
                        }
                )

            RecipeQuantityType.Package -> PackageQuantity(packages = entity.amount)
            RecipeQuantityType.Serving -> ServingQuantity(servings = entity.amount)
        }
}
