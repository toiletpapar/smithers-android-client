package com.example.budgeting_client.ui.manga

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.budgeting_client.ui.navigation.MainItem

fun NavGraphBuilder.mangaGraph(
    navController: NavController,
    onMainMenuOpen: () -> Unit,
) {
    navigation(startDestination = MainItem.Manga.route, route = "manga") {
        composable(MainItem.Manga.route) {
            MangaMain(
                onDrawerOpen = onMainMenuOpen,
                onAddClick = {
                    navController.navigate(MainItem.MangaAdd.route)
                }
            )
        }

        val goToIndex = {
            navController.navigate(MainItem.Manga.route)
        }

        composable(MainItem.MangaAdd.route) {
            MangaAdd(
                onClose = goToIndex,
                onSaveComplete = goToIndex
            )
        }
    }
}
