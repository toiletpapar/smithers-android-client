package com.example.budgeting_client.ui.manga

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
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
                    navController.navigate("manga/-1")
                },
                onEditClick = { crawlTargetId: Int ->
                    navController.navigate("manga/$crawlTargetId")
                }
            )
        }

        val goToIndex = {
            navController.navigate(MainItem.Manga.route)
        }

        composable(
            "manga/{crawlTargetId}",
            arguments = listOf(navArgument("crawlTargetId") { type = NavType.IntType })
        ) {
            MangaUpsert(
                onClose = goToIndex,
                onSaveComplete = goToIndex,
                crawlTargetId = it.arguments?.getInt("crawlTargetId")
            )
        }
    }
}
