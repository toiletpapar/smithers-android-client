package com.example.budgeting_client

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.webkit.CookieManager
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
import com.example.budgeting_client.utils.CookieInterceptor
import com.example.budgeting_client.utils.initializeGson
import com.example.budgeting_client.utils.initializeHttpClient
import com.example.budgeting_client.utils.initializeRetrofit
import okhttp3.OkHttpClient
import retrofit2.Retrofit

// Declare data store
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Application dependencies
class SmithersApplication : Application() {
    // TODO: Replace domain here and in network-security-config
    private val host = "http://10.0.2.2:8080/"
    private val gson = initializeGson()

    var mangaRepository: MangaRepository? = null
    var userRepository: UserRepository? = null
    override fun onCreate() {
        super.onCreate()

        // Initialize networking tools
        val client: OkHttpClient = initializeHttpClient(CookieInterceptor(host, dataStore))
        val retrofit: Retrofit = initializeRetrofit(host, client, gson)
        val crawlerService = retrofit.create(MangaNetworkDataSource::class.java)
        val userService = retrofit.create(UserNetworkDataSource::class.java)

        // Initialize data layer
        this.mangaRepository = MangaRepository(crawlerService, gson)
        this.userRepository = UserRepository(userService, gson)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize cookieManager instance
        CookieManager.getInstance()

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