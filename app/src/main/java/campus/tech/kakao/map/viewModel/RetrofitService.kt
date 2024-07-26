package campus.tech.kakao.map.viewModel

import campus.tech.kakao.map.BuildConfig
import campus.tech.kakao.map.dto.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitService {

    @GET("v2/local/search/keyword.json")
    fun requestPlaces(
        @Header("Authorization") auth: String = KEY,
        @Query("query") query: String
    ): Call<SearchResponse>

    companion object {
        private const val KEY: String = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}"
    }
}