package campus.tech.kakao.map.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import campus.tech.kakao.map.model.Place
import campus.tech.kakao.map.model.RecentSearchWord
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlacesViewModel(private val repository: MapRepository) : ViewModel() {

    private val _places: MutableLiveData<List<Place>> = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    private val _searchHistoryData: MutableLiveData<ArrayList<RecentSearchWord>> =
        MutableLiveData<ArrayList<RecentSearchWord>>()
    val searchHistoryData: LiveData<ArrayList<RecentSearchWord>> = _searchHistoryData

    init {
        _searchHistoryData.value = repository.searchHistoryList
    }

    fun searchPlaces(search: String) {
        if (search.isEmpty()) {
            _places.value = mutableListOf()
            return
        }
        repository.searchPlaces(search) { placeList ->
            _places.value = placeList
        }
    }

    fun searchDBPlaces(search: String) {
        if (search.isEmpty()) {
            _places.value = mutableListOf()
            return
        }
        repository.searchDBPlaces(search) { placeList ->
            _places.value = placeList
        }
    }

    fun savePos(longitude: String, latitude: String) {
        viewModelScope.launch() {
            withContext(Dispatchers.IO) {
                Log.d("prefs", "savePos: ${Thread.currentThread().name}")
                repository.savePos(LatLng.from(latitude.toDouble(), longitude.toDouble()))
            }
        }
    }

    fun getSearchHistory(): List<RecentSearchWord> {
        return searchHistoryData.value ?: emptyList()
    }

    fun insertSearch(search: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.d("prefs", "insetSearch: ${Thread.currentThread().name}")
                val foundIdx = repository.searchHistoryContains(search)
                if (foundIdx != -1) {
                    repository.moveSearchToLast(foundIdx, search)
                } else {
                    repository.addSearchHistory(search)
                }
            }
            _searchHistoryData.value = repository.searchHistoryList
        }
    }

    fun delSearch(idx: Int) {
        repository.delSearchHistory(idx)
        _searchHistoryData.value = repository.searchHistoryList
    }
}