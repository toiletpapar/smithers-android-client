package com.example.budgeting_client.ui.manga

import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.budgeting_client.models.CrawlerTypes
import com.example.budgeting_client.models.CreateCrawlerPayload
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
                    navController.navigate("manga/create")
                },
                onEditClick = { crawlTargetId: Int ->
                    navController.navigate("manga/update?crawlTargetId=$crawlTargetId")
                },
                onSearchClick = {
                    navController.navigate("manga/search")
                }
            )
        }

        val goToIndex = {
            navController.navigate(MainItem.Manga.route)
        }
        val navBackOrIndex = {
            if (!navController.popBackStack()) {
                goToIndex()
            }
        }

        composable("manga/search") {
            MangaSearch(
                onBack = navBackOrIndex,
                onSearchResultClick = { crawlTargetPayload ->
                    navController.navigate("manga/create?name=${Uri.encode(crawlTargetPayload.name)}&adapter=${crawlTargetPayload.adapter.value}&url=${Uri.encode(crawlTargetPayload.url)}")
                }
            )
        }

        composable(
            "manga/create?name={name}&adapter={adapter}&url={url}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType; defaultValue = "" },
                navArgument("adapter") { type = NavType.StringType; defaultValue = "webtoon" },
                navArgument("url") { type = NavType.StringType; defaultValue = "" }
            )
        ) { navBackStackEntry ->
            val crawler = CreateCrawlerPayload(
                name = navBackStackEntry.arguments?.getString("name").let{ Uri.decode(it) } ?: "",
                adapter = CrawlerTypes.values().find { value -> value.value == navBackStackEntry.arguments?.getString("adapter") } ?: CrawlerTypes.WEBTOON,
                url = navBackStackEntry.arguments?.getString("url").let{ Uri.decode(it) } ?: ""
            )

            Log.d("TEST", crawler.toString())

            MangaUpsert(
                onBack = navBackOrIndex,
                onSaveComplete = goToIndex,
                crawlTargetId = null,
                initialCrawler = crawler
            )
        }

        composable(
            "manga/update?crawlTargetId={crawlTargetId}",
            arguments = listOf(
                navArgument("crawlTargetId") { type = NavType.IntType; defaultValue = -1 }
            )
        ) {
            val crawlTargetId = it.arguments?.getInt("crawlTargetId")

            MangaUpsert(
                onBack = navBackOrIndex,
                onSaveComplete = goToIndex,
                crawlTargetId = crawlTargetId,
                initialCrawler = null
            )
        }
    }
}
