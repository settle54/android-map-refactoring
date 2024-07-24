package campus.tech.kakao.map.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import campus.tech.kakao.map.application.MyApplication
import campus.tech.kakao.map.databinding.ActivitySearchBinding
import campus.tech.kakao.map.data.model.Place
import campus.tech.kakao.map.ui.adapter.PlacesAdapter
import campus.tech.kakao.map.ui.adapter.SearchHistoryAdapter
import campus.tech.kakao.map.ui.viewModel.MapViewModel
import campus.tech.kakao.map.ui.viewModel.MapViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

    private val viewModel: MapViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpSearchHistoryAdapter()
        setUpPlacesAdapter()
        setUpViewModelObservers()

        binding.searchInput.addTextChangedListener { text ->
            viewModel.searchPlaces(text.toString())
//            viewModel.searchDBPlaces(text.toString())     //테스트용(검색)
//            viewModel.searchRoomPlaces(text.toString())   //테스트용(검색)
        }

        binding.deleteInput.setOnClickListener {
            binding.searchInput.text.clear()
        }
    }

    private fun setUpSearchHistoryAdapter() {
        searchHistoryAdapter = SearchHistoryAdapter(
            onDeleteClick = { position: Int ->
                viewModel.delSearch(position)
            },
            onTextClick = { position: Int ->
                val itemName = searchHistoryAdapter.getItemName(position)
                binding.searchInput.setText(itemName)
            })
        binding.searchHistory.adapter = searchHistoryAdapter
    }

    private fun setUpPlacesAdapter() {
        placesAdapter = PlacesAdapter { item: Place ->
            val itemName = item.name
            lifecycleScope.launch {
                viewModel.insertSearch(itemName)
                viewModel.savePos(item.longitude, item.latitude)
                Log.d("prefs", "lifecycle: ${Thread.currentThread().name}")
            }
            Log.d("prefs", "lifecycle2: ${Thread.currentThread().name}")
            goToSearch(item)
        }
        binding.placesRView.adapter = placesAdapter
        binding.placesRView.layoutManager = LinearLayoutManager(this)
    }

    private fun setUpViewModelObservers() {
        viewModel.places.observe(this, Observer { places ->
            placesAdapter.updateList(places)
            placesAdapter.notifyDataSetChanged()
            binding.textView.visibility =
                if (placesAdapter.itemCount <= 0) View.VISIBLE else View.GONE
        })
        
        viewModel.searchHistoryData.observe(this, Observer { searchHistoryData ->
            searchHistoryAdapter.submitList(searchHistoryData.toList())
            binding.searchHistory.isVisible = searchHistoryData.isNotEmpty()
        })
    }

    private fun goToSearch(place: Place) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("place", place)
        }
        Log.d("searchAct State", "Intent is: $intent")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
