package com.example.flowlayout.gson

import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by kcq on 2020/6/1
 */
interface AppService {
    @GET("get_data.json")
    fun getAppData():Call<List<App>>
}
