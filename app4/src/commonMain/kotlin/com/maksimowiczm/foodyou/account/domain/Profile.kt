package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.uuid.Uuid

class Profile(val id: ProfileId, name: String, avatar: Avatar, homeCardsOrder: List<HomeCard>) {
    companion object {
        fun new(name: String, avatar: Avatar): Profile {
            val id = Uuid.random()
            return Profile(
                id = ProfileId(id.toString()),
                name = name,
                avatar = avatar,
                homeCardsOrder = HomeCard.defaultOrder,
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

    enum class Avatar {
        PERSON,
        WOMAN,
        MAN,
        ENGINEER,
    }
}
