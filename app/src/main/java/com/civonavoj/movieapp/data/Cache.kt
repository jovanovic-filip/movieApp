package com.civonavoj.movieapp.data

import android.content.Context

class Cache {

    companion object {
        private const val SEARCH_HISTORY_KEY = "SEARCH_HISTORY"
        private const val SEARCH_VALUES_KEY = "RECENT_SEARCH_VALUES"
        private const val SEARCH_HISTORY_SIZE = 5
        private const val SEPARATOR = "###"

        fun clearCache(context: Context) {
            context.getSharedPreferences(SEARCH_HISTORY_KEY, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
        }

        private fun getSharedPrefs(context: Context) =
            context.getSharedPreferences(SEARCH_HISTORY_KEY, Context.MODE_PRIVATE)

        fun getSearchQueries(context: Context) =
            (getSharedPrefs(context).getString(SEARCH_VALUES_KEY,"") ?: "").split(SEPARATOR)

        fun saveSearchQuery(context: Context, query: String) {
            (getSearchQueries(context) + query).prepareForCache().let { dataToSave ->
                getSharedPrefs(context).edit().putString(SEARCH_VALUES_KEY, dataToSave).apply()
            }
        }

        private fun List<String>.prepareForCache() =
            takeLast(SEARCH_HISTORY_SIZE)
                .distinct()
                .filterNot { it.isEmpty() }
                .joinToString(SEPARATOR)
        }


}
