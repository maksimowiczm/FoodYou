package com.maksimowiczm.foodyou.feature.product.ui.create

import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormError
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormState
import pro.respawn.kmmutils.inputforms.Input
import pro.respawn.kmmutils.inputforms.dsl.input
import pro.respawn.kmmutils.inputforms.dsl.isEmptyValue
import pro.respawn.kmmutils.inputforms.dsl.isValid
import pro.respawn.kmmutils.inputforms.dsl.isValidOrEmpty

internal data class CreateProductState(
    override val name: Input = input(),
    override val brand: Input = input(),
    override val barcode: Input = input(),
    override val isModified: Boolean = false,
    override val proteins: Input = input(),
    override val carbohydrates: Input = input(),
    override val fats: Input = input(),
    override val sugars: Input = input(),
    override val saturatedFats: Input = input(),
    override val salt: Input = input(),
    override val sodium: Input = input(),
    override val fiber: Input = input(),
    override val packageWeight: Input = input(),
    override val servingWeight: Input = input()
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
