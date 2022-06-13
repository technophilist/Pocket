package com.example.pocket.di

import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * An interface that defines the different types of dispatchers used
 * with a coroutine.
 * Use [StandardDispatchersProvider] to get an implementation of
 * [DispatchersProvider] that contains the default coroutine
 * dispatchers. By depending on an interface instead of the
 * dispatchers directly, it becomes possible to switch out all
 * dispatchers with test dispatchers when running tests.
 */
interface DispatchersProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

/**
 * A concrete implementation of [DispatchersProvider] that contains the
 * default coroutine dispatchers.
 */
data class StandardDispatchersProvider @Inject constructor(
    @MainCoroutineDispatcher override val main: CoroutineDispatcher,
    @IoCoroutineDispatcher override val io: CoroutineDispatcher,
    @DefaultCoroutineDispatcher override val default: CoroutineDispatcher,
    @UnconfinedCoroutineDispatcher override val unconfined: CoroutineDispatcher
) : DispatchersProvider