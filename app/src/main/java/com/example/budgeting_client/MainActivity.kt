package com.example.budgeting_client

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.budgeting_client.repositories.MangaNetworkDataSource
import com.example.budgeting_client.repositories.MangaRepository
import com.example.budgeting_client.utils.retrofit
import com.example.budgeting_client.ui.navigation.MainDrawer
import com.example.budgeting_client.ui.theme.BudgetingclientTheme


class SmithersApplication : Application() {
    private val crawlerService = retrofit.create(MangaNetworkDataSource::class.java)
    val mangaRepository = MangaRepository(crawlerService)
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