package campus.tech.kakao.map.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import campus.tech.kakao.map.model.Place
import campus.tech.kakao.map.model.RecentSearchWord

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

    fun getSearchHistory(): List<RecentSearchWord> {
        return searchHistoryData.value ?: emptyList()
    }

    fun moveSearchToLast(idx: Int, search: String) {
        repository.moveSearchToLast(idx, search)
        _searchHistoryData.value = repository.searchHistoryList
    }

    fun addSearch(search: String) {
        repository.addSearchHistory(search)
        _searchHistoryData.value = repository.searchHistoryList
    }

    fun delSearch(idx: Int) {
        repository.delSearchHistory(idx)
        _searchHistoryData.value = repository.searchHistoryList
    }
}