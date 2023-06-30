package com.example.budgeting_client.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.budgeting_client.R
import com.example.budgeting_client.ui.finance.FinanceMain
import com.example.budgeting_client.ui.manga.mangaGraph
import com.example.budgeting_client.ui.user.UserLogin
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDrawer(
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory),
) {
    // State
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val onMainMenuOpen = fun() {
        scope.launch { drawerState.open() }
    }
    val items = listOf(
        MainItem.Manga,
        MainItem.Health,
        MainItem.Cooking,
        MainItem.Restaurants,
        MainItem.Finance
    )
    var selectedItem: MainItem? by remember { mutableStateOf(null) }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val user = userViewModel.uiState.user

    LaunchedEffect(user, currentRoute) {
        if (user !== null && currentRoute === "login") {
            selectedItem = MainItem.Manga
            navController.navigate(MainItem.Manga.route) {
                launchSingleTop = true
            }
        } else if (user === null && currentRoute !== "login") {
            selectedItem = null
            navController.navigate("login") {
                launchSingleTop = true
            }
        }
    }

    // Render
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                if (user !== null) {
                    Profile(user.username)
                    Divider()
                }
                Spacer(modifier = Modifier.height(16.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(ImageVector.vectorResource(id = item.image), contentDescription = stringResource(item.title)) },
                        label = { Text(stringResource(id = item.title)) },
                        selected = item == selectedItem,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            selectedItem = item
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
                NavigationDrawerItem(
                    icon = { Icon(ImageVector.vectorResource(id = R.drawable.logout), contentDescription = stringResource(R.string.logout)) },
                    label = { Text(stringResource(id = R.string.logout)) },
                    selected = selectedItem === null,
                    onClick = {
                        scope.launch { drawerState.close() }
                        userViewModel.logout()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
            }
        },
    ) {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                UserLogin(
                    onLoginClick = { authUser -> userViewModel.login(authUser) },
                    errors = userViewModel.uiState.errors
                )
            }
            mangaGraph(
                navController = navController,
                onMainMenuOpen = onMainMenuOpen,
            )
            composable(MainItem.Health.route) { FinanceMain(onMainMenuOpen) }
            composable(MainItem.Cooking.route) { FinanceMain(onMainMenuOpen) }
            composable(MainItem.Restaurants.route) { FinanceMain(onMainMenuOpen) }
            composable(MainItem.Finance.route) { FinanceMain(onMainMenuOpen) }
        }
    }
}