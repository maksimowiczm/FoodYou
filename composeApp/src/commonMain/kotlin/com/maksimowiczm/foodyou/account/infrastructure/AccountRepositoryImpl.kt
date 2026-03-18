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
import com.maksimowiczm.foodyou.common.infrastructure.filekit.accountDirectory
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
import kotlinx.coroutines.flow.combine

internal class AccountRepositoryImpl(private val accountDao: AccountDao) : AccountRepository {
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
        (accountDirectory() / "avatar")
            .apply { createDirectories() }
            .list()
            .forEach { file ->
                val profileId = file.nameWithoutExtension
                if (account.profiles.none { profile -> profile.id.value == profileId }) {
                    file.delete()
                }
            }

        val profileEntities = account.profiles.map { async { it.toEntity() } }.awaitAll()
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
        favoriteFoods = favoriteFoods.map { it.toDomain() },
    )
}

private fun ProfileFavoriteFoodEntity.toDomain(): FavoriteFoodIdentity =
    when (this.identityType) {
        FoodIdentityType.UserProduct -> FavoriteFoodIdentity.UserProduct(extra)
        FoodIdentityType.OpenFoodFacts -> FavoriteFoodIdentity.OpenFoodFacts(extra)
        FoodIdentityType.FoodDataCentral -> FavoriteFoodIdentity.FoodDataCentral(extra.toInt())
    }

private suspend fun Profile.Avatar.toEntity(id: ProfileId): String =
    when (this) {
        is Profile.Avatar.Photo -> {
            val source = PlatformFile(uri)
            if (!source.exists()) {
                error("Avatar photo file does not exist at path: $uri")
            }
            val bytes = source.readBytes()

            val directory =
                (accountDirectory() / "avatar").apply {
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
            (accountDirectory() / "avatar" / "${id.value}.jpg").delete(mustExist = false)

            "predefined:$name"
        }
    }

private suspend fun Profile.toEntity(): ProfileEntity =
    ProfileEntity(
        id = id.value,
        name = name,
        avatar = avatar.toEntity(id),
        homeFeaturesOrder = homeCardsOrder.joinToString(",") { it.ordinal.toString() },
    )

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
