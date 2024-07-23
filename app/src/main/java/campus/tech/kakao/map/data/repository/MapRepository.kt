package campus.tech.kakao.map.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import campus.tech.kakao.map.data.dao.PlaceDao
import campus.tech.kakao.map.data.database.PlacesDBHelper
import campus.tech.kakao.map.data.database.PlacesDatabase
import campus.tech.kakao.map.data.model.DBPlace
import campus.tech.kakao.map.data.model.DBPlace.Companion.DATABASE_NAME
import campus.tech.kakao.map.data.network.api.RetrofitClient
import campus.tech.kakao.map.data.network.dto.SearchResponse
import campus.tech.kakao.map.data.model.Place
import campus.tech.kakao.map.data.model.RecentSearchWord
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapRepository(private val context: Context, private val placeDao: PlaceDao) :
    LocalDBRepoImpl {
    private val localDB: PlacesDBHelper = PlacesDBHelper(context)
    private val localRoom: PlacesDatabase = PlacesDatabase.getDatabase(context)

    private lateinit var prefs: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor
    var searchHistoryList = ArrayList<RecentSearchWord>()

    init {
        setPrefs()
    }


    /**
     * 카카오 REST API 관련
     */
    fun searchPlaces(search: String, onPlaceResponse: (List<Place>) -> Unit) {
        RetrofitClient.retrofitService.requestPlaces(query = search).enqueue(object :
            Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val responseList = mutableListOf<Place>()
                    body?.documents?.forEach {
                        val category = it.categoryName.split(" \u003e ").last()
                        responseList.add(Place(it.placeName, it.addressName, category, it.x, it.y))
                    }
                    onPlaceResponse(responseList)
                } else {
                    onPlaceResponse(emptyList())
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                println("error: $t")
                onPlaceResponse(emptyList())
            }
        })
    }

    private fun dbFileExists(dbName: String): Boolean {
        val dbFile = context.getDatabasePath("$dbName")
        return dbFile.exists()
    }


    /**
     * Local DB 관련 - Room
     */
    suspend fun insertRoomInitialData() {
            if (!dbFileExists(DATABASE_NAME)) {
                val places = mutableListOf<DBPlace>()
                for (i in 1..30) {
                    val cafe = DBPlace(name = "공원$i", address = "서울 성동구 성수동 $i", category = "카페")
                    val pharmacy =
                        DBPlace(name = "병원$i", address = "서울 강남구 대치동 $i", category = "약국")
                    places.add(cafe)
                    places.add(pharmacy)
                }
                localRoom.placeDao().insertAll(*places.toTypedArray())
            }
            Log.d("search2", "insetSearch: ${Thread.currentThread().name}")
    }

    private fun DBPlace.toPlace(): Place {
        return Place(
            name = this.name,
            address = this.address,
            category = this.category,
            longitude = "",
            latitude = ""
        )
    }

    suspend fun searchRoomPlaces(search: String, onPlaceResponse: (List<Place>) -> Unit) {
            val filtered: List<Place> = getAllPlaces()
                .filter { it.name.contains(search, ignoreCase = true) }
                .map { dbPlace -> dbPlace.toPlace() }
            Log.d("Thread", "${Thread.currentThread().name}")
            onPlaceResponse(filtered)
    }

    override suspend fun getAllPlaces(): List<DBPlace> = placeDao.getAllPlaces()

    override suspend fun insertAll(vararg dbPlace: DBPlace) = placeDao.insertAll(*dbPlace)

    override suspend fun delete(dbPlace: DBPlace) = placeDao.delete(dbPlace)


    /**
     * Local DB 관련 - DBHelper
     */
    fun insertLocalInitialData() {
            if (!dbFileExists(PlacesDBHelper.TABLE_NAME)) {
                val places = arrayListOf<Place>()
                for (i in 1..30) {
                    val cafe = Place("카페$i", "서울 성동구 성수동 $i", "카페")
                    val pharmacy = Place("약국$i", "서울 강남구 대치동 $i", "약국")
                    places.add(cafe)
                    places.add(pharmacy)
                }
                localDB.insertPlaces(places)
            }
            Log.d("search2", "insetSearch: ${Thread.currentThread().name}")
    }

    fun getAllLocalPlaces(): List<Place> {
        return localDB.getAllPlaces()
    }

    fun insertLocalPlace(name: String, address: String, category: String) {
        val place = Place(name, address, category)
        localDB.insertPlace(place)
    }

    fun deleteLocalPlace(name: String, address: String, category: String) {
        val place = Place(name, address, category)
        localDB.deletePlace(place)
    }

    fun searchDBPlaces(search: String, onPlaceResponse: (List<Place>) -> Unit) {
        val allPlaces = getAllLocalPlaces()
        val filtered = allPlaces.filter { it.name.contains(search, ignoreCase = true) }
        Log.d("Thread", "${Thread.currentThread().name}")   // main 스레드
        onPlaceResponse(filtered)
    }


    /**
     * SharedPreferences 관련
     */
    fun getSearchHistory(): ArrayList<RecentSearchWord> {
        return searchHistoryList
    }

    fun searchHistoryContains(itemName: String): Int {
        return searchHistoryList.indexOfFirst { it.word == itemName }
    }

    fun moveSearchToLast(idx: Int, search: String) {
        if (idx == searchHistoryList.size - 1) return
        searchHistoryList.removeAt(idx)
        searchHistoryList.add(RecentSearchWord(search))
        saveSearchHistory()
    }

    fun addSearchHistory(search: String) {
        searchHistoryList.add(RecentSearchWord(search))
        saveSearchHistory()
    }

    fun delSearchHistory(idx: Int) {
        searchHistoryList.removeAt(idx)
        saveSearchHistory()
    }

    private fun saveSearchHistory() {
        Log.d("prefs", "saveHistory: ${Thread.currentThread().name}")
        val stringPrefs = GsonBuilder().create().toJson(
            searchHistoryList, object : TypeToken<ArrayList<RecentSearchWord>>() {}.type
        )
        prefEditor.putString(SEARCH_HISTORY, stringPrefs)
        prefEditor.apply()
    }

    private fun setPrefs() {
        prefs = context.getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE)
        prefEditor = prefs.edit()
        val stringPrefs = prefs.getString(SEARCH_HISTORY, null)

        if (stringPrefs != null && stringPrefs != "[]") {
            searchHistoryList = GsonBuilder().create().fromJson(
                stringPrefs, object : TypeToken<ArrayList<RecentSearchWord>>() {}.type
            )
        }
    }

    fun getLastPos(): LatLng? {
        val stringPrefs = prefs.getString(LAST_POSITION, null)
        if (stringPrefs != null) {
            val lastPos: LatLng = GsonBuilder().create().fromJson(
                stringPrefs, object : TypeToken<LatLng>() {}.type
            )
            return lastPos
        }
        return null
    }

    fun savePos(latLng: LatLng) {
        Log.d("prefs", "savePos: ${Thread.currentThread().name}")
        val stringPrefs = GsonBuilder().create().toJson(
            latLng, object : TypeToken<LatLng>() {}.type
        )
        prefEditor.putString(LAST_POSITION, stringPrefs)
        prefEditor.apply()
    }

    companion object {
        private const val PREF_NAME = "app_data"
        private const val SEARCH_HISTORY = "search_history"
        private const val LAST_POSITION = "last_position"
    }

}