package com.maksimowiczm.foodyou.core.input

import com.maksimowiczm.foodyou.core.input.dsl.fold
import com.maksimowiczm.foodyou.core.input.dsl.invoke

class Form<E>(val strategy: ValidationStrategy, vararg val rules: Rule<E>) {
    operator fun invoke(input: String): Input<E> = validate(input)

    fun validate(input: String): Input<E> = rules(input, strategy).fold(input)
}
