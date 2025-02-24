package com.civonavoj.movieapp

import android.content.Context
import android.content.SharedPreferences
import com.civonavoj.movieapp.data.Cache
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class CacheTest {
    private val context = mockk<Context>()
    private val sharedPrefs = mockk<SharedPreferences>()
    private val editor = mockk<SharedPreferences.Editor>()
    private val separator = "###"

    @Before
    fun setup() {
        every { context.getSharedPreferences("SEARCH_HISTORY", Context.MODE_PRIVATE) } returns sharedPrefs
        every { sharedPrefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } returns Unit
    }

    @Test
    fun `getSearchQueries returns empty list when no data`() {
        every { sharedPrefs.getString("RECENT_SEARCH_VALUES", "") } returns ""
        val result = Cache.getSearchQueries(context)
        assert(result.filterNot { it.isEmpty() }.isEmpty())
    }

    @Test
    fun `saveSearchQuery stores query and maintains history size`() {
        every { sharedPrefs.getString("RECENT_SEARCH_VALUES", "") } returns "query1${separator}query2${separator}query3"
        Cache.saveSearchQuery(context, "query4")
        verify { editor.putString("RECENT_SEARCH_VALUES", "query1${separator}query2${separator}query3${separator}query4") }
        verify { editor.apply() }
    }

    @Test
    fun `saveSearchQuery removes duplicates`() {
        every { sharedPrefs.getString("RECENT_SEARCH_VALUES", "") } returns "query1${separator}query2"
        Cache.saveSearchQuery(context, "query2")
        verify { editor.putString("RECENT_SEARCH_VALUES", "query1${separator}query2") }
        verify { editor.apply() }
    }
}
