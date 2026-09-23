package com.maksimowiczm.foodyou.common

/**
 * Encapsulates the decision logic of an event-sourced aggregate, following the "decider" pattern: a
 * pure, side-effect-free description of how commands turn into events, and how events fold into
 * state.
 *
 * @param C the command type accepted by this decider
 * @param E the domain event type produced by this decider
 * @param S the state type derived by folding events
 * @property decide given the current state and an incoming command, returns the events that should
 *   be recorded.
 * @property evolve folds a single event into the state, producing the next state.
 * @property initialState the state used before any events have been applied, i.e. the state of a
 *   stream that does not exist yet.
 */
class Decider<C, E, S>(
    val decide: (C, S) -> List<E>,
    val evolve: (S, E) -> S,
    val initialState: S,
)
