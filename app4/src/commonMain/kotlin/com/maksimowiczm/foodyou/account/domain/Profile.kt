package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.uuid.Uuid

class Profile(
    val id: ProfileId,
    val name: String,
    val avatar: Avatar,
    homeCardsOrder: List<HomeCard>,
) {
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

    private val _homeCardsOrder = homeCardsOrder.toMutableList()
    val homeCardsOrder: List<HomeCard>
        get() = _homeCardsOrder.toList()

    fun updateHomeCardsOrder(newOrder: List<HomeCard>) {
        if (!newOrder.containsAll(HomeCard.entries)) {
            error("New order must contain the same features as the current order")
        }

        _homeCardsOrder.clear()
        _homeCardsOrder.addAll(newOrder)
    }

    enum class Avatar {
        PERSON,
        WOMAN,
        MAN,
        ENGINEER,
    }
}
