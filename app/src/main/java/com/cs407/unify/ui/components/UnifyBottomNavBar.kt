package com.cs407.unify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cs407.unify.ui.theme.AppTheme

enum class BottomTab { Feed, Search, Post, Market, Profile }

@Composable
fun UnifyBottomBar(
    current: BottomTab, // which tab is currently active
    onHome: () -> Unit, // runs when home icon is clicked
    onSearch: () -> Unit, // runs when search icon is clicked
    onPost: () -> Unit, // runs when post icon is clicked
    onMarket: () -> Unit, // runs when shopping cart icon is clicked
    onProfile: () -> Unit, // runs when profile icon is clicked
    modifier: Modifier = Modifier // allows customization of the bottom bar by the parent screen
){
    Column(
        modifier = modifier.fillMaxWidth(),
    ){
        HorizontalDivider(
            color = Color.Gray,
            thickness = 3.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.customColors.gradientTop.copy(alpha = 1f))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            NavIcon(current == BottomTab.Feed, Icons.Filled.Home, "Feed", onHome)
            NavIcon(current == BottomTab.Search, Icons.Outlined.Search, "Search", onSearch)
            NavIcon(current == BottomTab.Post, Icons.Filled.Add, "Post", onPost)
            NavIcon(current == BottomTab.Market, Icons.Filled.ShoppingCart, "Market", onMarket)
            NavIcon(current == BottomTab.Profile, Icons.Filled.Person, "Profile", onProfile)
        }

    }
}

// helper function to create icons
@Composable
private fun NavIcon(
    selected: Boolean,
    icon: ImageVector,
    contentDesc: String,
    onClick: () -> Unit
){
    val size = if (selected) 50.dp else 25.dp // selected page icon is bigger
    val tint = if (selected) Color.Black else Color.Gray // selected page icon is black instead of gray

    IconButton(onClick = onClick){
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = tint,
            modifier = Modifier.size(size)
        )
    }
}