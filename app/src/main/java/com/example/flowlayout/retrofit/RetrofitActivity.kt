package com.example.flowlayout.retrofit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.flowlayout.R
import com.example.flowlayout.gson.App
import com.example.flowlayout.gson.AppService
import kotlinx.android.synthetic.main.activity_retrofit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitActivity : AppCompatActivity() {

    private val btnRequest by lazy { btn_request }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)
        btnRequest.setOnClickListener {
            val retrofit=Retrofit.Builder()
                    .baseUrl("http://192.XXX")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            val appService=retrofit.create(AppService::class.java)
            appService.getAppData().enqueue(object : Callback<List<App>> {
                override fun onResponse(call: Call<List<App>>, response: Response<List<App>>) {
                    val list = response.body()

                }

                override fun onFailure(call: Call<List<App>>, t: Throwable) {

                }
            })

        }
    }
}
