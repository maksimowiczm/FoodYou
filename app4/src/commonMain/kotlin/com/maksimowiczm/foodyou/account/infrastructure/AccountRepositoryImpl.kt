package com.maksimowiczm.foodyou.account.infrastructure

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.AccountSettings
import com.maksimowiczm.foodyou.account.domain.HomeCard
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountDao
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.FoodIdentityType
import com.maksimowiczm.foodyou.account.infrastructure.room.ProfileEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.ProfileFavoriteFoodEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.SettingsEntity
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.infrastructure.filekit.directory
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(private val accountDao: AccountDao) : AccountRepository {
    override fun observe(localAccountId: LocalAccountId): Flow<Account?> {
        return accountDao.observeRichAccount(localAccountId.value).map { accountEntity ->
            if (accountEntity == null) {
                null
            } else {
                Account.of(
                    localAccountId = LocalAccountId(accountEntity.account.id),
                    settings = accountEntity.settings.toDomain(),
                    profiles =
                        accountEntity.profiles.map { pe ->
                            val favoriteFoods =
                                accountEntity.favoriteFoods.filter { it.profileId == pe.id }
                            pe.toDomain(favoriteFoods)
                        },
                )
            }
        }
    }

    override suspend fun loadAll(): List<Account> {
        return accountDao
            .observeRichAccounts()
            .map { list ->
                list.map { accountEntity ->
                    Account.of(
                        localAccountId = LocalAccountId(accountEntity.account.id),
                        settings = accountEntity.settings.toDomain(),
                        profiles =
                            accountEntity.profiles.map { pe ->
                                val favoriteFoods =
                                    accountEntity.favoriteFoods.filter { it.profileId == pe.id }
                                pe.toDomain(favoriteFoods)
                            },
                    )
                }
            }
            .first()
    }

    override suspend fun save(account: Account) = coroutineScope {
        val accountEntity = AccountEntity(id = account.localAccountId.value)

        (account.directory() / "avatar")
            .apply { createDirectories() }
            .list()
            .forEach { file ->
                val profileId = file.nameWithoutExtension
                if (account.profiles.none { profile -> profile.id.value == profileId }) {
                    file.delete()
                }
            }

        val profileEntities =
            account.profiles.map { async { it.toEntity(account.localAccountId) } }.awaitAll()
        val settingsEntity = account.settings.toEntity(account.localAccountId.value)
        val profileFavoriteFoodEntities =
            account.profiles.flatMap { it.toFavoriteFoodEntity(account.localAccountId) }

        accountDao.upsertAccountWithDetails(
            accountEntity = accountEntity,
            profileEntities = profileEntities,
            profileFavoriteFoodEntities = profileFavoriteFoodEntities,
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

private fun ProfileEntity.toDomain(favoriteFoods: List<ProfileFavoriteFoodEntity>): Profile {
    val avatar =
        runCatching {
                when {
                    avatar.startsWith("photo:") -> {
                        val uri = avatar.removePrefix("photo:")
                        Profile.Avatar.Photo(uri = uri)
                    }

                    avatar.startsWith("predefined:") -> {
                        when (val name = avatar.removePrefix("predefined:")) {
                            "Person" -> Profile.Avatar.Predefined.Person
                            "Woman" -> Profile.Avatar.Predefined.Woman
                            "Man" -> Profile.Avatar.Predefined.Man
                            "Engineer" -> Profile.Avatar.Predefined.Engineer
                            else -> error("Unknown predefined avatar name: $name")
                        }
                    }

                    else -> error("Unknown avatar format: $avatar")
                }
            }
            .getOrElse { Profile.Avatar.Predefined.Person }

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
        avatar = avatar,
        homeCardsOrder = homeFeaturesOrder,
        favoriteFoods = favoriteFoods.map { it.toDomain(LocalAccountId(accountId)) },
    )
}

private fun ProfileFavoriteFoodEntity.toDomain(accountId: LocalAccountId): FoodProductIdentity =
    when (this.identityType) {
        //        FoodIdentityType.LocalProduct -> FoodProductIdentity.Local(extra, accountId)
        //        FoodIdentityType.OpenFoodFacts -> FoodProductIdentity.OpenFoodFacts(extra)
        FoodIdentityType.FoodDataCentral -> FoodProductIdentity.FoodDataCentral(extra.toInt())
    }

private suspend fun Profile.Avatar.toEntity(accountId: LocalAccountId, id: ProfileId): String =
    when (this) {
        is Profile.Avatar.Photo -> {
            val source = PlatformFile(uri)
            if (!source.exists()) {
                error("Avatar photo file does not exist at path: $uri")
            }
            val bytes = source.readBytes()

            val directory =
                (accountId.directory() / "avatar").apply {
                    if (!exists()) {
                        createDirectories()
                    }
                }

            val compressed =
                FileKit.compressImage(bytes = bytes, quality = 85, imageFormat = ImageFormat.JPEG)

            val dest = (directory / "${id.value}.jpg").apply { write(compressed) }

            "photo:${dest.path}"
        }

        is Profile.Avatar.Predefined -> {
            // Try to remove any existing photo file if switching to predefined avatar
            (accountId.directory() / "avatar").apply {
                if (!exists()) {
                    createDirectories()
                }
            }

            "predefined:$name"
        }
    }

private suspend fun Profile.toEntity(localAccountId: LocalAccountId): ProfileEntity =
    ProfileEntity(
        id = id.value,
        accountId = localAccountId.value,
        name = name,
        avatar = avatar.toEntity(localAccountId, id),
        homeFeaturesOrder = homeCardsOrder.joinToString(",") { it.ordinal.toString() },
    )

private fun Profile.toFavoriteFoodEntity(
    localAccountId: LocalAccountId
): List<ProfileFavoriteFoodEntity> =
    favoriteFoods.map { identity ->
        when (identity) {
            is FoodProductIdentity.FoodDataCentral ->
                ProfileFavoriteFoodEntity(
                    profileId = id.value,
                    accountId = localAccountId.value,
                    identityType = FoodIdentityType.FoodDataCentral,
                    extra = identity.fdcId.toString(),
                )

        //            is FoodProductIdentity.Local ->
        //                ProfileFavoriteFoodEntity(
        //                    profileId = id.value,
        //                    accountId = localAccountId.value,
        //                    identityType = FoodIdentityType.UserProduct,
        //                    extra = identity.id,
        //                )

        //            is FoodProductIdentity.OpenFoodFacts ->
        //                ProfileFavoriteFoodEntity(
        //                    profileId = id.value,
        //                    accountId = localAccountId.value,
        //                    identityType = FoodIdentityType.OpenFoodFacts,
        //                    extra = identity.barcode,
        //                )
        }
    }
