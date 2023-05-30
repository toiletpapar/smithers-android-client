package com.example.budgeting_client.ui.manga

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.budgeting_client.ui.navigation.ContextItem

fun NavGraphBuilder.mangaGraph(
    navController: NavController,
    onMainMenuOpen: () -> Unit,
) {

    navigation(startDestination = ContextItem.Manga.route, route = "manga") {
        composable(ContextItem.Manga.route) {
            MangaContext(
                onDrawerOpen = onMainMenuOpen,
                onAddClick = {
                    navController.navigate(ContextItem.MangaAdd.route)
                }
            )
        }

        val goToIndex = {
            navController.navigate(ContextItem.Manga.route)
        }

        composable(ContextItem.MangaAdd.route) {
            MangaAdd(
                onClose = goToIndex,
                onSaveComplete = goToIndex
            )
        }
    }
}
