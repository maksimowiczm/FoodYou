@file:MustUseReturnValues

package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity

data class Account(val settings: AccountSettings = AccountSettings(), val profiles: List<Profile>) {
    init {
        require(profiles.isNotEmpty()) { "Account must have at least one profile" }
        require(profiles.distinctBy { it.id }.size == profiles.size) {
            "Account cannot have duplicate profile IDs"
        }
        require(profiles.all { it.homeCardsOrder.containsAll(HomeCard.entries) }) {
            "All profiles must contain all home cards in their order"
        }
    }
}

fun Account.updateProfile(id: ProfileId, transform: (Profile) -> Profile): Account =
    copy(profiles = profiles.map { if (it.id == id) transform(it) else it })

fun Account.removeFavoriteUserFood(identity: UserProductIdentity): Account {
    val id = FavoriteFoodIdentity.UserProduct(identity.id)
    return copy(
        profiles =
            profiles.map { profile -> profile.copy(favoriteFoods = profile.favoriteFoods - id) }
    )
}
