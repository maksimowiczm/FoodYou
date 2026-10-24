package com.maksimowiczm.foodyou.app.infrastructure.crypto

import com.maksimowiczm.foodyou.common.unavailable

actual fun softwareCryptoImpl(): SoftwareCryptoImpl {
    unavailable("SoftwareCrypto unavailable on Android host platform")
}
