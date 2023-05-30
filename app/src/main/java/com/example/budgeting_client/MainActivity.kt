package com.example.budgeting_client

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.budgeting_client.data.crawler.CrawlerNetworkDataSource
import com.example.budgeting_client.data.crawler.MangaRepository
import com.example.budgeting_client.network.retrofit
import com.example.budgeting_client.ui.navigation.MainContextDrawer
import com.example.budgeting_client.ui.theme.BudgetingclientTheme


class SmithersApplication : Application() {
    private val crawlerService = retrofit.create(CrawlerNetworkDataSource::class.java)
    val mangaRepository = MangaRepository(crawlerService)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BudgetingclientTheme {
                MainContextDrawer()
            }
        }
    }
}

/**
 * TODO
 * On Crawler Add, navigate appropriately on success
 * Add LatestMangaUpdate GET requests to fully create MangaRepository
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