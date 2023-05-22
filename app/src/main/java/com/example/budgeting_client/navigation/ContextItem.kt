package com.example.budgeting_client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

class ContextItem (
    val image: ImageVector,
    val title: String,
    val content: @Composable () -> Unit
) {
}