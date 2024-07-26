package campus.tech.kakao.map.viewModel

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val retrofitService = Retrofit.Builder()
        .baseUrl("https://dapi.kakao.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitService::class.java)

}