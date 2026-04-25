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
import com.maksimowiczm.foodyou.common.domain.ImageUri
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.infrastructure.filekit.FileKitBlobStorage
import io.github.vinceglb.filekit.PlatformFile
import kotlin.uuid.Uuid
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class AccountRepositoryImpl(
    private val accountDao: AccountDao,
    private val blobStorage: FileKitBlobStorage,
) : AccountRepository {
    override fun observe(): Flow<Account?> =
        combine(
            accountDao.observeProfiles(),
            accountDao.observeSettings(),
            accountDao.observeFavoriteFoods(),
        ) { profiles, settings, favoriteFoods ->
            if (settings == null || profiles.isEmpty()) null
            else
                Account(
                    settings = settings.toDomain(),
                    profiles =
                        profiles.map { pe ->
                            val favoriteFoods = favoriteFoods.filter { it.profileId == pe.id }
                            pe.toDomain(favoriteFoods)
                        },
                )
        }

    override suspend fun save(account: Account) = coroutineScope {
        val profileEntities =
            account.profiles.map { profile -> async { profile.toEntity(blobStorage) } }.awaitAll()
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
        energyUnit = this.energyUnit,
        nutrientsOrder = nutrientsOrder,
    )
}

private fun AccountSettings.toEntity(): SettingsEntity {
    val nutrientsOrder = this.nutrientsOrder.joinToString(",") { it.ordinal.toString() }

    return SettingsEntity(
        id = 1,
        onboardingFinished = this.onboardingFinished,
        energyUnit = this.energyUnit,
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
        favoriteFoods = favoriteFoods.map { it.toDomain() }.toSet(),
    )
}

private fun ProfileFavoriteFoodEntity.toDomain(): FavoriteFoodIdentity =
    when (this.identityType) {
        FoodIdentityType.UserProduct -> FavoriteFoodIdentity.UserProduct(Uuid.parse(extra))
        FoodIdentityType.OpenFoodFacts -> FavoriteFoodIdentity.OpenFoodFacts(extra)
        FoodIdentityType.FoodDataCentral -> FavoriteFoodIdentity.FoodDataCentral(extra.toInt())
    }

private fun String.toDomainAvatar(): Profile.Avatar =
    runCatching {
            when {
                startsWith("photo:") -> Profile.Avatar.Photo(ImageUri(removePrefix("photo:")))
                startsWith("predefined:") -> removePrefix("predefined:").avatar
                else -> error("Unknown avatar format: $this")
            }
        }
        .getOrElse { Profile.Avatar.Predefined.Person }

private fun Profile.Avatar.toEntityAvatar(): String =
    when (this) {
        is Profile.Avatar.Photo -> "photo:${uri.value}"
        is Profile.Avatar.Predefined -> "predefined:$name"
    }

private val Profile.Avatar.Predefined.name: String
    get() =
        when (this) {
            Profile.Avatar.Predefined.Engineer -> "engineer"
            Profile.Avatar.Predefined.Man -> "man"
            Profile.Avatar.Predefined.Person -> "person"
            Profile.Avatar.Predefined.Woman -> "woman"
        }
private val String.avatar: Profile.Avatar.Predefined
    get() =
        when (this) {
            "engineer" -> Profile.Avatar.Predefined.Engineer
            "man" -> Profile.Avatar.Predefined.Man
            "person" -> Profile.Avatar.Predefined.Person
            "woman" -> Profile.Avatar.Predefined.Woman
            else -> error("Unknown predefined avatar name: $this")
        }

private suspend fun Profile.toEntity(blobStorage: FileKitBlobStorage): ProfileEntity {
    val persistedAvatar =
        when (val avatar = avatar) {
            is Profile.Avatar.Photo -> {
                val digest = blobStorage.store(PlatformFile(avatar.uri.value))
                val path = blobStorage.uri(digest)
                Profile.Avatar.Photo(uri = path)
            }

            is Profile.Avatar.Predefined -> avatar
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
                    extra = identity.id.toString(),
                )

            is FavoriteFoodIdentity.OpenFoodFacts ->
                ProfileFavoriteFoodEntity(
                    profileId = id.value,
                    identityType = FoodIdentityType.OpenFoodFacts,
                    extra = identity.barcode,
                )
        }
    }
