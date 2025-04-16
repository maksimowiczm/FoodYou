package com.maksimowiczm.foodyou.core.input.dsl

import com.maksimowiczm.foodyou.core.input.Input
import com.maksimowiczm.foodyou.core.input.Rule
import com.maksimowiczm.foodyou.core.input.ValidationStrategy

inline infix fun <E> (() -> Boolean).checks(crossinline error: () -> E): Sequence<E> = sequence {
    if (!invoke()) yield(error())
}

inline infix fun <T, E> (() -> T).validates(crossinline validator: (T) -> E?): Sequence<E> =
    sequence {
        validator(invoke())?.let { yield(it) }
    }

operator fun <E> Sequence<Rule<E>>.invoke(input: String, strategy: ValidationStrategy): List<E> =
    when (strategy) {
        is ValidationStrategy.FailFast -> listOfNotNull(
            firstNotNullOfOrNull {
                it(input).firstOrNull()
            }
        )

        is ValidationStrategy.LazyEval -> flatMap { it(input) }.toList()
    }

operator fun <E> Array<out Rule<E>>.invoke(input: String, strategy: ValidationStrategy): List<E> =
    asSequence()(input, strategy)

fun <E> Iterable<E>.fold(value: String): Input<E> = asSequence().fold(value)

fun <E> Sequence<E>.fold(value: String): Input<E> =
    if (none()) input(value) else Input.Invalid<E>(value, toList())
