package com.lahsuak.apps.farmigo.network

import com.lahsuak.apps.farmigo.model.Farm2
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroInstance() {
    private val BASE_URL =
        "https://api.data.gov.in/resource/"
    private var apiService: APIService? = null

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(APIService::class.java)
    }

    companion object {
        lateinit var instance: RetroInstance
        fun getRetroInstance(): RetroInstance
        {
            instance = RetroInstance()
            return instance
        }
    }

    fun getMarket(): Call<Farm2>? {
        return apiService?.getMarket()
    }
}