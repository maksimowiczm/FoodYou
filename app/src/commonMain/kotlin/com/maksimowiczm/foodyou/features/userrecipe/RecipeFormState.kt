package com.maksimowiczm.foodyou.features.userrecipe

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.form.rememberFormField
import com.maksimowiczm.foodyou.shared.ui.form.validateDouble
import com.maksimowiczm.foodyou.shared.ui.utility.LocalBlobResolver
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import foodyou.app.generated.resources.*
import io.konform.validation.ifPresent
import org.jetbrains.compose.resources.stringResource

@Stable
class RecipeFormState(
    val name: FormField,
    val servings: FormField,
    val note: FormField,
    private val defaultImageUri: String?,
    val imageUri: MutableState<String?>,
) {
    val isValid: Boolean by derivedStateOf { name.isValid && servings.isValid && note.isValid }

    val isModified: Boolean by derivedStateOf {
        name.isModified ||
            servings.isModified ||
            note.isModified ||
            imageUri.value != defaultImageUri
    }
}

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
        rememberFormField(defaultValue = recipe?.servings?.formatCompact() ?: "1") {
            constrain(required) { !it.isNullOrBlank() }
            ifPresent {
                validateDouble {
                    constrain(invalidNumber) { it != null }
                    constrain(valueMustBePositive) { it?.let { it > 0 } ?: true }
                }
            }
        }

    val note = rememberFormField(recipe?.note)

    val imageUri = recipe?.image?.let { LocalBlobResolver.current.resolve(it).value }
    val imageUriState = rememberSaveable(imageUri) { mutableStateOf(imageUri) }

    return remember(name, servings, note, imageUriState, imageUri) {
        RecipeFormState(name, servings, note, imageUri, imageUriState)
    }
}
