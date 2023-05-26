package com.example.budgeting_client.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgeting_client.finance.FinanceContext
import com.example.budgeting_client.manga.mangaGraph
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContextDrawer() {
    // State
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val onMainMenuOpen = fun() {
        scope.launch { drawerState.open() }
    }
    val items = listOf(
        ContextItem.Manga,
        ContextItem.Health,
        ContextItem.Cooking,
        ContextItem.Restaurants,
        ContextItem.Finance
    )
    val selectedItem = remember { mutableStateOf(items[0]) }

    val navController = rememberNavController()

    // Render
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(ImageVector.vectorResource(id = item.image), contentDescription = stringResource(item.title)) },
                        label = { Text(stringResource(id = item.title)) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            selectedItem.value = item
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
            }
        },
    ) {
        NavHost(navController = navController, startDestination = "manga") {
            mangaGraph(navController, onMainMenuOpen)
            composable(ContextItem.Health.route) { FinanceContext(onMainMenuOpen) }
            composable(ContextItem.Cooking.route) { FinanceContext(onMainMenuOpen) }
            composable(ContextItem.Restaurants.route) { FinanceContext(onMainMenuOpen) }
            composable(ContextItem.Finance.route) { FinanceContext(onMainMenuOpen) }
        }
    }
}