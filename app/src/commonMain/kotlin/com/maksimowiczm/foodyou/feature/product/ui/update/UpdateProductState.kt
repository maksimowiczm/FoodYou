package com.maksimowiczm.foodyou.feature.product.ui.update

import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormError
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormState
import pro.respawn.kmmutils.inputforms.Input
import pro.respawn.kmmutils.inputforms.dsl.isEmptyValue
import pro.respawn.kmmutils.inputforms.dsl.isValid
import pro.respawn.kmmutils.inputforms.dsl.isValidOrEmpty

internal data class UpdateProductState(
    override val name: Input,
    override val brand: Input,
    override val barcode: Input,
    override val isModified: Boolean = false,
    override val proteins: Input,
    override val carbohydrates: Input,
    override val fats: Input,
    override val sugars: Input,
    override val saturatedFats: Input,
    override val salt: Input,
    override val sodium: Input,
    override val fiber: Input,
    override val packageWeight: Input,
    override val servingWeight: Input
) : ProductFormState {
    override val isValid = name.isValid &&
        !name.isEmptyValue &&
        brand.isValidOrEmpty &&
        barcode.isValidOrEmpty &&
        proteins.isValid &&
        carbohydrates.isValid &&
        fats.isValid &&
        sugars.isValidOrEmpty &&
        saturatedFats.isValidOrEmpty &&
        salt.isValidOrEmpty &&
        sodium.isValidOrEmpty &&
        fiber.isValidOrEmpty &&
        packageWeight.isValidOrEmpty &&
        servingWeight.isValidOrEmpty

    private val proteinsValue
        get() = proteins.value.toFloatOrNull()
    private val carbohydratesValue
        get() = carbohydrates.value.toFloatOrNull()
    private val fatsValue
        get() = fats.value.toFloatOrNull()

    override val error: ProductFormError?
        get() {
            val proteins = proteinsValue ?: return null
            val carbohydrates = carbohydratesValue ?: return null
            val fats = fatsValue ?: return null

            val sum = proteins + carbohydrates + fats
            if (sum > 100) {
                return ProductFormError.MacronutrientsSumExceeds100
            }
            return null
        }

    override val calories: Float?
        get() {
            val proteins = proteinsValue ?: return null
            val carbohydrates = carbohydratesValue ?: return null
            val fats = fatsValue ?: return null

            return NutrientsHelper.calculateCalories(proteins, carbohydrates, fats)
        }
}
