package com.lahsuak.apps.farmigo.model

import com.google.gson.annotations.SerializedName

data class Farm2(
    @SerializedName("records") val records: List<Data>
)

