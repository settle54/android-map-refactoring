package campus.tech.kakao.map.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import campus.tech.kakao.map.data.model.RecentSearchWord
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSearchHistory(searchHistory: List<RecentSearchWord>) {
        Log.d("datastore", "saveSH: ${Thread.currentThread().name}")
        val json = GsonBuilder().create().toJson(searchHistory)
        dataStore.edit { preferences ->
            preferences[SEARCH_HISTORY_KEY] = json
        }
    }

    suspend fun getSearchHistory(): ArrayList<RecentSearchWord> {
        try {
            Log.d("datastore", "getSH: ${Thread.currentThread().name}")
            val preferences = dataStore.data.first()
            val json = preferences[SEARCH_HISTORY_KEY] ?: "[]"
            return GsonBuilder().create().fromJson(
                json, object : TypeToken<ArrayList<RecentSearchWord>>() {}.type)
        } catch (e: NoSuchElementException) {

        }
        return arrayListOf()
    }

    suspend fun savePos(latLng: LatLng) {
        Log.d("datastore", "savePos: ${Thread.currentThread().name}")
        val stringPrefs = GsonBuilder().create().toJson(latLng)
        dataStore.edit { preferences ->
            preferences[LAST_POSITION_KEY] = stringPrefs
        }
    }


    suspend fun getLastPos(): LatLng? {
        try {
            Log.d("datastore", "getLastPos: ${Thread.currentThread().name}")
            val preferences = dataStore.data.first()
            val json = preferences[LAST_POSITION_KEY] ?: return null
            return GsonBuilder().create().fromJson(json, LatLng::class.java)
        } catch (e: NoSuchElementException) {

        }
        return null
    }

    companion object {
        private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
        private val LAST_POSITION_KEY = stringPreferencesKey("last_position")
    }

}