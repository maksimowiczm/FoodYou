package com.maksimowiczm.foodyou.core.domain.usecase

import com.maksimowiczm.foodyou.core.domain.source.SharedProductRemoteDataSource

fun interface ExtractProductLinkFromTextUseCase {
    operator fun invoke(text: String): String?
}

class ExtractProductLinkFromTextUseCaseImpl(
    private val remoteSources: List<SharedProductRemoteDataSource>
) : ExtractProductLinkFromTextUseCase {
    override fun invoke(text: String): String? = remoteSources
        .asSequence()
        .mapNotNull { it.extractUrl(text) }
        .firstOrNull()
}
