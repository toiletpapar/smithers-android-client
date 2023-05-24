package com.example.budgeting_client.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.budgeting_client.R

sealed class ContextItem (
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    val route: String,
) {
    object Manga: ContextItem(R.drawable.book, R.string.manga_title, "manga")
    object Health: ContextItem(R.drawable.health, R.string.health_title, "health")
    object Cooking: ContextItem(R.drawable.recipes, R.string.cooking_title, "cooking")
    object Restaurants: ContextItem(R.drawable.restaurant, R.string.restaurants_title, "restaurants")
    object Finance: ContextItem(R.drawable.savings, R.string.finance_title, "finance")
}