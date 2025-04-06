package com.maksimowiczm.foodyou.core.ui.form

interface FormField<T, E> {
    val value: T
    val error: E?

    val dirty: Boolean
    val isValid: Boolean

    /**
     * Touch the field to mark it as dirty.
     */
    fun touch()

    fun onValueChange(newValue: String, touch: Boolean = true)
    fun onRawValueChange(newValue: T, touch: Boolean = true)
}
