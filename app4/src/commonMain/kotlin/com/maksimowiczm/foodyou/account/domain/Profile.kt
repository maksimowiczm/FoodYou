package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.uuid.Uuid

class Profile(
    val id: ProfileId,
    name: String,
    avatar: Avatar,
    homeCardsOrder: List<HomeCard>,
    favoriteFoods: List<FavoriteFoodIdentity>,
) {
    companion object {
        fun new(name: String, avatar: Avatar): Profile {
            val id = Uuid.random()
            return Profile(
                id = ProfileId(id.toString()),
                name = name,
                avatar = avatar,
                homeCardsOrder = HomeCard.defaultOrder,
                favoriteFoods = listOf(),
            )
        }
    }

    var name: String = name
        private set

    var avatar: Avatar = avatar
        private set

    private val _homeCardsOrder = homeCardsOrder.toMutableList()
    val homeCardsOrder: List<HomeCard>
        get() = _homeCardsOrder.toList()

    private val _favoriteFoods = favoriteFoods.toMutableList()
    val favoriteFoods: List<FavoriteFoodIdentity>
        get() = _favoriteFoods

    fun updateHomeCardsOrder(newOrder: List<HomeCard>) {
        require(newOrder.containsAll(HomeCard.entries)) { "New order must contain all home cards" }

        _homeCardsOrder.clear()
        _homeCardsOrder.addAll(newOrder)
    }

    fun updateName(newName: String) {
        name = newName
    }

    fun updateAvatar(newAvatar: Avatar) {
        avatar = newAvatar
    }

    fun addFavoriteFood(food: FavoriteFoodIdentity) {
        _favoriteFoods.add(food)
    }

    fun removeFavoriteFood(food: FavoriteFoodIdentity) {
        _favoriteFoods.remove(food)
    }

    fun isFavorite(food: FavoriteFoodIdentity): Boolean = _favoriteFoods.contains(food)

    sealed interface Avatar {
        data class Photo(val uri: String) : Avatar

        sealed interface Predefined : Avatar {
            val name: String

            data object Person : Predefined {
                override val name: String = "Person"
            }

            data object Woman : Predefined {
                override val name: String = "Woman"
            }

            data object Man : Predefined {
                override val name: String = "Man"
            }

            data object Engineer : Predefined {
                override val name: String = "Engineer"
            }
        }
    }
}
