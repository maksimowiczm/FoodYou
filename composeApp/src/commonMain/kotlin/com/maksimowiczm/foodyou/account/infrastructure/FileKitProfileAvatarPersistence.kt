package com.maksimowiczm.foodyou.account.infrastructure

import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.infrastructure.filekit.accountDirectory
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write

/**
 * FileKit-backed avatar persistence implementation.
 *
 * Behavior:
 * - Stores photo avatars as JPEG files in `<accountDirectory>/avatar/<profileId>.jpg`
 * - Compresses photos with quality `85`
 * - Deletes existing photo file when requested through [delete]
 */
internal class FileKitProfileAvatarPersistence {

    /**
     * Persists a photo avatar for profile [id] and returns the persisted photo avatar.
     *
     * Implementations can perform file I/O and may throw when avatar input is invalid (for example,
     * missing source file).
     */
    suspend fun save(id: ProfileId, avatar: Profile.Avatar.Photo): Profile.Avatar.Photo {
        val bytes = PlatformFile(avatar.uri).readBytes()

        val directory = (accountDirectory() / "avatar")
        directory.createDirectories()

        val compressed =
            FileKit.compressImage(bytes = bytes, quality = 85, imageFormat = ImageFormat.JPEG)

        val dest = (directory / "${id.value}.jpg").apply { write(compressed) }

        return Profile.Avatar.Photo(uri = dest.path)
    }

    /** Removes any persisted avatar file for [id]. */
    suspend fun delete(id: ProfileId) {
        (accountDirectory() / "avatar" / "${id.value}.jpg").delete(mustExist = false)
    }

    /** Removes all persisted avatar files except for those with [ids]. */
    suspend fun deleteAllBut(ids: List<ProfileId>) {
        val directory = (accountDirectory() / "avatar")
        directory.createDirectories()
        directory.list().forEach { file ->
            if (file.nameWithoutExtension !in ids.map { it.value }) {
                file.delete()
            }
        }
    }
}
