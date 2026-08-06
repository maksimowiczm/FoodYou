package com.maksimowiczm.foodyou.common.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LanguageTest {
    @Test
    fun should_match_exact_tag() {
        assertEquals(Language.PortuguesePortugal, Language.fromTag("pt-PT"))
        assertEquals(Language.PortugueseBrazil, Language.fromTag("pt-BR"))
        assertEquals(Language.English, Language.fromTag("en-US"))
    }

    @Test
    fun should_be_case_insensitive() {
        assertEquals(Language.PortuguesePortugal, Language.fromTag("PT-pt"))
        assertEquals(Language.English, Language.fromTag("EN-us"))
    }

    @Test
    fun should_handle_underscores() {
        assertEquals(Language.PortuguesePortugal, Language.fromTag("pt_PT"))
        assertEquals(Language.PortugueseBrazil, Language.fromTag("pt_BR"))
    }

    @Test
    fun should_fallback_to_language_only() {
        assertEquals(Language.English, Language.fromTag("en"))
        assertEquals(Language.English, Language.fromTag("en-GB"))
        assertEquals(Language.Polish, Language.fromTag("pl"))
        assertEquals(Language.Polish, Language.fromTag("pl-PL"))
    }

    @Test
    fun should_match_portuguese_correctly_with_variants() {
        // "pt" alone should match the first one found or a sensible default if we had one.
        // Current implementation returns the first one in `entries` that starts with "pt".
        // Language.PortugueseBrazil is before Language.PortuguesePortugal in the enum.
        assertEquals(Language.PortugueseBrazil, Language.fromTag("pt"))
    }

    @Test
    fun should_return_null_for_unknown_tag() {
        assertNull(Language.fromTag("xx"))
        assertNull(Language.fromTag("xx-YY"))
    }
}
