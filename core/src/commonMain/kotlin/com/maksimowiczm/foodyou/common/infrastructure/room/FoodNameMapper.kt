package com.maksimowiczm.foodyou.common.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodName

object FoodNameMapper {
    fun toFoodNameEntity(name: FoodName) =
        FoodNameEntity(
            english = name.english,
            catalan = name.catalan,
            danish = name.danish,
            german = name.german,
            spanish = name.spanish,
            french = name.french,
            indonesian = name.indonesian,
            italian = name.italian,
            hungarian = name.hungarian,
            dutch = name.dutch,
            polish = name.polish,
            portugueseBrazil = name.portugueseBrazil,
            portuguesePortugal = name.portuguesePortugal,
            slovenian = name.slovenian,
            turkish = name.turkish,
            russian = name.russian,
            ukrainian = name.ukrainian,
            arabic = name.arabic,
            chineseSimplified = name.chineseSimplified,
            fallback = name.fallback,
        )

    fun toFoodName(name: FoodNameEntity) =
        FoodName.requireAll(
            english = name.english,
            catalan = name.catalan,
            czech = name.czech,
            danish = name.danish,
            german = name.german,
            spanish = name.spanish,
            french = name.french,
            indonesian = name.indonesian,
            italian = name.italian,
            hungarian = name.hungarian,
            dutch = name.dutch,
            polish = name.polish,
            portugueseBrazil = name.portugueseBrazil,
            portuguesePortugal = name.portuguesePortugal,
            slovenian = name.slovenian,
            turkish = name.turkish,
            russian = name.russian,
            ukrainian = name.ukrainian,
            arabic = name.arabic,
            chineseSimplified = name.chineseSimplified,
            fallback = name.fallback,
        )
}
