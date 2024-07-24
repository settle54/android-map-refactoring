package campus.tech.kakao.map.application

import android.app.Application
import campus.tech.kakao.map.R
import campus.tech.kakao.map.data.dao.PlaceDao
import campus.tech.kakao.map.data.database.PlacesDatabase
import campus.tech.kakao.map.data.repository.MapRepository
import campus.tech.kakao.map.ui.viewModel.MapViewModelFactory
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {

    lateinit var viewModelFactory: MapViewModelFactory

    override fun onCreate() {
        super.onCreate()
        val nativeKey = getString(R.string.kakao_api_key)
        KakaoMapSdk.init(this, nativeKey)

    }
}