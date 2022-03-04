package com.lahsuak.apps.farmigo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lahsuak.apps.farmigo.model.Farm2
import com.lahsuak.apps.farmigo.network.RetroInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FarmViewModel: ViewModel() {
    private var dataMutableLiveData: MutableLiveData<Farm2> = MutableLiveData<Farm2>()

    fun getMarket() : LiveData<Farm2> {
        val instance = RetroInstance.getRetroInstance()
        val call = instance.getMarket()
        call?.enqueue(object : Callback<Farm2> {
            override fun onResponse(
                call: Call<Farm2>,
                response: Response<Farm2>
            ) {
                dataMutableLiveData.value = response.body()
            }
            override fun onFailure(call: Call<Farm2>, t: Throwable) {
               //error
            }
        })
        return dataMutableLiveData
    }
}