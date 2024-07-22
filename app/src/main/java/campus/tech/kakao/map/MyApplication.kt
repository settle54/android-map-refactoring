package campus.tech.kakao.map

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val nativeKey = getString(R.string.kakao_api_key)
        KakaoMapSdk.init(this, nativeKey)
    }
}