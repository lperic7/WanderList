package com.example.wanderlist.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Casino
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Wishlist : BottomNavItem("wishlist", "Wishlist", Icons.Filled.Favorite)
    object Visited : BottomNavItem("visited", "Posjećeno", Icons.Filled.Star)
    object Random : BottomNavItem("random", "Iznenadi me", Icons.Filled.Casino)
    object Map : BottomNavItem("map", "Karta", Icons.Filled.Map)
}

val bottomNavItems = listOf(
    BottomNavItem.Wishlist,
    BottomNavItem.Visited,
    BottomNavItem.Random,
    BottomNavItem.Map
)