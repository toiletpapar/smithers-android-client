package com.example.budgeting_client.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy

class DataStoreCookieJar(
    private val mainUrl: HttpUrl,
    private val dataStore: DataStore<Preferences>,
    private val cookieJar: JavaNetCookieJar
) : CookieJar {
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieJar.loadForRequest(url)
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        // Store in memory
        cookieJar.saveFromResponse(url, cookies)

        // Persist in storage only host cookies (i.e. the main cluster backing this application)
        // Also keeps old cookies around but shouldn't be a problem for the server
        // Also overwrites all cookies
        // Given that this server only sends/requires one cookie, this will be okay for now
        // TODO: JWT?
        if (mainUrl.host == url.host && mainUrl.port == url.port && mainUrl.isHttps == url.isHttps) {
            val cookieStrings = cookies.map { it.toString() }.joinToString(";")
            scope.launch {
                dataStore.edit { settings ->
                    settings[KEY_COOKIES] = cookieStrings
                }
            }
        }
    }

    companion object {
        private val KEY_COOKIES = stringPreferencesKey("cookies")
        private val scope = CoroutineScope(Dispatchers.IO)

        fun create(mainUrl: HttpUrl, dataStore: DataStore<Preferences>): DataStoreCookieJar {
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)  // TODO: accept original server
            CookieHandler.setDefault(cookieManager)

            // If loading a single line of text from a file takes too long, we can wait a bit before showing the main activity
            // Load cookies from DataStore and add them to the cookie store
            scope.launch {
                val preferences = dataStore.data.first()
                val cookieStrings = preferences[KEY_COOKIES]
                if (cookieStrings != null) {
                    val cookieList = cookieStrings.split(";")
                    val multimap = mapOf("Set-Cookie" to cookieList)
                    withContext(Dispatchers.IO) {
                        cookieManager.put(mainUrl.toUri(), multimap)
                    }
                }
            }

            val cookieJar = JavaNetCookieJar(cookieManager)

            return DataStoreCookieJar(mainUrl, dataStore, cookieJar)
        }
    }
}

fun initializeHttpClient(cookieJar: CookieJar): OkHttpClient {
    return OkHttpClient.Builder().cookieJar(cookieJar).build()
}

fun initializeRetrofit(url: HttpUrl, client: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
        .baseUrl(url.toString())
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}
