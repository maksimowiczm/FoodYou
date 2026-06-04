package com.maksimowiczm.foodyou.app.ui.food.recipe

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.form.rememberFormField
import com.maksimowiczm.foodyou.app.ui.common.form.validateDouble
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import foodyou.app.generated.resources.*
import io.konform.validation.ifPresent
import org.jetbrains.compose.resources.stringResource

@Stable
class RecipeFormState(
    val name: FormField,
    val servings: FormField,
    val note: FormField,
    val imageUri: MutableState<String?>,
)

@Composable
fun rememberRecipeFormState(recipe: UserRecipe?): RecipeFormState {
    val required = stringResource(Res.string.neutral_required)
    val invalidNumber = stringResource(Res.string.error_invalid_number)
    val valueMustBePositive = stringResource(Res.string.error_value_must_be_positive)

    val name =
        rememberFormField(defaultValue = recipe?.name?.fallback) {
            constrain(required) { !it.isNullOrBlank() }
        }

    val servings =
        rememberFormField(defaultValue = recipe?.servings?.toString() ?: "1") {
            constrain(required) { !it.isNullOrBlank() }
            ifPresent {
                validateDouble {
                    constrain(invalidNumber) { it != null }
                    constrain(valueMustBePositive) { it?.let { it > 0 } ?: true }
                }
            }
        }

    val note = rememberFormField(recipe?.note)

    val imageUri = recipe?.image?.let { resolveBlob(it).value }
    val imageUriState = rememberSaveable(imageUri) { mutableStateOf(imageUri) }

    return remember(name, servings, note, imageUriState) {
        RecipeFormState(name, servings, note, imageUriState)
    }
}
