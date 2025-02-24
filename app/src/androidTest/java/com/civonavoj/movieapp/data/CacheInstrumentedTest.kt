package com.civonavoj.movieapp

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.civonavoj.movieapp.data.Cache
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CacheInstrumentedTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Before
    fun setup() {
        Cache.clearCache(context)
    }

    @Test
    fun getSearchQueries_whenEmpty_returnsEmptyList() {
        val queries = Cache.getSearchQueries(context).filterNot { it.isEmpty() }
        assertEquals(emptyList<String>(), queries)
    }

    @Test
    fun saveSearchQuery_maintainsHistorySize() {
        repeat(6) { index ->
            Cache.saveSearchQuery(context, "query$index")
        }

        val queries = Cache.getSearchQueries(context).filterNot { it.isEmpty() }
        assertEquals(5, queries.size)
        assertEquals("query5", queries.last())
    }

    @Test
    fun saveSearchQuery_removeDuplicates() {
        Cache.saveSearchQuery(context, "test")
        Cache.saveSearchQuery(context, "unique")
        Cache.saveSearchQuery(context, "test")

        val queries = Cache.getSearchQueries(context).filterNot { it.isEmpty() }
        assertEquals(2, queries.size)
        assertEquals(listOf("test", "unique"), queries) // test is most recent
    }
}
