package com.maksimowiczm.foodyou.features.userrecipe

import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.shared.ui.utility.FoodNameSelector
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.first

class RecipeFormTransformer(private val foodNameSelector: FoodNameSelector) {
    data class Result(
        val name: FoodName,
        val note: String?,
        val imageBytes: ByteArray?,
        val servings: Double,
        val components: List<MeasuredFoodSnapshot>,
    )

    suspend fun transform(form: RecipeFormState, uiState: RecipeFormUiState): Result {
        require(form.isValid) { "Form is not valid" }
        requireNotNull(uiState.snapshots) { "Composition is not ready" }

        val language = foodNameSelector.observeLanguage().first()

        val nameStr = form.name.textFieldState.text.toString()
        val name =
            FoodName.requireAll(
                english = if (language == Language.English) nameStr else null,
                catalan = if (language == Language.Catalan) nameStr else null,
                czech = if (language == Language.Czech) nameStr else null,
                danish = if (language == Language.Danish) nameStr else null,
                german = if (language == Language.German) nameStr else null,
                spanish = if (language == Language.Spanish) nameStr else null,
                french = if (language == Language.French) nameStr else null,
                indonesian = if (language == Language.Indonesian) nameStr else null,
                italian = if (language == Language.Italian) nameStr else null,
                hungarian = if (language == Language.Hungarian) nameStr else null,
                dutch = if (language == Language.Dutch) nameStr else null,
                polish = if (language == Language.Polish) nameStr else null,
                portugueseBrazil = if (language == Language.PortugueseBrazil) nameStr else null,
                portuguesePortugal = if (language == Language.PortuguesePortugal) nameStr else null,
                slovenian = if (language == Language.Slovenian) nameStr else null,
                turkish = if (language == Language.Turkish) nameStr else null,
                russian = if (language == Language.Russian) nameStr else null,
                ukrainian = if (language == Language.Ukrainian) nameStr else null,
                arabic = if (language == Language.Arabic) nameStr else null,
                chineseSimplified = if (language == Language.ChineseSimplified) nameStr else null,
                fallback = nameStr,
            )

        val note = form.note.textFieldState.text.takeIf { it.isNotBlank() }?.toString()
        val servings = form.servings.textFieldState.text.toString().toDouble()

        return Result(
            name = name,
            note = note,
            imageBytes = form.imageUri.value?.let(::PlatformFile)?.readBytes(),
            servings = servings,
            components = uiState.snapshots,
        )
    }
}
