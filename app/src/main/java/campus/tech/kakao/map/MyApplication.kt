package campus.tech.kakao.map

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import campus.tech.kakao.map.viewModel.MapRepository
import campus.tech.kakao.map.viewModel.MapViewModel
import campus.tech.kakao.map.viewModel.MapViewModelFactory
import com.kakao.vectormap.KakaoMapSdk

class MyApplication: Application() {

    lateinit var viewModelFactory: MapViewModelFactory

    override fun onCreate() {
        super.onCreate()
        val nativeKey = getString(R.string.kakao_api_key)
        KakaoMapSdk.init(this, nativeKey)

        val repository = MapRepository(this)
        viewModelFactory = MapViewModelFactory(this, repository)
    }
}