package com.maksimowiczm.foodyou.common.domain

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * A content-addressed identifier for a stored binary object (blob).
 *
 * The digest is typically a cryptographic hash (e.g. SHA-256) of the blob's content, ensuring that
 * equal digests always refer to equal content.
 *
 * @property digest The raw digest string representing the blob's content hash.
 */
@Serializable @JvmInline value class BlobDigest(val digest: String)
