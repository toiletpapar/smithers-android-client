package com.example.budgeting_client.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.budgeting_client.R

sealed class MainItem (
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    val route: String
) {
    // Manga
    object Manga: MainItem(R.drawable.book, R.string.manga_title, "manga/index")
    object MangaAdd: MainItem(R.drawable.book, R.string.manga_add_title, "manga/add")

    // Health
    object Health: MainItem(R.drawable.health, R.string.health_title, "health")

    // Cooking
    object Cooking: MainItem(R.drawable.recipes, R.string.cooking_title, "cooking")

    // Restaurants
    object Restaurants: MainItem(R.drawable.restaurant, R.string.restaurants_title, "restaurants")

    // Finance
    object Finance: MainItem(R.drawable.savings, R.string.finance_title, "finance")
}