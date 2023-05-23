package com.example.budgeting_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.budgeting_client.navigation.MainContextDrawer
import com.example.budgeting_client.ui.theme.BudgetingclientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetingclientTheme {
                MainContextDrawer()
                applicationContext
            }
        }
    }
}