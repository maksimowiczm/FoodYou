package com.maksimowiczm.foodyou.account.infrastructure

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.AccountSettings
import com.maksimowiczm.foodyou.account.domain.HomeCard
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountDao
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.ProfileEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.SettingsEntity
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AccountRepositoryImpl(private val accountDao: AccountDao) : AccountRepository {
    override fun observe(localAccountId: LocalAccountId): Flow<Account?> {
        return combine(
            accountDao.observeAccount(localAccountId.value),
            accountDao.observeProfilesByAccountId(localAccountId.value),
            accountDao.observeSettingsByAccountId(localAccountId.value),
        ) { accountEntity, profileEntities, settingsEntity ->
            if (accountEntity == null) {
                null
            } else {
                Account.of(
                    localAccountId = LocalAccountId(accountEntity.id),
                    settings = settingsEntity?.toDomain() ?: AccountSettings.default,
                    profiles = profileEntities.map { it.toDomain() },
                )
            }
        }
    }

    override suspend fun save(account: Account) {
        val accountEntity = AccountEntity(id = account.localAccountId.value)
        val profileEntities = account.profiles.map { it.toEntity(account.localAccountId.value) }
        val settingsEntity = account.settings.toEntity(account.localAccountId.value)

        accountDao.upsertAccountWithDetails(
            accountEntity = accountEntity,
            profileEntities = profileEntities,
            settingsEntity = settingsEntity,
        )
    }
}

private fun SettingsEntity.toDomain(): AccountSettings {
    val nutrientsOrder =
        runCatching { this.nutrientsOrder.split(",").map { NutrientsOrder.entries[it.toInt()] } }
            .getOrElse { NutrientsOrder.defaultOrder }

    return AccountSettings(
        onboardingFinished = this.onboardingFinished,
        energyFormat = this.energyFormat,
        nutrientsOrder = nutrientsOrder,
    )
}

private fun AccountSettings.toEntity(accountId: String): SettingsEntity {
    val nutrientsOrder = this.nutrientsOrder.joinToString(",") { it.ordinal.toString() }

    return SettingsEntity(
        accountId = accountId,
        onboardingFinished = this.onboardingFinished,
        energyFormat = this.energyFormat,
        nutrientsOrder = nutrientsOrder,
    )
}

private fun ProfileEntity.toDomain(): Profile {
    val avatarName = this.avatar.removePrefix("predefined:")
    val avatar =
        try {
            Profile.Avatar.valueOf(avatarName)
        } catch (_: IllegalArgumentException) {
            Profile.Avatar.PERSON
        }
    val homeFeaturesOrder =
        this.homeFeaturesOrder.split(",").mapNotNull {
            try {
                HomeCard.entries[it.toInt()]
            } catch (_: IndexOutOfBoundsException) {
                null
            }
        }

    return Profile(
        id = ProfileId(this.id),
        name = this.name,
        avatar = avatar,
        homeCardsOrder = homeFeaturesOrder,
    )
}

private fun Profile.Avatar.toEntity(): String = "predefined:$name"

private fun Profile.toEntity(localAccountId: String): ProfileEntity {
    return ProfileEntity(
        id = this.id.value,
        localAccountId = localAccountId,
        name = this.name,
        avatar = this.avatar.toEntity(),
        homeFeaturesOrder = this.homeCardsOrder.joinToString(",") { it.ordinal.toString() },
    )
}
