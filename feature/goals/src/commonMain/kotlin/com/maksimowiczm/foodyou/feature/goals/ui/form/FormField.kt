package com.maksimowiczm.foodyou.feature.goals.ui.form

@Deprecated("dont do that")
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
