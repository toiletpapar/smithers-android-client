package com.example.budgeting_client.utils

import android.webkit.CookieManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CookieInterceptor(private val host: String, private val dataStore: DataStore<Preferences>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Add stored cookies to the request headers
        val cookieManager = CookieManager.getInstance()
        val storedCookies = cookieManager.getCookie(host)
        if (storedCookies != null && storedCookies.isNotEmpty()) {
            requestBuilder.addHeader("Cookie", storedCookies)
        }

        // Proceed with the request and receive the response
        val response = chain.proceed(requestBuilder.build())

        // Check if the response contains any Set-Cookie headers
        val cookieHeaders = response.headers("Set-Cookie")

        // Process and store the received cookies
        if (cookieHeaders.isNotEmpty()) {
            cookieManager.setCookie(host, cookieHeaders.joinToString(separator = "; "))
        }

        return response
    }
}
fun initializeHttpClient(interceptor: Interceptor): OkHttpClient {
    return OkHttpClient.Builder().addInterceptor(interceptor).build()
}
fun initializeRetrofit(host: String, client: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
        .baseUrl(host)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}
