package com.maksimowiczm.foodyou.app.ui

import com.maksimowiczm.foodyou.app.ui.changelog.changelog
import com.maksimowiczm.foodyou.app.ui.database.database
import com.maksimowiczm.foodyou.app.ui.food.diary.foodDiary
import com.maksimowiczm.foodyou.app.ui.food.food
import com.maksimowiczm.foodyou.app.ui.goals.goals
import com.maksimowiczm.foodyou.app.ui.home.home
import com.maksimowiczm.foodyou.app.ui.language.language
import com.maksimowiczm.foodyou.app.ui.meal.meal
import com.maksimowiczm.foodyou.app.ui.onboarding.onboarding
import com.maksimowiczm.foodyou.app.ui.personalization.personalization
import com.maksimowiczm.foodyou.app.ui.sponsor.sponsor
import com.maksimowiczm.foodyou.app.ui.theme.theme
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { AppViewModel(settingsRepository = userPreferencesRepository()) }

    changelog()
    database()
    food()
    foodDiary()
    goals()
    home()
    language()
    meal()
    onboarding()
    personalization()
    sponsor()
    theme()
}
