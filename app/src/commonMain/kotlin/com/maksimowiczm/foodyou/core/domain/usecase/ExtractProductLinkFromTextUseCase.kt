package com.maksimowiczm.foodyou.core.domain.usecase

fun interface ExtractProductLinkFromTextUseCase {
    operator fun invoke(text: String): String?
}

class ExtractProductLinkFromTextUseCaseImpl : ExtractProductLinkFromTextUseCase {
    override fun invoke(text: String): String? = null
}
