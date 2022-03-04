package com.lahsuak.apps.farmigo.network

import com.lahsuak.apps.farmigo.model.Farm2
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface APIService {
    @GET("189cab67-c7fa-4e58-995c-fb467434169d?api-key=579b464db66ec23bdd000001cdd3946e44ce4aad7209ff7b23ac571b&format=json&offset=0&limit=10")
    fun getMarket(): Call<Farm2>
}