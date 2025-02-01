package com.maksimowiczm.foodyou.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.feature.diary.data.DiaryPreferences
import com.maksimowiczm.foodyou.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.feature.diary.data.model.defaultGoals
import com.maksimowiczm.foodyou.feature.product.data.ProductPreferences
import com.maksimowiczm.foodyou.feature.system.data.SystemInfoRepository
import com.maksimowiczm.foodyou.feature.system.data.model.Country
import com.maksimowiczm.foodyou.infrastructure.datastore.get
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val systemInfoRepository: SystemInfoRepository
) : SettingsRepository {
    override fun observeOpenFoodFactsEnabled() = dataStore.observe(
        ProductPreferences.openFoodFactsEnabled
    ).map { it ?: false }

    override fun observeOpenFoodFactsCountry() = dataStore.observe(
        ProductPreferences.openFoodCountryCode
    ).map {
        systemInfoRepository.countries.find { country ->
            it?.compareTo(
                other = country.code,
                ignoreCase = true
            ) == 0
        }
    }

    override suspend fun enableOpenFoodFacts() {
        val country = dataStore.get(ProductPreferences.openFoodCountryCode)
            ?: systemInfoRepository.defaultCountry.code

        dataStore.set(
            ProductPreferences.openFoodFactsEnabled to true,
            ProductPreferences.openFoodCountryCode to country
        )
    }

    override suspend fun disableOpenFoodFacts() {
        dataStore.set(ProductPreferences.openFoodFactsEnabled to false)
    }

    override suspend fun setOpenFoodFactsCountry(country: Country) {
        dataStore.set(ProductPreferences.openFoodCountryCode to country.code)
    }

    override fun observeDailyGoals() = combine(
        dataStore.observe(DiaryPreferences.caloriesGoal),
        dataStore.observe(DiaryPreferences.proteinsGoal),
        dataStore.observe(DiaryPreferences.carbohydratesGoal),
        dataStore.observe(DiaryPreferences.fatsGoal)
    ) { calories, proteins, carbohydrates, fats ->
        if (calories == null || proteins == null || carbohydrates == null || fats == null) {
            defaultGoals()
        } else {
            DailyGoals(
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                mealCalorieGoalMap = emptyMap()
            )
        }
    }

    override suspend fun setDailyGoals(goals: DailyGoals) {
        dataStore.set(
            DiaryPreferences.caloriesGoal to goals.calories,
            DiaryPreferences.proteinsGoal to goals.proteins,
            DiaryPreferences.carbohydratesGoal to goals.carbohydrates,
            DiaryPreferences.fatsGoal to goals.fats
        )
    }

    override val defaultCountry: Country
        get() = systemInfoRepository.defaultCountry

    override val countries: List<Country>
        get() = systemInfoRepository.countries
}
