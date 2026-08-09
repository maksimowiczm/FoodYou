package com.maksimowiczm.foodyou.features

import com.maksimowiczm.foodyou.features.diary.diary
import com.maksimowiczm.foodyou.features.fooddatacentral.foodDataCentral
import com.maksimowiczm.foodyou.features.home.home
import com.maksimowiczm.foodyou.features.language.language
import com.maksimowiczm.foodyou.features.meal.meal
import com.maksimowiczm.foodyou.features.onboarding.onboarding
import com.maksimowiczm.foodyou.features.openfoodfacts.openFoodFacts
import com.maksimowiczm.foodyou.features.personalization.personalization
import com.maksimowiczm.foodyou.features.privacy.privacy
import com.maksimowiczm.foodyou.features.profile.profile
import com.maksimowiczm.foodyou.features.userproduct.userProduct
import com.maksimowiczm.foodyou.features.userrecipe.userRecipe
import org.koin.dsl.module

val featuresModule = module {
    diary()
    onboarding()
    home()
    language()
    meal()
    personalization()
    userProduct()
    userRecipe()
    foodDataCentral()
    openFoodFacts()
    privacy()
    profile()
}
