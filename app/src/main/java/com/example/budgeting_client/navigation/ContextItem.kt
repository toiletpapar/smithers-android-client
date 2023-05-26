package com.example.budgeting_client.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.budgeting_client.R

sealed class ContextItem (
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    val route: String,
) {
    // Manga
    object Manga: ContextItem(R.drawable.book, R.string.manga_title, "manga/index")
    object MangaAdd: ContextItem(R.drawable.book, R.string.manga_add_title, "manga/add")

    // Health
    object Health: ContextItem(R.drawable.health, R.string.health_title, "health")

    // Cooking
    object Cooking: ContextItem(R.drawable.recipes, R.string.cooking_title, "cooking")

    // Restaurants
    object Restaurants: ContextItem(R.drawable.restaurant, R.string.restaurants_title, "restaurants")

    // Finance
    object Finance: ContextItem(R.drawable.savings, R.string.finance_title, "finance")
}