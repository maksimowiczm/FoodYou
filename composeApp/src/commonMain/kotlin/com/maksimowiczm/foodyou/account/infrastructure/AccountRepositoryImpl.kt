package com.maksimowiczm.foodyou.account.infrastructure

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.AccountSettings
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.account.domain.HomeCard
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountDao
import com.maksimowiczm.foodyou.account.infrastructure.room.FoodIdentityType
import com.maksimowiczm.foodyou.account.infrastructure.room.ProfileEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.ProfileFavoriteFoodEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.SettingsEntity
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class AccountRepositoryImpl(private val accountDao: AccountDao) : AccountRepository {
    private val profileAvatarPersistence = FileKitProfileAvatarPersistence()

    override fun observe(): Flow<Account?> =
        combine(
            accountDao.observeProfiles(),
            accountDao.observeSettings(),
            accountDao.observeFavoriteFoods(),
        ) { profiles, settings, favoriteFoods ->
            if (settings == null || profiles.isEmpty()) null
            else
                Account.of(
                    settings = settings.toDomain(),
                    profiles =
                        profiles.map { pe ->
                            val favoriteFoods = favoriteFoods.filter { it.profileId == pe.id }
                            pe.toDomain(favoriteFoods)
                        },
                )
        }

    override suspend fun save(account: Account) = coroutineScope {
        profileAvatarPersistence.deleteAllBut(account.profiles.map { it.id })

        val profileEntities =
            account.profiles
                .map { profile -> async { profile.toEntity(profileAvatarPersistence) } }
                .awaitAll()
        val settingsEntity = account.settings.toEntity()
        val profileFavoriteFoodEntities = account.profiles.flatMap { it.toFavoriteFoodEntity() }

        accountDao.upsertAccountWithDetails(
            profiles = profileEntities,
            favoriteFoods = profileFavoriteFoodEntities,
            settings = settingsEntity,
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

private fun AccountSettings.toEntity(): SettingsEntity {
    val nutrientsOrder = this.nutrientsOrder.joinToString(",") { it.ordinal.toString() }

    return SettingsEntity(
        id = 1,
        onboardingFinished = this.onboardingFinished,
        energyFormat = this.energyFormat,
        nutrientsOrder = nutrientsOrder,
    )
}

private fun ProfileEntity.toDomain(favoriteFoods: List<ProfileFavoriteFoodEntity>): Profile {
    val homeFeaturesOrder =
        homeFeaturesOrder.split(",").mapNotNull {
            try {
                HomeCard.entries[it.toInt()]
            } catch (_: IndexOutOfBoundsException) {
                null
            }
        }

    return Profile(
        id = ProfileId(id),
        name = name,
        avatar = avatar.toDomainAvatar(),
        homeCardsOrder = homeFeaturesOrder,
        favoriteFoods = favoriteFoods.map { it.toDomain() },
    )
}

private fun ProfileFavoriteFoodEntity.toDomain(): FavoriteFoodIdentity =
    when (this.identityType) {
        FoodIdentityType.UserProduct -> FavoriteFoodIdentity.UserProduct(extra)
        FoodIdentityType.OpenFoodFacts -> FavoriteFoodIdentity.OpenFoodFacts(extra)
        FoodIdentityType.FoodDataCentral -> FavoriteFoodIdentity.FoodDataCentral(extra.toInt())
    }

private fun String.toDomainAvatar(): Profile.Avatar =
    runCatching {
            when {
                startsWith("photo:") -> {
                    val uri = removePrefix("photo:")
                    Profile.Avatar.Photo(uri = uri)
                }

                startsWith("predefined:") -> {
                    when (val name = removePrefix("predefined:")) {
                        "Person" -> Profile.Avatar.Predefined.Person
                        "Woman" -> Profile.Avatar.Predefined.Woman
                        "Man" -> Profile.Avatar.Predefined.Man
                        "Engineer" -> Profile.Avatar.Predefined.Engineer
                        else -> error("Unknown predefined avatar name: $name")
                    }
                }

                else -> error("Unknown avatar format: $this")
            }
        }
        .getOrElse { Profile.Avatar.Predefined.Person }

private fun Profile.Avatar.toEntityAvatar(): String =
    when (this) {
        is Profile.Avatar.Photo -> "photo:$uri"
        is Profile.Avatar.Predefined -> "predefined:$name"
    }

private suspend fun Profile.toEntity(
    profileAvatarPersistence: FileKitProfileAvatarPersistence
): ProfileEntity {
    val persistedAvatar =
        when (val avatar = avatar) {
            is Profile.Avatar.Photo -> profileAvatarPersistence.save(id, avatar)
            is Profile.Avatar.Predefined -> {
                profileAvatarPersistence.delete(id)
                avatar
            }
        }

    return ProfileEntity(
        id = id.value,
        name = name,
        avatar = persistedAvatar.toEntityAvatar(),
        homeFeaturesOrder = homeCardsOrder.joinToString(",") { it.ordinal.toString() },
    )
}

private fun Profile.toFavoriteFoodEntity(): List<ProfileFavoriteFoodEntity> =
    favoriteFoods.map { identity ->
        when (identity) {
            is FavoriteFoodIdentity.FoodDataCentral ->
                ProfileFavoriteFoodEntity(
                    profileId = id.value,
                    identityType = FoodIdentityType.FoodDataCentral,
                    extra = identity.fdcId.toString(),
                )

            is FavoriteFoodIdentity.UserProduct ->
                ProfileFavoriteFoodEntity(
                    profileId = id.value,
                    identityType = FoodIdentityType.UserProduct,
                    extra = identity.id,
                )

            is FavoriteFoodIdentity.OpenFoodFacts ->
                ProfileFavoriteFoodEntity(
                    profileId = id.value,
                    identityType = FoodIdentityType.OpenFoodFacts,
                    extra = identity.barcode,
                )
        }
    }
