package com.example.budgeting_client

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.budgeting_client.repositories.MangaNetworkDataSource
import com.example.budgeting_client.repositories.MangaRepository
import com.example.budgeting_client.repositories.UserNetworkDataSource
import com.example.budgeting_client.repositories.UserRepository
import com.example.budgeting_client.ui.navigation.MainDrawer
import com.example.budgeting_client.ui.theme.BudgetingclientTheme
import com.example.budgeting_client.utils.DataStoreCookieJar
import com.example.budgeting_client.utils.initializeGson
import com.example.budgeting_client.utils.initializeHttpClient
import com.example.budgeting_client.utils.initializeRetrofit
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit

// TODO: Add ability to import all crawlers through bookmark bar (adding one at a time is a pain)
// TODO: Pull the most "popular" mangadex and webtoon comics, allow users to select from those instead of creating the crawlers manually
// TODO: Add "Favourites" which are always filtered to the top
// TODO: Add "Mark as Read" for Cards
// TODO: Memoization for efficient recompose
// Declare data store
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Application dependencies
class SmithersApplication : Application() {
    private val url = "http://10.0.2.2:8080/".toHttpUrlOrNull()!!       // Local
//    private val url = "http://192.168.0.24:8080/".toHttpUrlOrNull()!!   // LAN
//    private val url = "https://tylerpoon.ca".toHttpUrlOrNull()!!          // Production
    private val gson = initializeGson()

    var mangaRepository: MangaRepository? = null
    var userRepository: UserRepository? = null
    override fun onCreate() {
        super.onCreate()

        val cookieJar = DataStoreCookieJar.create(url, dataStore)

        // Initialize networking tools
        val client: OkHttpClient = initializeHttpClient(cookieJar)
        val retrofit: Retrofit = initializeRetrofit(url, client, gson)
        val crawlerService = retrofit.create(MangaNetworkDataSource::class.java)
        val userService = retrofit.create(UserNetworkDataSource::class.java)

        // Initialize data layer
        this.mangaRepository = MangaRepository(crawlerService, gson)
        this.userRepository = UserRepository(userService, gson)
    }

    // TODO: companion object?
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BudgetingclientTheme {
                MainDrawer()
            }
        }
    }
}

/**
 * TODO
 * Connect to the server for application functionality:
 * * User-initiated Sync
 * * Edit
 * * Delete
 * Using WorkManager deferrables for automated syncing from crawler server
 * Notifications
 *
 * Internationalization
 * Responsive Design Testing
 * Unit Testing
 */