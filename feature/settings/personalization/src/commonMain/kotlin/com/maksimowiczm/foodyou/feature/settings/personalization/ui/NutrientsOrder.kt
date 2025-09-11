package com.maksimowiczm.foodyou.feature.settings.personalization.ui

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.NutrientsOrder
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
