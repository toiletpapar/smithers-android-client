package com.example.budgeting_client.network

import com.example.budgeting_client.utils.gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofit: Retrofit = Retrofit.Builder()
    // TODO: Replace domain here and in network-security-config
    .baseUrl("http://10.0.2.2:8080/api/")
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()