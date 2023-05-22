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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.budgeting_client.R
import com.example.budgeting_client.finance.financeContext
import com.example.budgeting_client.manga.mangaContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContextDrawer() {
    // State
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val onMainMenuOpen = fun() { scope.launch { drawerState.open() }}
    val financeComponent = financeContext(onMainMenuOpen)
    val items = listOf(
        ContextItem(ImageVector.vectorResource(id = R.drawable.book), "Manga", mangaContext(onMainMenuOpen)),
        ContextItem(ImageVector.vectorResource(id = R.drawable.health), "Health", financeComponent),
        ContextItem(ImageVector.vectorResource(id = R.drawable.recipes), "Cooking", financeComponent),
        ContextItem(ImageVector.vectorResource(id = R.drawable.restaurant), "Restaurants", financeComponent),
        ContextItem(ImageVector.vectorResource(id = R.drawable.savings), "Finance", financeComponent),
    )
    val selectedItem = remember { mutableStateOf(items[0]) }

    // Render
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.image, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = selectedItem.value.content
    )
}