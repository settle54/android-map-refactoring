package campus.tech.kakao.map.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import campus.tech.kakao.map.data.dao.PlaceDao
import campus.tech.kakao.map.data.database.PlacesDBHelper
import campus.tech.kakao.map.data.database.PlacesRoomDB
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRepository @Inject constructor(
    private val context: Context,
    private val dataStoreManager: DataStoreManager,
    private val placeDao: PlaceDao,
    private val localSql: PlacesDBHelper,
    private val localRoom: PlacesRoomDB
) : LocalDBRepoImpl {

    var searchHistoryList = ArrayList<RecentSearchWord>()


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
            localSql.insertPlaces(places)
        }
        Log.d("search2", "insetSearch: ${Thread.currentThread().name}")
    }

    fun getAllLocalPlaces(): List<Place> {
        return localSql.getAllPlaces()
    }

    fun insertLocalPlace(name: String, address: String, category: String) {
        val place = Place(name, address, category)
        localSql.insertPlace(place)
    }

    fun deleteLocalPlace(name: String, address: String, category: String) {
        val place = Place(name, address, category)
        localSql.deletePlace(place)
    }

    fun searchDBPlaces(search: String, onPlaceResponse: (List<Place>) -> Unit) {
        val allPlaces = getAllLocalPlaces()
        val filtered = allPlaces.filter { it.name.contains(search, ignoreCase = true) }
        Log.d("Thread", "${Thread.currentThread().name}")   // main 스레드
        onPlaceResponse(filtered)
    }


    /**
     * datastore 관련
     */
    fun getSearchHistory(): ArrayList<RecentSearchWord> {
        return searchHistoryList
    }

    fun searchHistoryContains(itemName: String): Int {
        return searchHistoryList.indexOfFirst { it.word == itemName }
    }

    suspend fun moveSearchToLast(idx: Int, search: String) {
        if (idx == searchHistoryList.size - 1) return
        searchHistoryList.removeAt(idx)
        searchHistoryList.add(RecentSearchWord(search))
        saveSearchHistory()
    }

    suspend fun addSearchHistory(search: String) {
        withContext(Dispatchers.IO) {
            searchHistoryList.add(RecentSearchWord(search))
            saveSearchHistory()
        }
    }

    suspend fun delSearchHistory(idx: Int) {
        withContext(Dispatchers.IO) {
            searchHistoryList.removeAt(idx)
            saveSearchHistory()
        }
    }

    private suspend fun saveSearchHistory() {
        dataStoreManager.saveSearchHistory(searchHistoryList)
    }

    suspend fun setPrefs() {
        searchHistoryList = dataStoreManager.getSearchHistory()
    }

    suspend fun getLastPos(): LatLng? {
        return dataStoreManager.getLastPos()
    }

    suspend fun savePos(latLng: LatLng) {
        return dataStoreManager.savePos(latLng)
    }

}