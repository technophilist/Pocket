package com.example.pocket

import kotlinx.coroutines.*
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun couroutineTest() {
        runBlocking {
            val scope = CoroutineScope(Dispatchers.IO)
            val job1 = scope.launch {
                delay(1000 * 2)
                println("first Coroutine")
            }
            job1.join()

            scope.launch {
                println("Second Coroutine")
            }



        }
    }

}