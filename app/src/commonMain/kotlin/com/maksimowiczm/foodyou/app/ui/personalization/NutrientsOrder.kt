package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.settings.domain.entity.NutrientsOrder
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutrientsOrder.stringResource(): String =
    when (this) {
        NutrientsOrder.Proteins -> stringResource(Res.string.nutriment_proteins)
        NutrientsOrder.Fats -> stringResource(Res.string.nutriment_fats)
        NutrientsOrder.Carbohydrates -> stringResource(Res.string.nutriment_carbohydrates)
        NutrientsOrder.Other -> stringResource(Res.string.headline_other)
        NutrientsOrder.Vitamins -> stringResource(Res.string.headline_vitamins)
        NutrientsOrder.Minerals -> stringResource(Res.string.headline_minerals)
    }
