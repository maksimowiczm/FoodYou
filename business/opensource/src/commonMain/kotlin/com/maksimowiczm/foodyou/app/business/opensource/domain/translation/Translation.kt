package com.maksimowiczm.foodyou.app.business.opensource.domain.translation

data class Translation(
    val languageName: String,
    val languageTag: String,
    val authorsStrings: List<Author>,
    val isVerified: Boolean = false,
) {
    constructor(
        languageName: String,
        languageTag: String,
        isVerified: Boolean = false,
        vararg authors: Author,
    ) : this(
        languageName = languageName,
        languageTag = languageTag,
        authorsStrings = authors.toList(),
        isVerified = isVerified,
    )
}

data class Author(val name: String, val link: String? = null)
