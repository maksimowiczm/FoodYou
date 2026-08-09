package com.maksimowiczm.foodyou

import com.maksimowiczm.foodyou.account.account
import com.maksimowiczm.foodyou.analytics.analytics
import com.maksimowiczm.foodyou.common.common
import com.maksimowiczm.foodyou.common.event.inMemoryEventBus
import com.maksimowiczm.foodyou.device.device
import com.maksimowiczm.foodyou.fooddatacentral.foodDataCentral
import com.maksimowiczm.foodyou.fooddiary.foodDiary
import com.maksimowiczm.foodyou.mealplan.mealPlan
import com.maksimowiczm.foodyou.openfoodfacts.openFoodFacts
import com.maksimowiczm.foodyou.search.search
import com.maksimowiczm.foodyou.userproduct.userProduct
import com.maksimowiczm.foodyou.userrecipe.userRecipe
import org.koin.dsl.module

val coreModule = module {
    account()
    analytics()
    common()
    inMemoryEventBus()
    device()
    foodDataCentral()
    foodDiary()
    mealPlan()
    openFoodFacts()
    search()
    userProduct()
    userRecipe()
}
